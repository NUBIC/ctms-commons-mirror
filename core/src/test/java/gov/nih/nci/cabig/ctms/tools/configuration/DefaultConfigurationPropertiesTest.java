package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.testing.CommonsCoreTestCase;

import java.util.Properties;

/**
 * @author Rhett Sutphin
 */
public class DefaultConfigurationPropertiesTest extends CommonsCoreTestCase {
    public void testEmptyPropertiesIsEmptyWithoutErrors() throws Exception {
        ConfigurationProperties empty = DefaultConfigurationProperties.empty();
        assertEquals(0, empty.size());
        assertNull(empty.get("anything"));
        assertNull(empty.getNameFor("anything"));
    }

    public void testAssertEmptyPropertiesFailsOnAdd() throws Exception {
        try {
            DefaultConfigurationProperties.empty().add(new DefaultConfigurationProperty.Int("some"));
            fail("Exception not thrown");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    public void testUnionContainsAllProperties() throws Exception {
        DefaultConfigurationProperty.Bool goodProp = new DefaultConfigurationProperty.Bool("good");
        DefaultConfigurationProperties a = new DefaultConfigurationProperties(new Properties());
        a.add(goodProp);
        ConfigurationProperties b = new ExampleConfiguration().getProperties();

        ConfigurationProperties aAndB = DefaultConfigurationProperties.union(a, b);
        assertTrue("Missing property from a", aAndB.containsKey("good"));
        assertTrue("Missing property from b", aAndB.containsKey(ExampleConfiguration.SMTP_HOST.getKey()));
        assertEquals("Wrong number of properties", 4, aAndB.size());

        assertNotSame("Configuration properties not cloned", goodProp, aAndB.get("good"));
    }

    public void testUnionPreservesDetails() throws Exception {
        DefaultConfigurationProperty.Bool goodProp = new DefaultConfigurationProperty.Bool("good");
        DefaultConfigurationProperties a = new DefaultConfigurationProperties(new Properties());
        a.add(goodProp);
        ConfigurationProperties b = new ExampleConfiguration().getProperties();

        ConfigurationProperties aAndB = DefaultConfigurationProperties.union(a, b);
        ConfigurationProperty<?> newSmtpHost = null, newSmtpPort = null;
        for (ConfigurationProperty<?> property : aAndB.getAll()) {
            if (property.getKey().equals(ExampleConfiguration.SMTP_HOST.getKey())) {
                newSmtpHost = property;
            } else if (property.getKey().equals(ExampleConfiguration.SMTP_PORT.getKey())) {
                newSmtpPort = property;
            }
        }
        assertNotNull(newSmtpHost);
        assertNotNull(newSmtpPort);
        assertEquals("Description not preseved in union",
            "The name or IP address for the SMTP server to use for e-mail notifications",
            newSmtpHost.getDescription());
        assertEquals("Default not preseved in union",
            25, newSmtpPort.getDefault());
    }
}
