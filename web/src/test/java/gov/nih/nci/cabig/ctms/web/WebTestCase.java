package gov.nih.nci.cabig.ctms.web;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.Errors;
import org.springframework.validation.BindException;
import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

/**
 * @author Rhett Sutphin
 */
public abstract class WebTestCase extends CommonsTestCase {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockServletContext servletContext;
    protected Errors errors;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        servletContext = new MockServletContext();
        request = new MockHttpServletRequest(servletContext);
        response = new MockHttpServletResponse();
        errors = new BindException(new Object(), "command");
    }
}
