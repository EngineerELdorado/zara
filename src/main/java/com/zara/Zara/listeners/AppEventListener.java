package com.zara.Zara.listeners;


import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.IRoleService;
import com.zara.Zara.services.ITransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;


@Component
public class AppEventListener implements CommandLineRunner {

    @Autowired
    IRoleService roleService;

    @Autowired
    ITransactionService transactionService;

    Logger LOGGER = LogManager.getLogger(AppEventListener.class);

    @Override
    public void run(String... args) {
        LOGGER.info("............PESAPAY BACKEND HAS STARTED...........");

        Collection<PesapayTransaction> transactions = transactionService.getAll();
        int count =0;
        for(PesapayTransaction transaction: transactions){

            BigDecimal amount = transaction.getFinalAmount();
            BigDecimal charges = amount.multiply(new BigDecimal("5")).divide(
                    new BigDecimal("100")
            );
            transaction.setOriginalAmount(amount);
            BigDecimal sent = amount.subtract(charges);
            transaction.setFinalAmount(sent);
            transaction.setCharges(charges);
            transactionService.addTransaction(transaction);
            LOGGER.info("UPDATED TRANSACTION "+ count +"\n originalAmount :"+amount
            +"\n charges: "+charges+" \n finalAmount: "+sent);
        }

    }




}
