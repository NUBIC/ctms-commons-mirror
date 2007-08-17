package gov.nih.nci.cabig.ctms.lang;

import junit.framework.TestCase;

import java.util.List;
import java.util.Collection;
import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class TypeToolsTest extends TestCase {

    public void testGetCollectionPropertyType() throws Exception {
        assertEquals(Integer.class,  TypeTools.getCollectionPropertyType(TestBean.class, "integers"));
    }

    public void testGetCollectionPropertyTypeWhenNotACollection() throws Exception {
        try {
            TypeTools.getCollectionPropertyType(TestBean.class, "long");
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("Property long of gov.nih.nci.cabig.ctms.lang.TypeToolsTest$TestBean is not a collection; it is a java.lang.Long",
                iae.getMessage());
        }
    }
    
    public void testGetCollectionPropertyTypeWhenNonExistent() throws Exception {
        try {
            TypeTools.getCollectionPropertyType(TestBean.class, "bogus");
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("gov.nih.nci.cabig.ctms.lang.TypeToolsTest$TestBean has no property bogus",
                iae.getMessage());
        }
    }

    public void testGetMapPropertyKeyType() throws Exception {
        assertEquals(Long.class, TypeTools.getMapPropertyKeyType(TestBean.class, "longToString"));
    }

    public void testGetMapPropertyValueType() throws Exception {
        assertEquals(String.class, TypeTools.getMapPropertyValueType(TestBean.class, "longToString"));
    }

    private static class TestBean {
        public Long getLong() { throw new UnsupportedOperationException("Stub"); }
        public List<String> getStrings() { throw new UnsupportedOperationException("Stub"); }
        public Collection<Integer> getIntegers() { throw new UnsupportedOperationException("Stub"); }
        public Map<Long, String> getLongToString() { throw new UnsupportedOperationException("Stub"); }
    }
}
