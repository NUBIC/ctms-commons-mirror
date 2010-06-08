package gov.nih.nci.cabig.ctms.testing;

import static org.easymock.classextension.EasyMock.*;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
public class MockRegistry {
    private Logger log;
    private Set<Object> mocks;

    public MockRegistry() {
        this(null);
    }

    public MockRegistry(Logger log) {
        this.log = log;
        mocks = new LinkedHashSet<Object>();
    }

    public <T> T registerMockFor(Class<T> forClass, Method... methodsToMock) {
        T mock;
        if (methodsToMock.length == 0) {
            mock = createMock(forClass);
        } else {
            mock = createMock(forClass, methodsToMock);
        }
        return registered(mock);
    }

    public <T> T registerNiceMockFor(Class<T> forClass, Method... methodsToMock) {
        T mock;
        if (methodsToMock.length == 0) {
            mock = createNiceMock(forClass);
        } else {
            mock = createNiceMock(forClass, methodsToMock);
        }
        return registered(mock);
    }

    public void replayMocks() {
        for (Object mock : mocks) {
            debug("Replaying %s", mock);
            replay(mock);
        }
    }

    public void verifyMocks() {
        for (Object mock : mocks) {
            debug("Verifying %s", mock);
            verify(mock);
        }
    }

    public void resetMocks() {
        for (Object mock : mocks) {
            debug("Resetting %s", mock);
            reset(mock);
        }
    }

    private <T> T registered(T mock) {
        mocks.add(mock);
        return mock;
    }

    private void debug(String msg, Object... params) {
        if (log != null) log.debug(String.format(msg, params));
    }
}
