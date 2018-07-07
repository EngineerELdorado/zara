package com.zara.Zara.utils;
import com.zara.Zara.constants.Keys;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountNumberGenerator implements IdentifierGenerator {



    private String defaultPrefix = Keys.ACCOUNT_NUMBER_PREFIX;
    private int defaultNumber = 1;

    @Override
    public Serializable generate(SharedSessionContractImplementor  session, Object arg1)
            throws HibernateException {
        String userId = "";
        String digits = "";
        Connection con = session.connection();
        try {
            java.sql.PreparedStatement pst = con
                    .prepareStatement("select * from app_user order by account_number desc limit 1");
            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                userId = rs.getString("account_number");
                //System.out.println(userId);
                String prefix = userId.substring(0, 3);
                String str[] = userId.split(prefix);
                digits = String.format("%04d", Integer.parseInt(str[1]) + 1);
                userId = prefix.concat(digits);
            } else {
                digits = String.format("%04d", defaultNumber);
                userId = defaultPrefix.concat(digits);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }


}
