package gov.nih.nci.cabig.ctms.testing;

import static org.easymock.classextension.EasyMock.*;
import org.apache.commons.logging.Log;

import java.util.Set;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;

/**
 * @author Rhett Sutphin
 */
public class MockRegistry {
    private Log log;
    private Set<Object> mocks;

    public MockRegistry() {
        this(null);
    }

    public MockRegistry(Log log) {
        this.log = log;
        mocks = new LinkedHashSet<Object>();
    }

    public <T> T registerMockFor(Class<T> forClass) {
        return registered(createMock(forClass));
    }

    public <T> T registerMockFor(Class<T> forClass, Method... methodsToMock) {
        return registered(createMock(forClass, methodsToMock));
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
