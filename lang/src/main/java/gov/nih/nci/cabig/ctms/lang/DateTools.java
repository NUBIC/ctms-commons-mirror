package gov.nih.nci.cabig.ctms.lang;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;

/**
 * Utility methods for creating and comparing dates.
 *
 * Derived from NU's core-commons library (DateUtils, there).
 *
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class DateTools {
    public static final int DEFAULT_HOUR_OF_DAY = 12;
    public static final int DEFAULT_MINUTE = 0;
    public static final int DEFAULT_SECOND = 0;
    public static final int DEFAULT_MILLISECOND = 0;
    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;

    public static Timestamp createTimestamp(int year, int month, int day) {
        return new Timestamp(createDate(year, month, day).getTime());
    }

    public static Timestamp createTimestamp(int year, int month, int day, int hour, int minute, int second) {
        return new Timestamp(createDate(year, month, day, hour, minute, second).getTime());
    }

    public static Date createDate(int year, int month, int day) {
        return createCalendar(year, month, day).getTime();
    }

    public static Date createDate(int year, int month, int day, int hour, int minute, int second) {
        return createCalendar(year, month, day, hour, minute, second).getTime();
    }

    public static java.sql.Date createSqlDate(int year, int month, int day) {
        return createSqlDate(year, month, day, DEFAULT_HOUR_OF_DAY, DEFAULT_MINUTE, DEFAULT_SECOND);
    }

    public static java.sql.Date createSqlDate(int year, int month, int day, int hour, int minute, int second) {
        return new java.sql.Date(createCalendar(year, month, day, hour, minute, second).getTimeInMillis());
    }

    public static Calendar createCalendar(int year, int month, int day) {
        return createCalendar(year, month, day, DEFAULT_HOUR_OF_DAY, DEFAULT_MINUTE, DEFAULT_SECOND);
    }

    public static Calendar createCalendar(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, DEFAULT_MILLISECOND);
        return calendar;
    }

    // TODO: this method will not always return correct results if the two dates were constructed
    // TODO: in different timezones
    //// Example:
    ////   date1=2004-01-01 01:00 UTC
    ////   date2=2004-01-01 23:00 CST (UTC -0600)
    ////   date2 is on 2004-01-02 in UTC
    public static boolean daysEqual(Date date1, Date date2) {
        if (date1 == null) {
            return date2 == null;
        } else if (date2 == null) {
            return false;
        } else {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return (
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
        }
    }

    public static boolean daysEqual(Date date, int year, int month, int day) {
        return daysEqual(date, createDate(year, month, day));
    }

    //// TODO: this method is not aware of daylight savings time
    public static long differenceInDays(Date early, Date late) {
        long ms1 = zeroTime(early).getTime();
        long ms2 = zeroTime(late).getTime();
        return (ms2 - ms1) / MILLISECONDS_PER_DAY;
    }

    public static Date zeroTime(Date date) {
        if (date == null) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
