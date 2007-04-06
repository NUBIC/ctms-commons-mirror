package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class FlowTest extends TestCase {
    private Flow<Tab<?>> flow = new Flow<Tab<?>>("Test flow");

    public void testAddTabSetsNumber() throws Exception {
        flow.addTab(new Tab("Zero", "0", null));
        flow.addTab(new Tab("One",  "1", null));
        flow.addTab(new Tab("Two",  "2", null));
        assertEquals(0, (int) flow.getTab(0).getNumber());
        assertEquals(1, (int) flow.getTab(1).getNumber());
        assertEquals(2, (int) flow.getTab(2).getNumber());
    }
}
