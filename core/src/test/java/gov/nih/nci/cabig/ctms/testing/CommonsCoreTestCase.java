package gov.nih.nci.cabig.ctms.testing;

import gov.nih.nci.cabig.ctms.dao.DomainObjectDao;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Rhett Sutphin
 */
public abstract class CommonsCoreTestCase extends TestCase {
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
        List<Method> methods = new LinkedList<Method>(Arrays.asList(forClass.getMethods()));
        for (Iterator<Method> iterator = methods.iterator(); iterator.hasNext();) {
            Method method = iterator.next();
            if ("domainClass".equals(method.getName())) {
                iterator.remove();
            }
        }
        return registerMockFor(forClass, methods.toArray(new Method[methods.size()]));
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
