package gov.nih.nci.cabig.ctms.lang;

import java.util.regex.Pattern;

/**
 * Specialized utility functions for dealing with {@link String}s.  If the function you need
 * isn't here, consider {@link org.apache.commons.lang.StringUtils} before adding it.  This
 * class shouldn't overlap with that one.
 * <p>
 * Derived from NU's core-commons library (StringUtils, there).
 *
 * @author Rhett Sutphin
 */
public class StringTools {
    public static final Pattern NEWLINE_PATTERN = Pattern.compile("(\r\n)|\n|\r");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Returns a copy of the the given string with the whitespace normalized.
     *
     * @param toNormalize
     * @return A whitespace-normalized copy
     * @see #normalizeWhitespace(StringBuffer)
     */
    public static String normalizeWhitespace(String toNormalize) {
        if (toNormalize == null) return null;
        return normalizeWhitespace(new StringBuffer(toNormalize)).substring(0);
    }

    /**
     * Normalizes the whitespace in the given buffer in-place.
     * This means that the whitespace is stripped from the head and the tail
     * and any contiguous stretches of whitespace are converted into a single
     * space.
     *
     * @param toNormalize
     * @return the passed-in buffer
     */
    public static StringBuffer normalizeWhitespace(StringBuffer toNormalize) {
        if (toNormalize == null) return null;
        // start with this value == true to completely remove leading whitespace
        boolean prevIsWhitespace = true;
        for (int i = 0; i < toNormalize.length(); i++) {
            if (Character.isWhitespace(toNormalize.charAt(i))) {
                if (prevIsWhitespace) {
                    toNormalize.deleteCharAt(i);
                    i--;
                } else {
                    toNormalize.setCharAt(i, ' ');
                    prevIsWhitespace = true;
                }
            } else {
                prevIsWhitespace = false;
            }
        }

        // remove (at most) one trailing ' '
        if (toNormalize.length() > 0 && toNormalize.charAt(toNormalize.length() - 1) == ' ')
            toNormalize.deleteCharAt(toNormalize.length() - 1);
        return toNormalize;
    }

    public static String augmentWithLineNumbers(String source) {
        return augmentWithLineNumbers(source, 1);
    }

    public static String augmentWithLineNumbers(String source, int first) {
        String[] lines = NEWLINE_PATTERN.split(source);

        int maxLineNumber = lines.length + first - 1;
        int digitsInMaxLineNumber = NumberTools.countDigits(maxLineNumber);

        StringBuffer augmented = new StringBuffer();
        int lineNum = first;
        for (String line : lines) {
            appendWithPadding(Long.toString(lineNum), digitsInMaxLineNumber, true, augmented)
                .append(": ").append(line).append('\n');
            lineNum++;
        }
        return augmented.toString();
    }

    public static StringBuffer appendWithPadding(String value, int width, boolean alignRight, StringBuffer buffer) {
        int digits = value.length();
        if (alignRight) {
            for (int i = 0; i < (width - digits); i++) {
                buffer.append(' ');
            }
        }
        buffer.append(value);
        if (!alignRight) {
            for (int i = 0; i < (width - digits); i++) {
                buffer.append(' ');
            }
        }
        return buffer;
    }

    public static int findMatchingCloseCharacter(String s, int openIndex, char openChar, char closeChar) {
        if (s.charAt(openIndex) != openChar) {
            throw new IllegalArgumentException("Character at openIndex is not " + openChar + ": " + s.charAt(openIndex));
        }
        int currentlyOpen = 0;
        int nextOpenIndex = openIndex;
        int nextCloseIndex = Integer.MAX_VALUE;
        int examineNext = openIndex;
        while (nextOpenIndex < s.length()) {
            if (s.charAt(examineNext) == closeChar) {
                currentlyOpen--;
            } else {
                currentlyOpen++;
            }

            if (currentlyOpen == 0) {
                break;
            } else {
                nextOpenIndex = s.indexOf(openChar, examineNext + 1);
                nextCloseIndex = s.indexOf(closeChar, examineNext + 1);

                examineNext = Math.min(nextCloseIndex >= 0 ? nextCloseIndex : Integer.MAX_VALUE,
                        nextOpenIndex >= 0 ? nextOpenIndex : Integer.MAX_VALUE);

                if (examineNext == Integer.MAX_VALUE) {
                    break;
                }
            }
        }

        if (currentlyOpen == 0) {
            return nextCloseIndex;
        } else {
            return -1;
        }
    }

    public static String createFullFieldName(String rootFieldName, String fieldName) {
        if (org.apache.commons.lang.StringUtils.isBlank(rootFieldName)) return fieldName;
        if (org.apache.commons.lang.StringUtils.isBlank(fieldName)) return rootFieldName;
        return new StringBuffer(rootFieldName).append('.').append(fieldName).toString();
    }

    public static String toHex(byte[] array, char sep) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            byte b = array[i];
            appendWithPadding(Integer.toHexString(0xff & b), 2, true, sb);
            if (i != array.length - 1) sb.append(sep);
        }
        return sb.toString();
    }

    public static String fromUnderscoredToCamelback(String underscored) {
        StringBuffer camelback = new StringBuffer();
        String[] parts = org.apache.commons.lang.StringUtils.split(underscored.toLowerCase(), '_');
        for (int i = 0; i < parts.length; i++) {
            camelback.append(i > 0 ? org.apache.commons.lang.StringUtils.capitalize(parts[i]) : parts[i]);
        }
        return camelback.toString();
    }

    /**
     * Creates an appropriately-pluralized string containing the count concatentated with
     * the word.  For example <code>createCountString(1, "dog")</code> gives <code>"1 dog"</code>.
     * <code>createCountString(5, "hare")</code> gives <code>"5 hares"</code>.
     * <p>
     * Does not currently have any knowledge of non-default english plurals
     * (e.g., fish, mice, flies).
     */
    public static String createCountString(int count, String word) {
        StringBuffer result = new StringBuffer().append(count).append(' ').append(word);
        if (count != 1) result.append('s');
        return result.toString();
    }

    private StringTools() { }
}
