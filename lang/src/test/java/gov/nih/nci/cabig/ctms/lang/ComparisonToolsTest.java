package gov.nih.nci.cabig.ctms.lang;

import junit.framework.TestCase;

/**
 * @author RhettSutphin
 */
public class ComparisonToolsTest extends TestCase {
    public void testCompareTwoNulls() {
        assertEquals(0, ComparisonTools.nullSafeCompare(null, null));
    }

    public void testCompare1stNull() {
        assertNegative(ComparisonTools.nullSafeCompare(null, "two"));
    }

    public void testCompare2ndNull() {
        assertPositive(ComparisonTools.nullSafeCompare("one", null));
    }

    public void testCompareBothNotNull() {
        assertEquals("one".compareTo("two"), ComparisonTools.nullSafeCompare("one", "two"));
    }

    public void testEquals1stNull() {
        assertFalse(ComparisonTools.nullSafeEquals(null, "two"));
    }

    public void testEquals2ndNull() {
        assertFalse(ComparisonTools.nullSafeEquals("one", null));
    }

    public void testEqualsBothNull() {
        assertTrue(ComparisonTools.nullSafeEquals(null, null));
    }

    public void testEqualsBothNotNull() {
        assertFalse(ComparisonTools.nullSafeEquals("one", "two"));
        assertTrue(ComparisonTools.nullSafeEquals("same", "same"));
    }

    /* TODO: will probably want to move this functionality into core module
    private WithLongId idOne  = new TestableWithId(1);
    private WithLongId idTwo  = new TestableWithId(2);
    private WithLongId idNull = new TestableWithId(null);

    public void testEqualsById1stNull() {
        assertFalse(ComparisonTools.nullSafeEqualsById(null, idOne));
    }

    public void testEqualsById2ndNull() {
        assertFalse(ComparisonTools.nullSafeEqualsById(null, idTwo));
    }

    public void testEqualsByIdBothNull() {
        assertTrue(ComparisonTools.nullSafeEqualsById(null, null));
    }

    public void testEqualsByIdBothNotNull() {
        assertFalse(ComparisonTools.nullSafeEqualsById(idOne, idTwo));
        assertTrue(ComparisonTools.nullSafeEqualsById(idOne, new TestableWithId(1)));
    }

    public void testEqualsById1stIdNull() {
        assertFalse(ComparisonTools.nullSafeEqualsById(idNull, idTwo));
    }

    public void testEqualsById2ndIdNull() {
        assertFalse(ComparisonTools.nullSafeEqualsById(idOne, idNull));
    }

    public void testEqualsByIdBothIdNull() {
        assertTrue(ComparisonTools.nullSafeEqualsById(idNull, idNull));
    }

    private static class TestableWithId implements WithLongId {
        private Long id;

        public TestableWithId(long id) {
            this(new Long(id));
        }

        public TestableWithId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean equals(Object obj) {
            fail("This method should never be called");
            return false;
        }
    }
    */

    public void testBooleanInspectWhereBothNull() {
        assertFalse(ComparisonTools.nullSafeInspect(null, null, false, true, new TestableBooleanInspector(true)));
        assertTrue(ComparisonTools.nullSafeInspect(null, null, true, false, new TestableBooleanInspector(false)));
    }

    public void testBooleanInspectWhereOneIsNull() {
        assertFalse(ComparisonTools.nullSafeInspect(null, "foo", true, false, new TestableBooleanInspector(true)));
        assertFalse(ComparisonTools.nullSafeInspect("foo", null, true, false, new TestableBooleanInspector(true)));
        assertTrue(ComparisonTools.nullSafeInspect(null, "foo", false, true, new TestableBooleanInspector(false)));
        assertTrue(ComparisonTools.nullSafeInspect("foo", null, false, true, new TestableBooleanInspector(false)));
    }

    public void testBooleanInspectWhereBothNonNull() {
        assertFalse(ComparisonTools.nullSafeInspect("foo", "bar", true, true, new TestableBooleanInspector(false)));
        assertTrue(ComparisonTools.nullSafeInspect("foo", "bar", false, false, new TestableBooleanInspector(true)));
    }

    private static class TestableBooleanInspector implements ComparisonTools.BooleanInspector<Object> {
        private boolean returnValue;

        public TestableBooleanInspector(boolean returnValue) {
            this.returnValue = returnValue;
        }

        public boolean inspect(Object one, Object two) {
            return returnValue;
        }
    }

    // TODO: when the assertions in CoreTestCase are moved into ctms commons, use these from there

    private static void assertPositive(long value) {
        assertTrue(value + " is not positive", value > 0);
    }

    private static void assertNegative(long value) {
        assertTrue(value + " is not negative", value < 0);
    }

}
