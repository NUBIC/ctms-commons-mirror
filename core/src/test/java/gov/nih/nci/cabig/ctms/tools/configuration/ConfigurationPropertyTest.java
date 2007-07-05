package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

/**
 * @author Rhett Sutphin
 */
public class ConfigurationPropertyTest extends CommonsTestCase {
    public void testGetName() throws Exception {
        assertEquals("Outgoing mail (SMTP) server name", ExampleConfiguration.SMTP_HOST.getName());
    }
    
    public void testGetDescription() throws Exception {
        assertEquals("The port on which to communicate with the SMTP server",
            ExampleConfiguration.SMTP_PORT.getDescription());
    }
}
