package gov.nih.nci.cabig.ctms.testing;

import static org.easymock.classextension.EasyMock.*;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.reflect.Method;

import gov.nih.nci.cabig.ctms.dao.DomainObjectDao;

/**
 * @author Rhett Sutphin
 */
public class MockRegistry {
    private Set<Object> mocks;

    public MockRegistry() {
        mocks = new LinkedHashSet<Object>();
    }

    public <T> T registerMockFor(Class<T> forClass) {
        return registered(createMock(forClass));
    }

    public <T> T registerMockFor(Class<T> forClass, Method... methodsToMock) {
        return registered(createMock(forClass, methodsToMock));
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

    public void replayMocks() {
        for (Object mock : mocks) replay(mock);
    }

    public void verifyMocks() {
        for (Object mock : mocks) verify(mock);
    }

    public void resetMocks() {
        for (Object mock : mocks) reset(mock);
    }

    private <T> T registered(T mock) {
        mocks.add(mock);
        return mock;
    }

}
