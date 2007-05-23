package gov.nih.nci.cabig.ctms.lang;

import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;

import junit.framework.TestCase;

/**
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class DateToolsTest extends TestCase {
    public void testCreateDate() {
        Calendar toTest = Calendar.getInstance();
        toTest.setTime(DateTools.createDate(2003, Calendar.NOVEMBER, 15));
        assertEquals(2003, toTest.get(Calendar.YEAR));
        assertEquals(Calendar.NOVEMBER, toTest.get(Calendar.MONTH));
        assertEquals(15, toTest.get(Calendar.DATE));
        assertEquals(Calendar.PM, toTest.get(Calendar.AM_PM));
        assertEquals(12, toTest.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, toTest.get(Calendar.MINUTE));
        assertEquals(0, toTest.get(Calendar.SECOND));
        assertEquals(0, toTest.get(Calendar.MILLISECOND));
    }

    public void testCreateTimestampWithHourMinuteSecond() {
        Timestamp toTest = DateTools.createTimestamp(2003, Calendar.JANUARY, 5, 6, 7, 8);
        Calendar testable = Calendar.getInstance();
        testable.setTimeInMillis(toTest.getTime());
        assertEquals(2003, testable.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, testable.get(Calendar.MONTH));
        assertEquals(5, testable.get(Calendar.DATE));
        assertEquals(Calendar.AM, testable.get(Calendar.AM_PM));
        assertEquals(6, testable.get(Calendar.HOUR_OF_DAY));
        assertEquals(7, testable.get(Calendar.MINUTE));
        assertEquals(8, testable.get(Calendar.SECOND));
        assertEquals(DateTools.DEFAULT_MILLISECOND, testable.get(Calendar.MILLISECOND));
    }

    public void testDaysEqualWhenEqual() {
        Calendar cal1 = DateTools.createCalendar(2003, 1, 2);
        cal1.set(Calendar.HOUR, 2);
        Calendar cal2 = DateTools.createCalendar(2003, 1, 2);
        cal1.set(Calendar.HOUR, 3);
        assertTrue(DateTools.daysEqual(cal1.getTime(), cal2.getTime()));
        assertTrue(DateTools.daysEqual(cal1.getTime(),
            cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH)));
    }

    public void testDaysEqualWhenUnequal() {
        Date d1= DateTools.createDate(2003, 1, 2);
        int d2Year = 2003;
        int d2Month = 2;
        int d2Day = 2;
        Date d2= DateTools.createDate(d2Year, d2Month, d2Day);
        assertFalse(DateTools.daysEqual(d1, d2));
        assertFalse(DateTools.daysEqual(d1, d2Year, d2Month, d2Day));
    }

    public void testDaysEqualWhenFirstNull() {
        assertFalse(DateTools.daysEqual(null, DateTools.createDate(2003, 1, 2)));
        assertFalse(DateTools.daysEqual(null, 2003, 1, 2));
    }

    public void testDaysEqualWhenSecondNull() {
        assertFalse(DateTools.daysEqual(DateTools.createDate(2003, 1, 2), null));
    }

    public void testDaysEqualWhenBothNull() {
        assertTrue(DateTools.daysEqual(null, null));
    }

    public void testDaysDifferent() throws Exception {
        assertEquals(4,
            DateTools.differenceInDays(
                DateTools.createDate(2000, Calendar.MARCH, 28),
                DateTools.createDate(2000, Calendar.APRIL,  1)
            )
        );
        assertEquals(369,
            DateTools.differenceInDays(
                DateTools.createDate(2002, Calendar.MARCH, 28),
                DateTools.createDate(2003, Calendar.APRIL,  1)
            )
        );
        assertEquals(369,
            DateTools.differenceInDays(
                DateTools.createDate(2000, Calendar.MAY,  28),
                DateTools.createDate(2001, Calendar.JUNE,  1)
            )
        );
        assertEquals(0,
            DateTools.differenceInDays(
                DateTools.createDate(2000, Calendar.MARCH, 28, 13, 14, 15),
                DateTools.createDate(2000, Calendar.MARCH, 28, 15, 16, 17)
            )
        );
        assertEquals(-4,
            DateTools.differenceInDays(
                DateTools.createDate(2000, Calendar.APRIL,  1),
                DateTools.createDate(2000, Calendar.MARCH, 28)
            )
        );
    }
}
