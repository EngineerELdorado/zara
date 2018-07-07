package com.zara.Zara.constants;

import com.zara.Zara.utils.GenerateRandomStuff;

public class Keys {

    public static String RESPONSE_CODE="response_code";
    public static String RESPONSE_SUCCESS="00";
    public static String RESPONSE_FAILURE="01";
    public static String RESPONSE_MESSAGE="response_message";
    public static String ACCOUNT_NUMBER_PREFIX= GenerateRandomStuff.getRandomString(3).toUpperCase();
    public static String TRANSACTION_NUMBER_PREFIX=
            GenerateRandomStuff.getRandomString(2).toUpperCase()
                    +String.valueOf( GenerateRandomStuff.getRandomNumber(10)).toUpperCase();

    public static final String TWILIO_ACCOUNT_SID = "AC22eab31833b47b1ba1bd10b989d18845";
    public static final String TWILIO_AUTH_TOKEN = "c9d6524d81a85a83d0e0e3b84e4d9fd7";
    public static final String TWILIO_NUMBER = "+254203892394";

    public static final String AFRICAS_TALKING_USERNAME="Sandbox";
    public static final String AFRICAS_TALKING_KEY="4cc0d9391b4958b664626fe16cbe996bc27539b799bdde05f4710ca12b24f400";
}
