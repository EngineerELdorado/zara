package com.zara.Zara.specs;

import com.zara.Zara.dtos.responses.StatsResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class StatsSpecs {

    public StatsResource findStats(EntityManager entityManager, Long accountId, LocalDateTime startDate, LocalDateTime endDate) {

        String query = "SELECT" +
                " COUNT(t)            AS numberOfTransactions, " +
                " SUM(t.sender_amount)   AS totalSent " +

                " FROM transactions t " +
                " LEFT JOIN accounts a ON t.sender_account_id = a.id " +
                " WHERE t.sender_account_id =:accountId " +
                " AND t.created_at BETWEEN :startDate and :endDate ";


        if (accountId != null) {
            query = query + " AND a.id =:accountId";
        }

        Query nativeQuery = entityManager.createNativeQuery(query, Tuple.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("accountId", accountId);

        Tuple sentTuple = (Tuple) nativeQuery.getSingleResult();

        StatsResource statsResource = new StatsResource();
        statsResource.setTotalSent(sentTuple.get("totalSent", BigDecimal.class));
        statsResource.setNumberOfSentTransactions(sentTuple.get("numberOfTransactions", BigInteger.class).longValue());
        return statsResource;
    }


}
