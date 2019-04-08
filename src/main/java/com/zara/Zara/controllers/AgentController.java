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

import static com.zara.Zara.constants.Responses.PHONE_NUMBER_ALREADY_TAKEN;
import static com.zara.Zara.constants.Responses.USER_REGISTRATION_SUCCESS;

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
    public ResponseEntity<?> save(@RequestBody Agent agent) throws UnsupportedEncodingException {

        agent.setPin(bCryptPasswordEncoder.encode(agent.getPin()));
        agent.setCreatedOn(new Date());
        agent.setVerified(true);
        agent.setStatus("ACTIVE");
        agent.setStatusDescription("the customer verified");
        agent.setAgentNumber(BusinessNumbersGenerator.generateAgentNumber(agentService));
        agent.setBalance(new BigDecimal("0"));
        if (!isPhoneTaken(agent.getPhoneNumber())){
            Agent savedAgent = agentService.save(agent);
            if (savedAgent!=null){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                apiResponse.setAgent(savedAgent);
                LOG.info("REGISTRATION SUCCESSFUL");
                Sms sms = new Sms();
                sms.setTo(savedAgent.getPhoneNumber());
                sms.setMessage("Cher "+agent.getFullName()+" Bievenu sur PesaPay. maintenant vous avez un compte agent. votre identifiant est "+savedAgent.getAgentNumber());
                SmsService.sendSms(sms);
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                LOG.info("REGISTRATION FAILED. BAD REQUEST PROBABLY");
            }
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage(PHONE_NUMBER_ALREADY_TAKEN);
            LOG.info("REGISTRATION FAILED. PHONE NUMBER ALREADY TAKEN");
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
        sms.setMessage("votre code de verification pour PesaPay est "+String.valueOf(otp));
        SmsService.sendSms(sms);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }
    @PostMapping("/validateOtp")
    public ResponseEntity <?> validateOtp(@RequestBody Agent otpObject) throws UnsupportedEncodingException {

//Validate the Otp
        if(Integer.parseInt(otpObject.getOtp()) >= 0){
            int serverOtp = otpService.getOtp(otpObject.getPhoneNumber());
            if(serverOtp > 0){
                if(Integer.parseInt(otpObject.getOtp()) == serverOtp){
                    otpService.clearOTP(otpObject.getPhoneNumber());
                    apiResponse.setResponseCode("00");
                    apiResponse.setResponseMessage("SUCCESS");

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

    @GetMapping("/findByPhoneNumber/{phoneNumber}")
    public ResponseEntity<?>findCustomberByPhoneNumber(@PathVariable String phoneNumber) throws UnsupportedEncodingException {
        if (!phoneNumber.startsWith("+")){
            phoneNumber= "+"+phoneNumber;
        }
        Agent existingAgent = agentService.findByPhoneNumber(phoneNumber);
        if (existingAgent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte PesaPay");
        }else {
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Success");
            apiResponse.setAgent(existingAgent);
        }
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/findByAgentNumber/{agentNumber}")
    public ResponseEntity<?>findAgentNumber(@PathVariable String agentNumber) throws UnsupportedEncodingException {
        Agent agent = agentService.findByAgentNumber(agentNumber);
        if (agent==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte PesaPay");
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
