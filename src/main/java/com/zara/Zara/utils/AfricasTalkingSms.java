package com.zara.Zara.utils;

import com.zara.Zara.utils.sms.AfricasTalkingGateway;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.zara.Zara.constants.Keys.AFRICAS_TALKING_KEY;
import static com.zara.Zara.constants.Keys.AFRICAS_TALKING_USERNAME;

public class AfricasTalkingSms {

    public static String username= AFRICAS_TALKING_USERNAME;
    public static String apiKey = AFRICAS_TALKING_KEY;

    public static void sendSms(String receivers, String body){

        AfricasTalkingGateway gateway  = new AfricasTalkingGateway(username, apiKey);

        try {
            JSONArray results = gateway.sendMessage(receivers, body);
            for( int i = 0; i < results.length(); ++i ) {
                JSONObject result = results.getJSONObject(i);
                System.out.print(result.getString("status") + ","); // status is either "Success" or "error message"
                System.out.print(result.getString("statusCode") + ",");
                System.out.print(result.getString("number") + ",");
                System.out.print(result.getString("messageId") + ",");
                System.out.println(result.getString("cost"));
            }
        } catch (Exception e) {
            System.out.println("Encountered an error while sending " + e.getMessage());
        }
    }
}
