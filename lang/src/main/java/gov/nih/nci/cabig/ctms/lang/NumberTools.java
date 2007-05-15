package gov.nih.nci.cabig.ctms.lang;

/**
 * Utility methods for dealing with numbers.
 *
 * Derived from NU's core-commons library (MathUtils, there).
 *
 * @author Rhett Sutphin
 */
public class NumberTools {
    public static int countDigits(long value) {
        if (value < 0)
            throw new IllegalArgumentException("Only works on nonnegative integers");
        if (value == 0)
            return 1;
        else
            return (int) Math.ceil(
                Math.log(value + 1) / Math.log(10)
            );
    }

    private NumberTools() { }
}
