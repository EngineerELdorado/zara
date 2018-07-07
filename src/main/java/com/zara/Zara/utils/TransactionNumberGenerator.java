package com.zara.Zara.utils;
import com.zara.Zara.constants.Keys;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionNumberGenerator implements IdentifierGenerator {



    private String defaultPrefix = Keys.TRANSACTION_NUMBER_PREFIX;
    private int defaultNumber = 1;

    @Override
    public Serializable generate(SharedSessionContractImplementor  session, Object arg1)
            throws HibernateException {
        String transaction_number = "";
        String digits = "";
        Connection con = session.connection();
        try {
            java.sql.PreparedStatement pst = con
                    .prepareStatement("select * from transaction order by transaction_number desc limit 1");
            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                transaction_number = rs.getString("transaction_number");
                //System.out.println(userId);
                String prefix = transaction_number.substring(0, 3);
                String str[] = transaction_number.split(prefix);
                digits = String.format("%04d", Integer.parseInt(str[1]) + 1);
                transaction_number =prefix.concat(digits);
            } else {
                digits = String.format("%04d", defaultNumber);
                transaction_number = defaultPrefix.concat(digits);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction_number;
    }


}
