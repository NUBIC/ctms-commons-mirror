package gov.nih.nci.cabig.ctms.domain;

import static gov.nih.nci.cabig.ctms.domain.DomainObjectTools.equalById;
import junit.framework.TestCase;
import gov.nih.nci.cabig.ctms.testing.TestObject;

/**
 * @author Rhett Sutphin
 */
public class DomainObjectToolsTest extends TestCase {
    private DomainObject o1, o2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        o1 = new TestObject(1);
        o2 = new TestObject(2);
    }

    public void testEqualByIdWithTwoNulls() throws Exception {
        assertTrue(equalById(null, null));
    }

    public void testEqualByIdWithFirstNull() throws Exception {
        assertFalse(equalById(null, o1));
    }

    public void testEqualByIdWithSecondNull() throws Exception {
        assertFalse(equalById(o1, null));
    }

    public void testEqualByIdWhenSame() throws Exception {
        assertTrue(equalById(o1, o1));
    }

    public void testEqualByIdWhenEqual() throws Exception {
        o2.setId(o1.getId());
        assertTrue(equalById(o1, o2));
    }

    public void testEqualByIdWhenNotEqual() throws Exception {
        assertFalse(equalById(o1, o2));
    }
}
