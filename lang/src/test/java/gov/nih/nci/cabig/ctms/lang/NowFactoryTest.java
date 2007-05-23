package gov.nih.nci.cabig.ctms.lang;

import junit.framework.TestCase;

import java.util.Date;
import java.sql.Timestamp;

/**
 * @author Rhett Sutphin
 */
public class NowFactoryTest extends TestCase {
    protected NowFactory nowFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        nowFactory = new NowFactory();
    }

    public void testNowIsNow() throws Exception {
        Date actual = nowFactory.getNow();
        assertNotNull(actual);
        assertNow(actual.getTime());
    }
    
    public void testNowTimestampIsNow() throws Exception {
        Timestamp actual = nowFactory.getNowTimestamp();
        assertNotNull(actual);
        assertNow(actual.getTime());
    }

    private static void assertNow(long actualMillis) {
        long expectedMillis = System.currentTimeMillis();
        long diff = Math.abs(actualMillis - expectedMillis);

        assertTrue("Difference from now is not within 100ms: " + diff, diff < 100);
    }
}
