package gov.nih.nci.cabig.ctms.lang;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class StringToolsTest extends TestCase {
    public void testNormalizeString() {
        String ws = "   Lots o'\n whitespace\t\t";
        assertEquals("Lots o' whitespace", StringTools.normalizeWhitespace(ws));
        String noWs = "ABCDEF";
        assertEquals("ABCDEF", StringTools.normalizeWhitespace(noWs));
    }

    public void testNormalizeStringBuffer() {
        StringBuffer ws = new StringBuffer("Gallons of \t\t\t\t\t inter\nsticial whitespace");
        StringBuffer norm = StringTools.normalizeWhitespace(ws);
        assertSame(ws, norm);
        assertEquals("Gallons of inter sticial whitespace", ws.toString());
    }

    public void testLineNumbering() {
        String s =
                "A\n" +
                "B\n" +
                "C\n" +
                "D\r\n" +
                "E\n" +
                "F\n" +
                "G\n" +
                "H\n" +
                "I\n" +
                "J\n";
        String numbered = StringTools.augmentWithLineNumbers(s, 1);
        assertEquals(" 1: A\n" +
                " 2: B\n" +
                " 3: C\n" +
                " 4: D\n" +
                " 5: E\n" +
                " 6: F\n" +
                " 7: G\n" +
                " 8: H\n" +
                " 9: I\n" +
                "10: J\n", numbered);
    }

    public void testFindMatchingCloseChar() {
        String balanced = "()(()((())()))";
        String unbalancedOpen = "(()(())((()(()))";
        String unbalancedClose = "(((())())))";

        assertMatchedParen("()", 0, 1);
        assertMatchedParen("(", 0, -1);

        assertMatchedParen(balanced, 0, 1);
        assertMatchedParen(balanced, 2, 13);
        assertMatchedParen(balanced, 3, 4);
        assertMatchedParen(balanced, 5, 12);
        assertMatchedParen(balanced, 6, 9);
        assertMatchedParen(balanced, 7, 8);
        assertMatchedParen(balanced, 10, 11);

        assertMatchedParen(unbalancedOpen, 0, -1);
        assertMatchedParen(unbalancedOpen, 1, 2);
        assertMatchedParen(unbalancedOpen, 3, 6);
        assertMatchedParen(unbalancedOpen, 4, 5);
        assertMatchedParen(unbalancedOpen, 7, -1);
        assertMatchedParen(unbalancedOpen, 8, 15);
        assertMatchedParen(unbalancedOpen, 9, 10);
        assertMatchedParen(unbalancedOpen, 11, 14);
        assertMatchedParen(unbalancedOpen, 12, 13);

        assertMatchedParen(unbalancedClose, 0, 9);
        assertMatchedParen(unbalancedClose, 1, 8);
        assertMatchedParen(unbalancedClose, 2, 5);
        assertMatchedParen(unbalancedClose, 3, 4);
        assertMatchedParen(unbalancedClose, 6, 7);
    }

    private void assertMatchedParen(String s, int openIndex, int closeIndex) {
        assertEquals('(', s.charAt(openIndex));
        if (closeIndex >= 0) {
            assertEquals(')', s.charAt(closeIndex));
        }
        assertEquals(closeIndex, StringTools.findMatchingCloseCharacter(s, openIndex, '(', ')'));
    }

    public void testFromUnderscoredToCamelback() {
        assertEquals("weanOrUnpackDate", StringTools.fromUnderscoredToCamelback("WEAN_or_UNPACK_DATE"));
        assertEquals("underscoreless", StringTools.fromUnderscoredToCamelback("underscORELESS"));
    }

    public void testPluralizeAddsSIfCountIsNotOne() {
        assertEquals("2 cows", StringTools.createCountString(2, "cow"));
        assertEquals("1 pig", StringTools.createCountString(1, "pig"));
        assertEquals("0 birds", StringTools.createCountString(0, "bird"));
    }
}
