package gov.nih.nci.cabig.ctms.lang;

import junit.framework.TestCase;

import java.util.Date;

/**
 * @author Rhett Sutphin
 */
public class NowFactoryTest extends TestCase {
    public void testNowIsNow() throws Exception {
        long millis = System.currentTimeMillis();
        Date actual = new NowFactory().getNow();
        assertNotNull(actual);
        long diff = Math.abs(millis - actual.getTime());
        assertTrue("Difference is not within 100ms: " + diff, diff < 100);
    }
}
