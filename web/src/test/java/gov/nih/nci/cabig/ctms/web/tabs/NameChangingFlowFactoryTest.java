package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class NameChangingFlowFactoryTest extends TestCase {
    NameChangingFlowFactory<String> factory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factory = new TestFactory();
    }

    public void testTabsCopiedIntoResult() throws Exception {
        factory.addTab(new SimpleTestTab<String>(1));
        factory.addTab(new SimpleTestTab<String>(2));
        factory.addTab(new SimpleTestTab<String>(3));

        Flow<String> actual = factory.createFlow("DC");
        assertEquals(3, actual.getTabCount());
        assertEquals("Tab 2", actual.getTabs().get(1).getLongTitle());
    }

    public void testResultHasCorrectName() throws Exception {
        Flow<String> actual = factory.createFlow("Woo: hoo");
        assertEquals("Woo: hoo", actual.getName());
    }

    private static class TestFactory extends NameChangingFlowFactory<String> {
        protected String createName(String command) {
            return command;
        }
    }
}
