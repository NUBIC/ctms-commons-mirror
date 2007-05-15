package gov.nih.nci.cabig.ctms.web.taglibs;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;
import java.util.Collection;
import java.util.Iterator;

import gov.nih.nci.cabig.ctms.lang.StringTools;

/**
 * Small functions which are useful in views but are not provided by standard libraries (JSTL, etc.).
 * <p>
 * Derived from NU's core-commons library.
 *
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class Functions {
    private Functions() { }

    public static String capitalize(String text) {
        if (org.apache.commons.lang.StringUtils.isBlank(text)) return text;
        if (text.length()==1) return text.toUpperCase();
        return new StringBuffer(text.substring(0, 1).toUpperCase()).append(text.substring(1)).toString();
    }

    public static String countString(int count, String word) {
        return StringTools.createCountString(count, word);
    }

    public static String parity(int i) {
        return (i % 2 == 0) ? "even" : "odd";
    }

    public static String newlinesToXhtmlBr(String text) {
        return newlinesToBr(text, "<br />");
    }

    public static String newlinesToHtmlBr(String text) {
        return newlinesToBr(text, "<br>");
    }

    private static String newlinesToBr(String text, String brTag) {
        if (text == null) return null;
        Pattern newlinePattern = StringTools.NEWLINE_PATTERN;
        if (newlinePattern.matcher(text).matches()) {
            return brTag + '\n';
        } else {
            String[] lines = newlinePattern.split(text);
            if (lines.length == 1) {
                return text;
            } else {
                boolean endsWithNl = newlinePattern.matcher(text).find(text.length() - 1);
                StringBuffer reassembled = new StringBuffer(text.length() + lines.length * brTag.length());
                for (int i = 0; i < lines.length; i++) {
                    reassembled.append(lines[i]);
                    if (endsWithNl || i < lines.length - 1) {
                        reassembled.append(brTag).append('\n');
                    }
                }
                return reassembled.toString();
            }
        }
    }

    public static String collapseIntoDisjointRangesString(Collection<Integer> list) {
        if (list == null) return null;
        if (list.size() == 0) return "";
        Iterator<Integer> iterator = list.iterator();
        StringBuffer sb = new StringBuffer();
        Integer rangeStart = null;
        Integer last = iterator.next();
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            if (i == last + 1) {
                if (rangeStart == null) {
                    rangeStart = last;
                    sb.append(last);
                }
            } else {
                appendRangeEnd(rangeStart, sb, last);
                sb.append(", ");
                rangeStart = null;
            }
            last = i;
        }
        appendRangeEnd(rangeStart, sb, last);
        return sb.toString();
    }

    private static void appendRangeEnd(Integer rangeStart, StringBuffer sb, Integer rangeEnd) {
        if (rangeStart != null) {
            if (rangeEnd - rangeStart > 1) {
                sb.append('-');
            } else {
                sb.append(", ");
            }
        }
        sb.append(rangeEnd);
    }

    public static String zeropad(int number, int length) {
        return zeropad(new Integer(number), length);
    }

    public static String zeropad(Number number, int length) {
        if (number == null) return "";
        return String.format("%0" + length + 'd', number);
    }

    public static String classname(Object o) {
        return o == null ? null : o.getClass().getName();
    }

    public static String join(Collection<?> elements, String glue) {
        return StringUtils.join(elements, glue);
    }

    public static Boolean contains(Collection<?> collection, Object obj) {
        return collection == null ? Boolean.FALSE : collection.contains(obj);
    }
}
