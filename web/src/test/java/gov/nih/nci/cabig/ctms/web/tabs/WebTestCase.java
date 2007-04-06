package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rhett Sutphin
 */
public abstract class WebTestCase extends TestCase {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
}
