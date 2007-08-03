package gov.nih.nci.cabig.ctms.maven.tomcat;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class ManagerResponseTest extends TestCase {
    public void testCreateRawWithBody() throws Exception {
        ManagerResponse<String> actual = ManagerResponse.createRawResponse(
            "OK - Everything's super\n" +
            "this is some more info"
        );
        assertTrue(actual.isOK());
        assertEquals("Everything's super", actual.getStatusMessage());
        assertEquals("this is some more info", actual.getPayload());
    }

    public void testCreateRawNoBody() throws Exception {
        ManagerResponse<String> actual = ManagerResponse.createRawResponse(
            "OK - Everything's super"
        );
        assertTrue(actual.isOK());
        assertEquals("Everything's super", actual.getStatusMessage());
        assertNull(actual.getPayload());
    }

    public void testIsOK() throws Exception {
        ManagerResponse<Object> actual = new ManagerResponse<Object>("OK - Roger");
        assertTrue(actual.isOK());
    }
    
    public void testFailure() throws Exception {
        ManagerResponse<Object> actual = new ManagerResponse<Object>("FAIL - Mayday");
        assertFalse(actual.isOK());
    }
}
