package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

import java.util.Properties;

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

    public void testUnionContainsAllProperties() throws Exception {
        ConfigurationProperty.Bool goodProp = new ConfigurationProperty.Bool("good");
        ConfigurationProperties a = new ConfigurationProperties(new Properties());
        a.add(goodProp);
        ConfigurationProperties b = new ExampleConfiguration().getProperties();

        ConfigurationProperties aAndB = ConfigurationProperties.union(a, b);
        assertTrue("Missing property from a", aAndB.containsKey("good"));
        assertTrue("Missing property from b", aAndB.containsKey(ExampleConfiguration.SMTP_HOST.getKey()));
        assertEquals("Wrong number of properties", 4, aAndB.size());

        assertNotSame("Configuration properties not cloned", goodProp, aAndB.get("good"));
    }

    public void testUnionPreservesDetails() throws Exception {
        ConfigurationProperty.Bool goodProp = new ConfigurationProperty.Bool("good");
        ConfigurationProperties a = new ConfigurationProperties(new Properties());
        a.add(goodProp);
        ConfigurationProperties b = new ExampleConfiguration().getProperties();

        ConfigurationProperties aAndB = ConfigurationProperties.union(a, b);
        assertEquals("Description not preseved in union",
            "The name or IP address for the SMTP server to use for e-mail notifications",
            aAndB.getDescriptionFor(ExampleConfiguration.SMTP_HOST.getKey()));
        assertEquals("Default not preseved in union",
            "25",
            aAndB.getStoredDefaultFor(ExampleConfiguration.SMTP_PORT.getKey()));
    }
}
