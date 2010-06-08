package gov.nih.nci.cabig.ctms.testing;

import gov.nih.nci.cabig.ctms.lang.ComparisonTools;
import gov.nih.nci.cabig.ctms.lang.DateTools;
import static junit.framework.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Comparator;

/**
 * Additional useful assertions for use with junit.
 *
 * Derived from <code>CoreTestCase</code> in NU's core-commons library
 *
 * @author Rhett Sutphin
 */
public class MoreJUnitAssertions {
    public static void assertNotEquals(Object expected, Object actual) {
        assertNotEquals(null, expected, actual);
    }

    public static void assertNotEquals(String message, Object expected, Object actual) {
        assertFalse(prependMessage(message) + expected + " is equal to " + actual,
                expected == null ? actual == null : expected.equals(actual));
    }

    public static void assertEqualsAndNotSame(Object expected, Object actual) {
        assertEqualsAndNotSame(null, expected, actual);
    }

    public static void assertEqualsAndNotSame(String message, Object expected, Object actual) {
        assertEquals(prependMessage(message), expected, actual);
        assertNotSame(prependMessage(message), expected, actual);
    }

    public static void assertEqualArrays(Object[] expected, Object[] actual) {
        assertEqualArrays(null, expected, actual);
    }

    public static void assertEqualArrays(String message, Object[] expected, Object[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertEquals(prependMessage(message + "Mismatch at index " + i), expected[i], actual[i]);
        }
    }

    public static void assertContains(String message, String str, String subStr) {
        assertTrue(prependMessage(message) + str + " must contain value " + subStr, str.indexOf(subStr) >= 0);
    }

    public static void assertContains(String str, String subStr) {
        assertContains(null, str, subStr);
    }

    public static void assertNotContains(String message, String str, String subStr) {
        assertTrue(prependMessage(message) + str + " must not contain value " + subStr, str.indexOf(subStr) == -1);
    }

    public static void assertNotContains(String str, String subStr) {
        assertNotContains(null, str, subStr);
    }

    public static <T> void assertContains(Collection<T> c, T o) {
        assertContains(null, c, o);
    }

    public static <T> void assertContains(String message, Collection<T> c, T o) {
        assertTrue(prependMessage(message) + ' ' + c + " must contain " + o, c.contains(o));
    }

    public static <T, U extends T> void assertContains(Collection<T> c, Collection<U> subCollection) {
        assertContains(null, c, subCollection);
    }

    public static <T, U extends T> void assertContains(String message, Collection<T> c, Collection<U> subCollection) {
        assertTrue(prependMessage(message) + ' ' + c + " must contain all elements of " + subCollection, c.containsAll(subCollection));
    }

    public static <T> void assertNotContains(Collection<T> c, T o) {
        assertNotContains(null, c, o);
    }

    public static <T> void assertNotContains(String message, Collection<T> c, T o) {
        assertFalse(prependMessage(message) + ' ' + c + " must not contain " + o, c.contains(o));
    }

    public static <K> void assertContainsKey(Map<K, ?> m, K key) {
        assertContainsKey(null, m, key);
    }

    public static <K> void assertContainsKey(String message, Map<K, ?> m, K key) {
        assertTrue(prependMessage(message) + ' ' + m + " must contain key " + key, m.containsKey(key));
    }

    public static <K, V> void assertContainsPair(Map<K, V> m, K key, V value) {
        assertContainsPair(null, m, key, value);
    }

    public static <K, V> void assertContainsPair(String message, Map<K, V> m, K key, V value) {
        assertContainsKey(message, m, key);
        assertEquals(prependMessage(message) + ' ' + m + " must contain key-value pair (" + key + " => " + value + ')', value, m.get(key));
    }

    public static <K, V, L extends K, W extends V> void assertContains(Map<K, V> container, Map<L, W> containee) {
        assertContains(null, container, containee);
    }

    public static <K, V, L extends K, W extends V> void assertContains(String message, Map<K, V> container, Map<L, W> containee) {
        for (Map.Entry<L, W> entry : containee.entrySet()) {
            boolean containsKey = container.containsKey(entry.getKey());
            boolean keyHasCorrectValue = ComparisonTools.nullSafeEquals(container.get(entry.getKey()), entry.getValue());
            if (!(containsKey && keyHasCorrectValue)) {
                fail(prependMessage(message) + entry.getKey() + " => " + entry.getValue() + " not in " + container);
            }
        }
    }

    public static void assertPositive(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not positive", value > 0);
    }

    public static void assertPositive(long value) {
        assertPositive(null, value);
    }

    public static void assertNonnegative(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not nonnegative", value >= 0);
    }

    public static void assertNonnegative(long value) {
        assertNonnegative(null, value);
    }

    public static void assertNonpositive(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not nonpositive", value <= 0);
    }

    public static void assertNonpositive(long value) {
        assertNonpositive(null, value);
    }

    public static void assertNegative(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not negative", value < 0);
    }

    public static void assertNegative(long value) {
        assertNegative(null, value);
    }

    public static <T extends Comparable<T>> void assertOrder(T first, T second) {
        assertOrder((String) null, first, second);
    }

    public static <T extends Comparable<T>> void assertOrder(String message, T first, T second) {
        assertNegative(message, first.compareTo(second));
        assertPositive(message, second.compareTo(first));
    }

    public static <T> void assertOrder(Comparator<T> comparator, T first, T second) {
        assertOrder(null, comparator, first, second);
    }

    public static <T> void assertOrder(String message, Comparator<T> comparator, T first, T second) {
        assertNegative(message, comparator.compare(first, second));
        assertPositive(message, comparator.compare(second, first));
    }

    public static void assertDatesClose(Date expected, Date actual, long marginInMs) {
        assertDatesClose(null, expected, actual, marginInMs);
    }

    public static void assertDatesClose(String message, Date expected, Date actual, long marginInMs) {
        long difference = Math.abs(expected.getTime() - actual.getTime());
        assertTrue(prependMessage(message) + "Dates not within " + marginInMs +
                " of one another; expected=" + expected + " actual=" + actual, difference < marginInMs);
    }

    public static void assertDayOfDate(int expectedYear, int expectedMonth, int expectedDayOfMonth, Date actualDate) {
        assertDayOfDate(null, expectedYear, expectedMonth, expectedDayOfMonth, actualDate);
    }

    public static void assertDayOfDate(String message, int expectedYear, int expectedMonth, int expectedDayOfMonth, Date actualDate) {
        assertSameDay(message, DateTools.createDate(expectedYear, expectedMonth, expectedDayOfMonth), actualDate);
    }

    public static void assertSameDay(Date expectedDay, Date actualDay) {
        assertSameDay(null, expectedDay, actualDay);
    }

    public static void assertSameDay(String message, Date expectedDay, Date actualDay) {
        assertTrue(prependMessage(message) + "Dates not on the same day; expected=" + expectedDay + " actual=" + actualDay,
                DateTools.daysEqual(expectedDay, actualDay));
    }

    public static void assertTimeOfDate(String message, int hour, int minute, int second, int ms, Date actual) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(actual);
        assertEquals(prependMessage(message) + "Hours not equal", hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(prependMessage(message) + "Minutes not equal", minute, cal.get(Calendar.MINUTE));
        assertEquals(prependMessage(message) + "Seconds not equal", second, cal.get(Calendar.SECOND));
        assertEquals(prependMessage(message) + "Milliseconds not equal", ms, cal.get(Calendar.MILLISECOND));
    }

    public static String prependMessage(String message) {
        return (message == null ? "" : message + ": ");
    }
}
