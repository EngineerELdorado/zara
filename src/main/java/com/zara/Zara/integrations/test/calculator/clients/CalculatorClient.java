package com.zara.Zara.integrations.test.calculator.clients;

import com.zara.Zara.integrations.test.calculator.*;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Service
public class CalculatorClient extends WebServiceGatewaySupport {

    public AddResponse addTwoNumbers(int a, int b) {

        Add add = new Add();
        add.setIntA(a);
        add.setIntB(b);

        return (AddResponse) getWebServiceTemplate().marshalSendAndReceive(add);
    }

    public DivideResponse divideTwoNumbers(int a, int b) {

        Divide divide = new Divide();
        divide.setIntA(a);
        divide.setIntB(b);

        return (DivideResponse) getWebServiceTemplate().marshalSendAndReceive(divide);
    }

    public MultiplyResponse multiplyTwoNumbers(int a, int b) {

        Multiply multiply = new Multiply();
        multiply.setIntA(a);
        multiply.setIntB(b);

        return (MultiplyResponse) getWebServiceTemplate().marshalSendAndReceive(multiply);
    }

    public SubtractResponse subtractTwoNumbers(int a, int b) {

        Subtract subtract = new Subtract();
        subtract.setIntA(a);
        subtract.setIntB(b);

        return (SubtractResponse) getWebServiceTemplate().marshalSendAndReceive(subtract);
    }
}
