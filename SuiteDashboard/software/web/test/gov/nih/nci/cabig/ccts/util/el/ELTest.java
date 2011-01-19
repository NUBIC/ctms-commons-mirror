package gov.nih.nci.cabig.ccts.util.el;

import junit.framework.TestCase;

public class ELTest extends TestCase {

    public void testEL() {
        EL el = new EL();
        assertEquals("true", el.evaluate("${true && true}"));
        assertEquals("true", el.evaluate("${true || false}"));
        assertEquals("true", el.evaluate("${false || true}"));
        assertEquals("false", el.evaluate("${false && true}"));
        assertEquals("false", el.evaluate("${false && false}"));
        assertEquals("3", el.evaluate("${1 + 2}"));
        assertEquals("7", el.evaluate("${2 * 2 + 2 + 1}"));
        assertEquals("9", el.evaluate("${2 * (2 + 2) + 1}"));
        assertEquals("4.0", el.evaluate("${2 * (2 + 6) / 4}"));
    }

}
