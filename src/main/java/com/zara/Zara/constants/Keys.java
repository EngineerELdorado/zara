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

    public static String MOVE_SMS_URL="https://sms.movesms.co.ke/api/";
    public static String MOVE_SMS_SENDER_ID="SMARTLINK";
    public static String MOVE_SMS_USERNAME="DenisEldorado";
    public static String MOVE_SMS_API_KEY="mZt0gTtslM9JZC8Cp6PGrZu4UBpVuPmiaZccQhI23RmwXo3eU6";
}
