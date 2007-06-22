package gov.nih.nci.cabig.ctms.lang;

import org.apache.commons.collections15.comparators.NullComparator;
import org.apache.commons.collections15.comparators.ComparableComparator;

import java.util.Comparator;

/**
 * Utilities for comparing objects, both for equality and for ordering.
 *
 * Derived from NU's core-commons library (ComparisonTools, there).
 *
 * @see Comparator
 * @see Comparable
 *
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class ComparisonTools {
    private static <T> NullComparator<T> nullsLowComparator() {
        return new NullComparator<T>(ComparableComparator.<T>getInstance(), false);
    }

    public static <T> int nullSafeCompare(Comparable<T> one, Comparable<T> two) {
        return nullsLowComparator().compare(one, two);
    }

    public static boolean nullSafeEquals(Object one, Object two) {
        return nullSafeInspect(one, two, EqualityInspector.INSTANCE);
    }

    /* TODO: might want to move this into core module later
    public static boolean nullSafeEqualsById(WithReadOnlyLongId one, WithReadOnlyLongId two) {
        return nullSafeInspect(one, two, true, false, ByIdBooleanInspector.INSTANCE);
    }

    private static class ByIdBooleanInspector implements BooleanInspector<> {
        private static final BooleanInspector INSTANCE = new ByIdBooleanInspector();
        public boolean inspect(Object one, Object two) {
            return nullSafeEquals(((WithReadOnlyLongId) one).getId(), ((WithReadOnlyLongId) two).getId());
        }
    }
    */

    public static <T> boolean nullSafeInspect(T one, T two, BooleanInspector<T> inspector) {
        return nullSafeInspect(one, two, true, false, inspector);
    }

    public static <T> boolean nullSafeInspect(T one, T two, boolean ifBothNull, boolean ifExactlyOneNull, BooleanInspector<T> inspector) {
        if (one == null) {
            if (two == null) return ifBothNull;
            else return ifExactlyOneNull;
        } else {
            if (two == null) return ifExactlyOneNull;
            else return inspector.inspect(one, two);
        }
    }

    public static interface BooleanInspector<T> {
        boolean inspect(T one, T two);
    }

    private static class EqualityInspector implements BooleanInspector<Object> {
        public static final EqualityInspector INSTANCE = new EqualityInspector();

        public boolean inspect(Object one, Object two) {
            return one.equals(two);
        }
    }

    private ComparisonTools() { }
}
