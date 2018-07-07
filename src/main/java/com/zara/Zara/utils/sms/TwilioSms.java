package com.zara.Zara.utils.sms;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.zara.Zara.constants.Keys.TWILIO_ACCOUNT_SID;
import static com.zara.Zara.constants.Keys.TWILIO_AUTH_TOKEN;
import static com.zara.Zara.constants.Keys.TWILIO_NUMBER;

public class TwilioSms {

   public static Logger LOGGER = LogManager.getLogger(TwilioSms.class);

    public static void sendSMS(String receiverPhoneNumber, String body) {
        try {
            TwilioRestClient client = new TwilioRestClient(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);

            // Build a filter for the MessageList
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Body", body));
            params.add(new BasicNameValuePair("To", receiverPhoneNumber)); //Add real number here
            params.add(new BasicNameValuePair("From", TWILIO_NUMBER));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            Message message = messageFactory.create(params);
            LOGGER.info("................ THE SMS HAS BEEN SENT................ ");
        }
        catch (TwilioRestException e) {
            LOGGER.info(".........SMS ERROR: "+ e.getErrorMessage());
        }
    }
}
