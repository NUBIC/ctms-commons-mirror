package gov.nih.nci.cabig.ctms.testing;

import gov.nih.nci.cabig.ctms.dao.DomainObjectDao;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * @author Rhett Sutphin
 */
public abstract class CommonsTestCase extends TestCase {
    private MockRegistry mockRegistry;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockRegistry = new MockRegistry();
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

    protected <T extends DomainObjectDao<?>> T registerDaoMockFor(Class<T> forClass) {
        return mockRegistry.registerDaoMockFor(forClass);
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
