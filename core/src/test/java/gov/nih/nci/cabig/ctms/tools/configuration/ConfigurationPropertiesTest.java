package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

/**
 * @author Rhett Sutphin
 */
public class ConfigurationPropertiesTest extends CommonsTestCase {
    public void testEmptyPropertiesIsEmptyWithoutErrors() throws Exception {
        ConfigurationProperties empty = ConfigurationProperties.empty();
        assertEquals(0, empty.size());
        assertNull(empty.get("anything"));
        assertNull(empty.getNameFor("anything"));
    }

    public void testAssertEmptyPropertiesFailsOnAdd() throws Exception {
        try {
            ConfigurationProperties.empty().add(new ConfigurationProperty.Int("some"));
            fail("Exception not thrown");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }
}
