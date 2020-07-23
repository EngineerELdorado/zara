package com.zara.Zara.utils;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class PeriodUtil {

    public static DateTime START_OF_TODAY = new DateTime().withTimeAtStartOfDay();
    public static DateTime END_OF_TODAY = START_OF_TODAY.plusDays(1).withTimeAtStartOfDay().minusSeconds(1);

    public static DateTime START_OF_YESTERDAY = START_OF_TODAY.minusDays(1).withTimeAtStartOfDay();
    public static DateTime END_OF_YESTERDAY = START_OF_TODAY.minusSeconds(1);

    public static DateTime START_OF_LAST_WEEK = START_OF_TODAY.minusWeeks(1).withTimeAtStartOfDay();
    public static DateTime END_OF_LAST_WEEK = START_OF_LAST_WEEK.plusWeeks(1).minusSeconds(1);

    public static DateTime START_OF_WEEK = new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY)
            .toDateTimeAtStartOfDay().withTimeAtStartOfDay();

    public static DateTime END_OF_WEEK = new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY)
            .toDateTimeAtStartOfDay().withTimeAtStartOfDay().plusDays(1).minusSeconds(1);

    public static DateTime START_OF_LAST_MONTH = new DateMidnight().withDayOfMonth(1).toDateTime().minusMonths(1);
    public static DateTime END_OF_LAST_MONTH = START_OF_LAST_MONTH.plusMonths(1).minusSeconds(1);

    public static DateTime START_OF_MONTH = new DateMidnight().withDayOfMonth(1).toDateTime();
    public static DateTime END_OF_MONTH = START_OF_MONTH.plusMonths(1).minusSeconds(1);

    public static DateTime START_OF_YEAR = new LocalDate().withMonthOfYear(DateTimeConstants.JANUARY)
            .withDayOfMonth(1).toDateTimeAtStartOfDay().withTimeAtStartOfDay();

    public static DateTime END_OF_YEAR = START_OF_YEAR.plusYears(1).minusSeconds(1);

    public static DateTime START_OF_LAST_YEAR = new DateMidnight().withDayOfMonth(1).toDateTime().minusYears(1)
            .withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(1);

    public static DateTime END_OF_LAST_YEAR = START_OF_LAST_YEAR.plusYears(1).minusSeconds(1);

    public static DateTime BEGINNING_OF_TIME = new DateTime().withYear(1970);
    public static DateTime CURRENT_TIME = new DateTime();
}
