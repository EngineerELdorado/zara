package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_WITHDRAWAL;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_WITHDRAWAL;

@RestController
@RequestMapping("/withdrawals")
@CrossOrigin(origins = "*")
public class WithdrawalController {


    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    BigDecimal agentCommission=new BigDecimal("1.0");
    BigDecimal pesapayCharges;
    BigDecimal agentFinalAmount;

    BigDecimal originalAmount,charges,finalAmount;
    @Autowired
    IAgentService agentService;
    Logger LOGGER = LogManager.getLogger(WithdrawalController.class);

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {
        originalAmount =new BigDecimal(request.getAmount());
        charges = originalAmount.multiply(new BigDecimal(PERCENTAGE_ON_WITHDRAWAL))
                .divide(new BigDecimal("100"));
        finalAmount = originalAmount;
        Agent agent = agentService.findByAgentNumber(request.getReceiver());
        Customer customer = customerService.findByPhoneNumber(request.getSender());
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero agent n'existe pas");
            LOGGER.info("AGENT NUMBER NOT FOUND FOR "+ request.getReceiver());
        }else if (!agent.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Cet compte agent est bloquE");
            LOGGER.info("ACCOUNT NOT ACTIVE FOR AGENT "+agent.getAgentNumber());
        }else if (!agent.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Agent non verifiE");
            LOGGER.info("ACCOUNT NOT VERIFIED FOR AGENT "+agent.getAgentNumber());
        }
        else if (customer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero de client n'existe pas");
            LOGGER.info("CUSTOMER ACCOUNT NOT FOUND");
        }
        else if (!customer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du client n'est pas actif\n "+customer.getStatusDescription());
            LOGGER.info("CUSTOMER ACCOUNT NOT ACTIVE");
        }else if (!customer.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du client n'est pas encore verifie\n "+customer.getStatusDescription());
            LOGGER.info("CUSTOMER ACCOUNT NOT VERIFIED");
        }else if (!bCryptPasswordEncoder.matches(request.getPin(), customer.getPin())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Pin Incorrect");
            LOGGER.info("INCORRECT PIN FOR CUSTOMER "+ agent.getAgentNumber());
        }
        else if (customer.getBalance().compareTo(originalAmount.add(charges))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde Insuffisant ");
            LOGGER.info("CUSTOMER BALANCE INSUFFICIENT FOR CUSTOMER "+customer.getFullName());
            Sms sms = new Sms();
            sms.setTo(customer.getPhoneNumber());
            sms.setMessage("Votre solde est insuffisant pour retirer "+originalAmount+" USD et supporter les frais de retrait. vous avez actuellement "+customer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD." +
                    "Il vous faut au moins "+originalAmount.add(charges)+"USD pour effectuer cette transaction");
            SmsService.sendSms(sms);
        }else{
            PesapayTransaction transaction = new PesapayTransaction();
            agentFinalAmount = new BigDecimal(request.getAmount()).subtract(agentCommission);
            transaction.setFinalAmount(agentFinalAmount);
            transaction.setCreatedOn(new Date());
            transaction.setCreationDate(System.currentTimeMillis());
            transaction.setStatus("00");
            transaction.setDescription("Withdrawal successful");
            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
            transaction.setCreatedByCustomer(customer);
            transaction.setReceivedByAgent(agent);
            transaction.setSender(customer.getFullName());
            transaction.setReceiver(agent.getFullName());
            transaction.setTransactionType(TRANSACTION_WITHDRAWAL);

            PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
            if (createdTransaction==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("ECHEC");
                LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
            }else {

                customer.setBalance(customer.getBalance().subtract(new BigDecimal(request.getAmount())));
                Customer updatedCustomer = customerService.save(customer);

                Sms sms1 = new Sms();
                sms1.setTo(customer.getPhoneNumber());
                sms1.setMessage(customer.getFullName()+ " vous venez de retirer de votre compte "+ originalAmount +"USD au numero agent "+agent.getAgentNumber()+" "+agent.getFullName()+" via PesaPay. "+
                        " les frais de transactions on ete de "+ charges+"USD.  type de transaction RETRAIT DIRECT. votre solde actuel est "+updatedCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms1);
                agent.setCommission(agent.getCommission().add(agentCommission));
                agent.setBalance(agent.getBalance().add(new BigDecimal(request.getAmount())));
                Agent updatedAgent = agentService.save(agent);
                Sms sms2 = new Sms();
                sms2.setTo(agent.getPhoneNumber());
                sms2.setMessage(agent.getFullName()+ " vous venez de recevoir "+request.getAmount()+" USD venant de "+customer.getFullName()+" via PesaPay. "+
                        " type de transaction RETRAIT DIRECT. votre solde actuel est "+updatedAgent.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber()+" commission obtenue "+agentCommission+" USD");
                SmsService.sendSms(sms2);



                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());

            }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping("/business/post")
    public ResponseEntity<?>businessPost(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {

        Agent agent = agentService.findByAgentNumber(request.getReceiver());
        Business business = businessService.findByBusinessNumber(request.getSender());
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero agent n'existe pas");
            LOGGER.info("AGENT NUMBER NOT FOUND FOR "+ request.getReceiver());
        }else if (!agent.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Cet compte agent est bloquE");
            LOGGER.info("ACCOUNT NOT ACTIVE FOR AGENT "+agent.getAgentNumber());
        }else if (!agent.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Agent non verifiE");
            LOGGER.info("ACCOUNT NOT VERIFIED FOR AGENT "+agent.getAgentNumber());
        }
        else if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero de client n'existe pas");
            LOGGER.info("CUSTOMER ACCOUNT NOT FOUND");
        }
        else if (!business.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du client n'est pas actif\n "+business.getStatusDescription());
            LOGGER.info("CUSTOMER ACCOUNT NOT ACTIVE");
        }else if (!business.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du client n'est pas encore verifie\n "+business.getStatusDescription());
            LOGGER.info("CUSTOMER ACCOUNT NOT VERIFIED");
        }else if (!bCryptPasswordEncoder.matches(request.getPin(), business.getPassword())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Pin Incorrect");
            LOGGER.info("INCORRECT PIN FOR CUSTOMER "+ agent.getAgentNumber());
        }
        else if (business.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde Insuffisant "+business.getBalance()+" USD");
            LOGGER.info("CUSTOMER BALANCE INSUFFICIENT FOR CUSTOMER "+business.getBusinessName());
        }else{
            agentFinalAmount = new BigDecimal(request.getAmount()).subtract(agentCommission);
            PesapayTransaction transaction = new PesapayTransaction();
            transaction.setFinalAmount(agentFinalAmount);
            transaction.setCreatedOn(new Date());
            transaction.setStatus("00");
            transaction.setDescription("Withdrawal successful");
            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
            transaction.setCreatedByBusiness(business);
            transaction.setReceivedByAgent(agent);
            transaction.setTransactionType(TRANSACTION_WITHDRAWAL);

            PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
            if (createdTransaction==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("ECHEC");
                LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
            }else {

                business.setBalance(business.getBalance().subtract(new BigDecimal(request.getAmount())));
                Business updatedbusiness = businessService.save(business);

                Sms sms1 = new Sms();
                sms1.setTo(business.getPhoneNumber());
                sms1.setMessage(business.getBusinessName()+ " vous venez de retirer de votre compte "+request.getAmount()+"USD au numero agent "+agent.getAgentNumber()+" "+agent.getFullName()+" via PesaPay. "+
                        " type de transaction RETRAIT DIRECT. votre solde actuel est "+updatedbusiness.getBalance()+" USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms1);

                agent.setCommission(agent.getCommission().add(agentCommission));
                agent.setBalance(agent.getBalance().add(new BigDecimal(request.getAmount())));
                Agent updatedAgent = agentService.save(agent);
                Sms sms2 = new Sms();
                sms2.setTo(agent.getPhoneNumber());
                sms2.setMessage(agent.getFullName()+ " vous venez de recevoir "+request.getAmount()+"USD venant de "+business.getBusinessName()+" via PesaPay. "+
                        " type de transaction RETRAIT DIRECT. votre solde actuel est "+updatedAgent.getBalance()+" USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms2);



                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());

            }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
