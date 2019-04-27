package com.zara.Zara.constants;

import com.zara.Zara.utils.GenerateRandomStuff;

public class Keys {

    public static String RESPONSE_MESSAGE="response_message";
    public static String ACCOUNT_NUMBER_PREFIX= GenerateRandomStuff.getRandomString(3).toUpperCase();
    public static String TRANSACTION_NUMBER_PREFIX=
            GenerateRandomStuff.getRandomString(2).toUpperCase()
                    +String.valueOf( GenerateRandomStuff.getRandomNumber(10)).toUpperCase();

}
