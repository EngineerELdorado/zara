package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.IAgentService;
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

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_DEPOSIT;

@RestController
@RequestMapping("/deposits")
@CrossOrigin(origins = "*")
public class DepositController {


    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IAgentService agentService;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);

    BigDecimal agentCommission = new BigDecimal("1.0");
    BigDecimal pesaPayCharges;
    BigDecimal finalAmount;

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {

        Agent agent = agentService.findByAgentNumber(request.getSender());
        Customer customer = customerService.findByPhoneNumber(request.getReceiver());
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero agent n'existe pas");
            LOGGER.info("AGENT NUMBER NOT FOUND FOR "+ request.getSender());
        }else if (!agent.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Cet compte agent est bloquE");
            LOGGER.info("ACCOUNT NOT ACTIVE FOR AGENT "+agent.getAgentNumber());
        }else if (!agent.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Agent non verifiE");
            LOGGER.info("ACCOUNT NOT VERIFIED FOR AGENT "+agent.getAgentNumber());
        }else if (!bCryptPasswordEncoder.matches(request.getPin(), agent.getPin())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Pin Incorrect");
            LOGGER.info("INCORRECT PIN FOR AGENT "+ agent.getAgentNumber());
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
        }
        else if (agent.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde Insuffisant "+agent.getBalance()+" USD");
            LOGGER.info("AGENT BALANCE INSUFFICIENT FOR AGENT "+agent.getAgentNumber());
        }else{
            PesapayTransaction transaction = new PesapayTransaction();
            finalAmount = new BigDecimal(request.getAmount()).subtract(agentCommission);
            transaction.setFinalAmount(finalAmount);
            transaction.setCreatedOn(new Date());
            transaction.setCreationDate(System.currentTimeMillis());
            transaction.setStatus("00");
            transaction.setDescription("Deposit successful");
            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
            transaction.setReceivedByCustomer(customer);
            transaction.setCreatedByAgent(agent);
            transaction.setTransactionType(TRANSACTION_DEPOSIT);

            PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
            if (createdTransaction==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("ECHEC");
                LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
            }else {

                agent.setCommission(agent.getCommission().add(agentCommission));
                agent.setBalance(agent.getBalance().subtract(new BigDecimal(request.getAmount())));
                Agent updatedAgent = agentService.save(agent);
                Sms sms1 = new Sms();
                sms1.setTo(agent.getPhoneNumber());
                sms1.setMessage(agent.getFullName()+ " vous venez de transferer "+request.getAmount()+"USD A "+customer.getFullName()+" via PesaPay. "+
                " type de transaction DEPOT DIRECT. votre solde actuel est "+updatedAgent.getBalance().setScale(2,BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms1);

                customer.setBalance(customer.getBalance().add(new BigDecimal(request.getAmount())));
               Customer updatedCustomer = customerService.save(customer);

                Sms sms2 = new Sms();
                sms2.setTo(customer.getPhoneNumber());
                sms2.setMessage(customer.getFullName()+ " vous venez de recevoir "+request.getAmount()+"USD venant du numero agent "+agent.getAgentNumber()+" "+agent.getFullName()+" via PesaPay. "+
                        " type de transaction DEPOT DIRECT. votre solde actuel est "+updatedCustomer.getBalance().setScale(2,BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber()+" commission obtenue "+agentCommission);
                SmsService.sendSms(sms2);

                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());

            }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
