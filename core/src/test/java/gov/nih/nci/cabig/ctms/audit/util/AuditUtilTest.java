package gov.nih.nci.cabig.ctms.audit.util;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class AuditUtilTest extends TestCase {
    public void testGetObjectIdForClassWithGetId() throws Exception {
        assertEquals(new Integer(7), AuditUtil.getObjectId(new WithId(7)));
    }
    
    public void testGetObjectIdForErroringGetId() throws Exception {
        assertNull(AuditUtil.getObjectId(new Object() {
            public int getId() {
                throw new UnsupportedOperationException("Nope");
            }
        }));
    }

    public void testGetObjectIdForClassWithOverriddenGetIdMethod() throws Exception {
        assertEquals(new Integer(45), AuditUtil.getObjectId(new WithMultipleIdAccessors(45, 34)));
    }

    private static class WithId {
        private final int id;

        private WithId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static class WithMultipleIdAccessors {
        private final int id;
        private final int id2;

        private WithMultipleIdAccessors(int id1, int id2) {
            this.id = id1;
            this.id2 = id2;
        }

        public Integer getId(String kind) {
            return id2;
        }

        public Integer getId() {
            return id;
        }
    }
}
