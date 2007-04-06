package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class TabTest extends TestCase {
    private Tab<?> tab = new Tab("Long title", "Short title", "View");

    public void testDefaultRefData() throws Exception {
        assertNotNull("Default refdata null", tab.referenceData());
        assertEquals("Default refdata not empty", 0, tab.referenceData().size());
    }

    public void testDefaultTarget() throws Exception {
        tab.setNumber(5);
        assertEquals("Default target not next", 6, tab.getTargetNumber());
    }
}
