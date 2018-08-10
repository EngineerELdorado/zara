package com.zara.Zara.utils;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.services.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
public class CheckingUtils {

   public static Logger LOGGER = LogManager.getLogger(CheckingUtils.class);


    public static boolean isPhoneNumberValid(String phoneNumber){
        LOGGER.info("input phone number for format validation"+phoneNumber);
        return phoneNumber.startsWith("+") && phoneNumber.length() ==13;
    }

    public static boolean isPhoneNumberTaken(String phoneNumber, IUserService userService){
        LOGGER.info("input phone number for availability"+phoneNumber);
        AppUser user =userService.findByPhoneNumber(phoneNumber);
        if(user!=null){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean isAccountVerified(String accountNumber, IUserService userService){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        return appUser.isVerified();
    }

    public static boolean isAccountLocked(String accountNumber, IUserService userService){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        return appUser.isLocked();
    }

    public static boolean doesAccountNeedToResetPin(String accountNumber, IUserService userService){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        return appUser.isNeedToChangePin();
    }

    //if phone number starts with 0 or +
    public static String formatPhoneNumber(String msisdn) {
        String start_char = String.valueOf(msisdn.charAt(0));

        int msisdn_length = msisdn.length();
        System.out.println("Starting char" + start_char);
        System.out.println("MSISDN Length" + msisdn_length);

        if (start_char.equals("+") && msisdn_length == 13) {
            msisdn = msisdn.substring(4);
        } else if (start_char.equals("2") && msisdn_length == 12) {
            msisdn = msisdn.substring(3);
        } else if (start_char.equals("0") && msisdn_length == 10) {
            msisdn = msisdn.substring(1);
        } else if (start_char.equals("7") && msisdn_length == 9) {
            msisdn = msisdn;
        } else {
            return "0";
        }
        return msisdn;
    }
}
