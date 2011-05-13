package gov.nih.nci.cabig.ctms.web;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import static org.easymock.EasyMock.expect;

/**
 * @author Rhett Sutphin
 */
public class WebToolsTest extends TestCase {
    private MockHttpServletRequest request;
    private MockHttpSession session;
    private static final Collection<String> EXPECTED_REQUEST_PROPERTIES = Arrays.asList(
            // From HttpServletRequest
        "authType", "contextPath", "method", "pathInfo", "pathTranslated", "queryString",
        "remoteUser", "requestedSessionId", "requestURL", "requestURI", "servletPath",
        "userPrincipal", "requestedSessionIdFromCookie", "requestedSessionIdFromURL",
        "requestedSessionIdValid",
        // From ServletRequest
        "characterEncoding", "contentLength", "contentType", "locale", "localAddr",
        "localName", "localPort", "protocol", "remoteAddr", "remoteHost", "remotePort",
        "scheme", "serverName", "serverPort", "secure",
        // From MockHttpServletRequest
        "active", "servletContext"
    );

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        request = new MockHttpServletRequest();
        request.setServerName("www.neuromice.org");
        request.setContextPath("/");

        session = new MockHttpSession();
    }

    public void testSessionAttributesToMap() {
        session.setAttribute("jimmy", "james");
        session.setAttribute("johnny", "johnson");
        session.setAttribute("wnyx", 585);

        SortedMap<String, Object> map = WebTools.sessionAttributesToMap(session);

        Set<String> keys = map.keySet();
        assertContains(keys, "jimmy");
        assertContains(keys, "johnny");
        assertContains(keys, "wnyx");

        assertEquals(map.get("jimmy"), "james");
        assertEquals(map.get("johnny"), "johnson");
        assertEquals(map.get("wnyx"), 585);

        // test order
        Iterator<String> keysIt = keys.iterator();
        assertEquals("jimmy",  keysIt.next());
        assertEquals("johnny", keysIt.next());
        assertEquals("wnyx",   keysIt.next());
    }

    public void testRequestPropertiesToMapIsNotMissingAnything() throws Exception {
        Map<String, Object> actual = WebTools.requestPropertiesToMap(request);

        Set<String> missing = new LinkedHashSet<String>(EXPECTED_REQUEST_PROPERTIES);
        for (String property : EXPECTED_REQUEST_PROPERTIES) {
            if (actual.containsKey(property)) missing.remove(property);
        }
        assertEquals("One or more expected properties missing: " + missing, 0, missing.size());
    }

    public void testRequestPropertiesToMapHasNoExtras() throws Exception {
        Map<String, Object> actual = WebTools.requestPropertiesToMap(request);

        Set<String> extra = new LinkedHashSet<String>(actual.keySet());
        for (String property : EXPECTED_REQUEST_PROPERTIES) {
            if (actual.containsKey(property)) extra.remove(property);
        }
        assertEquals("One or more extra properties in map: "+ extra, 0, extra.size());
    }

    public void testRequestPropertiesToMapValues() throws Exception {
        // spot checks
        Map<String, Object> actual = WebTools.requestPropertiesToMap(request);
        assertEquals(request.getServerName(), actual.get("serverName"));
        assertEquals(request.getContextPath(), actual.get("contextPath"));
    }

    public void testRequestPropertiesToMapWhenAccessingPropertyThrowsExceptionSuppressesException()
        throws Exception
    {
        HttpServletRequest mockRequest = EasyMock.createNiceMock(HttpServletRequest.class);
        expect(mockRequest.getRemoteHost()).andThrow(new IllegalStateException("I forgot"));
        EasyMock.replay(mockRequest);

        Map<String, Object> actual = WebTools.requestPropertiesToMap(mockRequest);
        assertTrue(actual.get("remoteHost") instanceof IllegalStateException);
    }

    public void testRequestAttributesToMap() {
        request.setAttribute("wnyx", 585);
        request.setAttribute("jimmy", "james");
        request.setAttribute("johnny", "johnson");

        SortedMap<String, Object> map = WebTools.requestAttributesToMap(request);

        Set<String> keys = map.keySet();
        assertContains(keys, "jimmy");
        assertContains(keys, "johnny");
        assertContains(keys, "wnyx");

        assertEquals(map.get("jimmy"), "james");
        assertEquals(map.get("johnny"), "johnson");
        assertEquals(map.get("wnyx"), 585);

        // test order
        Iterator<String> keysIt = keys.iterator();
        assertEquals("jimmy",  keysIt.next());
        assertEquals("johnny", keysIt.next());
        assertEquals("wnyx",   keysIt.next());
    }

    public void testHeadersToMap() throws Exception {
        request.addHeader("Content-Type", "text/plain");
        request.addHeader("X-Doubled", "1");
        request.addHeader("X-Doubled", "2");

        Map<String, String[]> actual = WebTools.headersToMap(request);
        assertEquals("Wrong number of entries", 2, actual.size());
        assertEquals(actual.get("Content-Type")[0], "text/plain");
        assertEquals(actual.get("X-Doubled")[0], "1");
        assertEquals(actual.get("X-Doubled")[1], "2");
    }

    // TODO: until CoreTestCase is moved into ctms-commons
    private void assertContains(Collection<?> actual, Object expected) {
        assertTrue("Collection does not contain " + expected + ": " + actual, actual.contains(expected));
    }
}
