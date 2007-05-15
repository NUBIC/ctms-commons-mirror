package gov.nih.nci.cabig.ctms.web.taglibs;

import junit.framework.TestCase;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Arrays;

/**
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class FunctionsTest extends TestCase {
    public void testCapitalizeIgnoresBlanks() {
        assertNull(Functions.capitalize(null));
        assertEquals("  ", Functions.capitalize("  "));
    }

    public void testCapitalizeSingleCharacter() {
        assertEquals("A", Functions.capitalize("a"));
    }

    public void testCapitalizeLongerText() {
        assertEquals("Mister lister", Functions.capitalize("mister lister"));
    }

    public void testParity() {
        assertEquals("0 not even", "even", Functions.parity(0));
        assertEquals("1 not odd", "odd", Functions.parity(1));
        assertEquals("2 not even", "even", Functions.parity(2));
    }

    public void testNewlineToHtmlBrIgnoresNulls() {
        assertNull(Functions.newlinesToXhtmlBr(null));
    }

    public void testNewlineToXhtmlBrWorks() {
        assertEquals("a b c<br />\nd e f", Functions.newlinesToXhtmlBr("a b c\nd e f"));
        assertEquals("a b c d e f", Functions.newlinesToXhtmlBr("a b c d e f"));
        assertEquals("<br />\na<br />\nb<br />\nc<br />\nd<br />\ne<br />\nf<br />\n",
            Functions.newlinesToXhtmlBr("\na\nb\nc\nd\ne\nf\n"));
        assertEquals("<br />\n", Functions.newlinesToXhtmlBr("\n"));
    }

    public void testNewlineToHtmlBrWorks() {
        assertEquals("a b c<br>\nd e f", Functions.newlinesToHtmlBr("a b c\nd e f"));
        assertEquals("a b c d e f", Functions.newlinesToHtmlBr("a b c d e f"));
        assertEquals("<br>\na<br>\nb<br>\nc<br>\nd<br>\ne<br>\nf<br>\n",
            Functions.newlinesToHtmlBr("\na\nb\nc\nd\ne\nf\n"));
        assertEquals("<br>\n", Functions.newlinesToHtmlBr("\n"));
    }
    
    public void testCollapseIntoDisjointRanges() {
        SortedSet<Integer> list = new TreeSet<Integer>();
        for (int i = 0 ; i < 8 ; i++) { list.add(i); }
        assertEquals("0-7", Functions.collapseIntoDisjointRangesString(list));
        list.remove(0);
        assertEquals("1-7", Functions.collapseIntoDisjointRangesString(list));
        list.remove(4);
        assertEquals("1-3, 5-7", Functions.collapseIntoDisjointRangesString(list));
        list.remove(5);
        assertEquals("1-3, 6, 7", Functions.collapseIntoDisjointRangesString(list));
        list.remove(2);
        assertEquals("1, 3, 6, 7", Functions.collapseIntoDisjointRangesString(list));
        list.add(9);
        list.add(8);
        assertEquals("1, 3, 6-9", Functions.collapseIntoDisjointRangesString(list));
        list.remove(3);
        list.add(2);
        assertEquals("1, 2, 6-9", Functions.collapseIntoDisjointRangesString(list));
    }

    public void testZeropadAddsZeroesToLeftHandSideIfLengthLessThanSecondArgument() {
        assertEquals("001234", Functions.zeropad(1234, 6));
        assertEquals("1234", Functions.zeropad(1234, 3));
        assertEquals("023", Functions.zeropad(new Integer(23), 3));
    }

    public void testZeropadNullIsBlank() {
        assertEquals("", Functions.zeropad(null, 5));
    }

    public void testJoin() {
        assertEquals(Functions.join(Arrays.asList("fee", "fie", "foe"), ","), "fee,fie,foe");
        assertEquals(Functions.join(Arrays.asList("fum"), ","), "fum");
        assertEquals(Functions.join(Arrays.asList(5L, 8L), "; "), "5; 8");
        assertEquals(Functions.join(Arrays.asList(5L, "zip", 8L), "; "), "5; zip; 8");
        assertEquals(Functions.join(Arrays.asList(5L, null, 8L), "; "), "5; ; 8");
    }
}
