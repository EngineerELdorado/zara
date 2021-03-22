package com.zara.Zara.utils;

import java.util.Random;

public class GenerateRandomStuff {

    public static int getRandomNumber(int limit)
    {

        Random rand = new Random();
        int value = rand.nextInt(limit);
        return value;
    }

    public static String getRandomString(int count) {

      final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}


