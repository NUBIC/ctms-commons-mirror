package gov.nih.nci.cabig.ctms.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.Errors;
import org.springframework.validation.BindException;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import gov.nih.nci.cabig.ctms.testing.MockRegistry;

/**
 * @author Rhett Sutphin
 */
public abstract class WebTestCase extends TestCase {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockServletContext servletContext;
    protected Errors errors;
    private MockRegistry mockRegistry;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockRegistry = new MockRegistry();
        servletContext = new MockServletContext();
        request = new MockHttpServletRequest(servletContext);
        response = new MockHttpServletResponse();
        errors = new BindException(new Object(), "command");
    }

    ////// MOCK REGISTRY DELEGATION

    protected MockRegistry getMockRegistry() {
        return mockRegistry;
    }

    protected <T> T registerMockFor(Class<T> forClass) {
        return mockRegistry.registerMockFor(forClass);
    }

    protected <T> T registerMockFor(Class<T> forClass, Method... methodsToMock) {
        return mockRegistry.registerMockFor(forClass, methodsToMock);
    }

    protected void replayMocks() {
        mockRegistry.replayMocks();
    }

    protected void verifyMocks() {
        mockRegistry.verifyMocks();
    }

    protected void resetMocks() {
        mockRegistry.resetMocks();
    }
}
