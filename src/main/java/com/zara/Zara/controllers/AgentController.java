package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.LoginObject;
import com.zara.Zara.models.OtpObject;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.utils.OtpService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import com.zara.Zara.utils.GenerateRandomStuff;
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
import java.util.Random;

@RestController
@RequestMapping("/agents")
public class AgentController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    IAgentService agentService;
    @Autowired
    OtpService otpService;
    String initials;
    String firstPart;
    String secondPart;
    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    Logger LOG = LogManager.getLogger(AgentController.class);
    @PostMapping("/post")
    public ResponseEntity<?>createAgent(@RequestBody Agent agent) throws UnsupportedEncodingException {


        agent.setAgentNumber(BusinessNumbersGenerator.generateAgentNumber(agentService));
        agent.setPin(bCryptPasswordEncoder.encode(agent.getPin()));
        agent.setStatus("ACTIVE");
        agent.setVerified(true);
        agent.setCreatedOn(new Date());
        agent.setBalance(new BigDecimal("0"));
        Agent createdAgent = agentService.save(agent);
        if (createdAgent!=null){
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Enregistrement Reussi");
            apiResponse.setAgent(createdAgent);
            sms.setTo(agent.getPhoneNumber());
            sms.setMessage(agent.getFullName()+" Bievenu sur Setting. vous avez maintenant un compte AGENT. votre numero agent" +
                    " est "+createdAgent.getAgentNumber());
            SmsService.sendSms(sms);


        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Enregistrement Echoue");
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginObject loginObject){
        Agent agent = agentService.findByPhoneNumber(loginObject.getPhoneNumber());
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("numero introuvable");
            LOG.info("LOGIN FAILED FOR==> "+loginObject.getPhoneNumber()+" PHONE NUMBER NOT FOUND");
        }else{
            if (bCryptPasswordEncoder.matches(loginObject.getPin(), agent.getPin())){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Bienvenu");
                apiResponse.setAgent(agent);
                LOG.info("LOGIN SUCCESSFUL FOR==> "+loginObject.getPhoneNumber()+" "+agent.getFullName());
            }else {
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("pin incorrect");
                LOG.info("LOGIN FAILED FOR==> "+loginObject.getPhoneNumber()+" PIN INCORRECT");
            }
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public boolean isPhoneTaken(String phoneNumber){
        Agent agent = agentService.findByPhoneNumber(phoneNumber);
        if (agent!=null){
            return true;
        }else {
            return false;
        }
    }

    @PostMapping("/generateOtp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpObject otpObject) throws UnsupportedEncodingException {
        int otp = otpService.generateOTP(otpObject.getPhoneNumber());
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("Otp successfully generated");
        Sms sms = new Sms();
        sms.setTo(otpObject.getPhoneNumber());
        sms.setMessage("votre code de verification pour Setting est "+String.valueOf(otp));
        SmsService.sendSms(sms);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }
    @PostMapping("/validateOtp")
    public ResponseEntity <?> validateOtp(@RequestBody OtpObject otpObject) throws UnsupportedEncodingException {

//Validate the Otp
        if(otpObject.getOtp() >= 0){
            int serverOtp = otpService.getOtp(otpObject.getPhoneNumber());
            if(serverOtp > 0){
                if(otpObject.getOtp() == serverOtp){

                    Agent agent = agentService.findByPhoneNumber(otpObject.getPhoneNumber());
                    if (agent!=null){
                        agent.setStatusDescription("numero de compte verifie");
                        agent.setVerified(true);
                        agent.setStatus("ACTIVE");
                        agentService.save(agent);
                        otpService.clearOTP(otpObject.getPhoneNumber());
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("SUCCESS");
                        apiResponse.setAgent(agent);
                        Sms sms = new Sms();
                        sms.setTo(otpObject.getPhoneNumber());
                        sms.setMessage("Cher "+agent.getFullName()+" Bievenu sur Setting. vous etes maintenant agent");
                        SmsService.sendSms(sms);
                    }else{
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("USER NOT FOUND");
                    }
                }else{
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage("WRONG OTP");
                }
            }else {
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("WRONG OTP");
            }
        }else {
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("WRONG OTP");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?>findAgentPhoneNumber(@RequestBody Agent agent1) throws UnsupportedEncodingException {
        Agent agent = agentService.findByPhoneNumber(agent1.getPhoneNumber());
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte Setting");
        }else if (!agent.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce compte n'est pas encore activE. "+agent1.getStatusDescription());


        }else {
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Success");
            apiResponse.setAgent(agent);
        }
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/findByAgentNumber/{agentNumber}")
    public ResponseEntity<?>findAgentNumber(@PathVariable String agentNumber) throws UnsupportedEncodingException {
        Agent agent = agentService.findByAgentNumber(agentNumber);
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte Setting");
        }else if (!agent.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce compte n'est pas encore activE. "+agent.getStatusDescription());


        }else {
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Success");
            apiResponse.setAgent(agent);
        }
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

}
