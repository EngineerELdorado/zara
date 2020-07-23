package com.zara.Zara.utils;

import java.util.Date;

public class DateManagementUtil {

    public static Date addDays(Date d, int days) {
        d.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);
        return d;
    }

    public static boolean hasExpired(Date expiryDate) {
        return expiryDate.before(new Date());
    }
}
