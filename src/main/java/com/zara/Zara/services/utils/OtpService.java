package com.zara.Zara.services.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
@Service
public class OtpService {


    //cache based on username and OPT MAX 8
    private static final Integer EXPIRE_MINS = 5;
    private LoadingCache<String, Integer> otpCache;
    public OtpService(){
        super();
        otpCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }
    //This method is used to push the opt number against Key. Rewrite the OTP if it exists
    //Using user id  as key
    public int generateOTP(String key){
        int len =4;

        int OTP;
        String numbers = "0123456789";
        Random rndm_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        OTP = Integer.parseInt(String.valueOf(otp));
        otpCache.put(key, OTP);
        return OTP;
    }
    public int getOtp(String key){
        try{
            return otpCache.get(key);
        }catch (Exception e){
            return 0;
        }
    }
    //This method is used to clear the OTP catched already
    public void clearOTP(String key){
        otpCache.invalidate(key);
    }
}
