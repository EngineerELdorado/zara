package com.zara.Zara.controllers;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_WITHDRAWAL;

@RestController
@RequestMapping("/ussd")
@CrossOrigin(origins = "*")
public class UssdController {

     @Autowired
     ICustomerService customerService;
     @Autowired
     IBusinessService businessService;
     @Autowired
     BCryptPasswordEncoder bCryptPasswordEncoder;
     @Autowired
     IAgentService agentService;
     @Autowired
     ITransactionService transactionService;
    BigDecimal agentCommission=new BigDecimal("1.0");
     @PostMapping("/process")
    public String ussdRequest(@RequestParam String sessionId,
                              @RequestParam String serviceCode,
                              @RequestParam String phoneNumber,
                              @RequestParam String text) throws UnsupportedEncodingException {
        String message="";
        String inputs[]=text.split("\\*");
        Customer customer= customerService.findByPhoneNumber(phoneNumber);
        if (customer==null){
            if (text.equals("") || text.length()==0){

                message = "CON Bienvenu sur PesaPay. veillez choisir une option\n\n" +
                        "1. Creer un compte\n" +
                        "2. Voir nos tarifications";
            }else if (text.equals("1")){
                // TODO: 26/04/2019 customer  registration
                message ="CON entrez votre nom et postnom";
            }else if (text.equals("2")){
                message ="END Nos tarifications:\n\n" +
                        "10$---100$: 1$\n" +
                        "100$---200$: 2$\n" +
                        "200$---300$: 3$\n" +
                        "300$---400$: 4$\n" +
                        "500---600$: 5$";
            }else if (inputs[0].equals("1") && !inputs[0].equals("")  && inputs.length==1){
                message ="CON Entrer votre nom et postnom";
            }
            else if (inputs[0].equals("1") && !inputs[1].equals("")  && inputs.length==2){
                message ="CON Creer un pin (4 chiffres)";
            }else if (inputs[0].equals("1") && !inputs[2].equals("")  && inputs.length==3){
                customer = new Customer();
                customer.setFullName(inputs[1]);
                customer.setPhoneNumber(phoneNumber);
                customer.setPin(bCryptPasswordEncoder.encode(inputs[2]));
                customer.setCreationDate(new Date());
                customer.setVerified(true);
                customer.setStatus("ACTIVE");
                customer.setStatusDescription("the customer verified");
                customer.setBalance(new BigDecimal("0"));
                customerService.save(customer);
                message ="END Votre compte vient d'etre creer. bienvenu sur PesaPay "+inputs[1];
                String sms ="Bievenu sur PesaPay. maintenant vous pouvez retirer deposer " +
                "payer en ligne transferer ainsi effectuer tout genre de payment avec votre telephone.";
                sendSms(inputs[1],phoneNumber,sms);
            }
        }else{
            if (customer.getStatus().equals("ACTIVE")){
                message = "END votre compte est bloquE veillez contacter le service client de Pesapay au 09987542365";
            }
           else if (text.equals("")){
                message ="CON Salut..."+customer.getFullName()+" Quelle operation voulez vous effectuer?\n" +
                        "1. Solde\n" +
                        "2. Retirer \n" +
                        "3. Envoyer \n" +
                        "4. Payer pour un achat\n" +
                        "5. Payer pour un service";
            }else if (text.equals("1")){
                message ="END votre solde actuel est de "+customer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD";
            }

            else if (text.equals("2")){
                message ="CON entrez le numero identifiant de l'agent";
            }else if (inputs[0].equals("2") && inputs.length==1){
                message ="CON entrez le montant à retirer";
            }
            else if (inputs[0].equals("2") && inputs.length==2){
                message ="CON entrez votre pin";
            }else if (inputs[0].equals("2") && inputs.length==3){
                Agent agent = agentService.findByAgentNumber(inputs[1]);
                if (agent==null){
                    message="END agent non trouvé";
                }else if (!agent.getStatus().equals("ACTIVE")){
                    message ="END le compte de cet agent n'est pas operationel";
                }
               else if (!bCryptPasswordEncoder.matches(inputs[3], customer.getPin())){
                    message ="END pin incorrect";
                }
               else if (customer.getBalance().compareTo(new BigDecimal(inputs[2]))<0){
                    message ="END volde insuffisant. votre compte a actuellement "+customer.getBalance().setScale(2, BigDecimal.ROUND_UP);
                }else{
                     processWithdrawal(phoneNumber, inputs[1],inputs[2]);
                }
            }
        }

        return message;
    }

    private void processWithdrawal(String phoneNumber, String agentNumber, String amnt) throws UnsupportedEncodingException {

        Customer customer = customerService.findByPhoneNumber(phoneNumber);
        Agent agent = agentService.findByAgentNumber(agentNumber);
        BigDecimal amount = new BigDecimal(amnt);

        PesapayTransaction transaction = new PesapayTransaction();
        BigDecimal finalAmount = amount.subtract(agentCommission);
        transaction.setAmount(finalAmount);
        transaction.setCreatedOn(new Date());
        transaction.setStatus("00");
        transaction.setDescription("Withdrawal successful");
        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
        transaction.setCreatedByCustomer(customer);
        transaction.setReceivedByAgent(agent);
        transaction.setTransactionType(TRANSACTION_WITHDRAWAL);

        PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);

            customer.setBalance(customer.getBalance().subtract(amount));
            Customer updatedCustomer = customerService.save(customer);

            String msg1= " vous venez de retirer de votre compte " + finalAmount + "USD au numero agent " + agent.getAgentNumber() + " " + agent.getFullName() + " via PesaPay. " +
                    " type de transaction RETRAIT DIRECT. votre solde actuel est " + updatedCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP) + " USD. numero de transaction " + transaction.getTransactionNumber();
            sendSms(customer.getFullName(), customer.getPhoneNumber(), msg1);
            agent.setCommission(agent.getCommission().add(agentCommission));
            agent.setBalance(agent.getBalance().add(amount));
            Agent updatedAgent = agentService.save(agent);

            String msg2= " vous venez de recevoir " + amount + " USD venant de " + customer.getFullName() + " via PesaPay. " +
                    " type de transaction RETRAIT DIRECT. votre solde actuel est " + updatedAgent.getBalance() + " USD. numero de transaction " + transaction.getTransactionNumber() + " commission obtenue " + agentCommission + " USD";

        sendSms(customer.getFullName(), customer.getPhoneNumber(), msg2);
    }

    public void sendSms(String name, String phone, String msg) throws UnsupportedEncodingException {
        Sms sms = new Sms();
        sms.setTo(phone);
        sms.setMessage("Cher "+name+" "+ msg);
        SmsService.sendSms(sms);
    }
}
