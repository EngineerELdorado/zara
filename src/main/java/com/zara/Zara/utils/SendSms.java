package com.zara.Zara.utils;
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.URL;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;

import static com.zara.Zara.constants.Keys.*;

@SuppressWarnings("restriction")
public class SendSms {

   static Logger LOGGER = LogManager.getLogger(SendSms.class);

    public static String send( String phonenumber,String msg){
        try{
            try{
                URL smslink = new
                        URL(MOVE_SMS_URL+"?action=compose&"+"username="+MOVE_SMS_USERNAME+"&api_key="+MOVE_SMS_API_KEY+"&sender="+MOVE_SMS_SENDER_ID+"&to="+phonenumber+"&message="+java.net.URLEncoder.encode(msg, "UTF-8")+"");
                BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        smslink.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);

                System.out.println("Sms send successfully!");
                LOGGER.info("Sms send successfully!");
                //System.out.println("Api: " +smslink);

                in.close();
            }catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("Sms Connection not Found");
                throw new BadCredentialsException("Sms Connection not Found");
            }
        }catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }


}
