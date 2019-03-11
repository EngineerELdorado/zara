package com.zara.Zara.services.banking;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.zara.Zara.models.ChargeRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.zara.Zara.constants.ConstantVariables.STRIPE_CLIENT_SECRET;
import static com.zara.Zara.constants.ConstantVariables.STRIPE_CLIENT_SECRET_TEST;

@Service
public class StripeService {

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_CLIENT_SECRET;
    }

    public Charge charge(ChargeRequest chargeRequest)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {

        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("source", chargeRequest.getStripeToken());
        Customer customer = Customer.create(customerParams);


        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("customer", customer.getId());
        return Charge.create(chargeParams);
    }
}
