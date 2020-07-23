package com.zara.Zara.utils;

import java.util.Random;

public class GenerateRandomStuffUtil {

    public static int getRandomNumber(int limit) {

        Random rand = new Random();
        return rand.nextInt(limit);
    }

    public static String getOfflineIdentifier() {
        return System.currentTimeMillis() + getRandomString(10).toUpperCase() + getRandomNumber(10000);

    }
    public static String getRandomString(int count) {

      final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static int generateOTP(){

        int len =4;
        String numbers = "0123456789";
        Random rndm_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return Integer.parseInt(String.valueOf(otp));
    }
}


