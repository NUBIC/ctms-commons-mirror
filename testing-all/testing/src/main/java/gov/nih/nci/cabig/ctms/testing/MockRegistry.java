package gov.nih.nci.cabig.ctms.testing;

import static org.easymock.classextension.EasyMock.*;

import java.util.Set;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;

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
