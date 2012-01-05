package gov.nih.nci.cabig.ctms.tools.hibernate;

import junit.framework.TestCase;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class MoreRestrictionsTest extends TestCase {
    private CriteriaQuery criteriaQuery;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        criteriaQuery = new SimpleCriteriaQuery();
    }

    public void testShortInListNotBrokenUp() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(3), 10);
        assertEquals("(foo in (?, ?, ?))", criterionSql(actual));
    }

    public void testShortInListPreservesValues() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(3), 10);
        TypedValue[] actualValues = actual.getTypedValues(null, criteriaQuery);
        for (int i = 0; i < actualValues.length; i++) {
            assertEquals("value " + i + " is wrong", i + 1, actualValues[i].getValue());
        }
        assertEquals("Wrong number of actual values", 3, actualValues.length);
    }

    public void testLongInListIsBrokenUp() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(5), 3);
        assertEquals("(foo in (?, ?, ?) or foo in (?, ?))", criterionSql(actual));
    }

    public void testLongInListPreservesValues() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(5), 3);
        TypedValue[] actualValues = actual.getTypedValues(null, criteriaQuery);
        for (int i = 0; i < actualValues.length; i++) {
            assertEquals("value " + i + " is wrong", i + 1, actualValues[i].getValue());
        }
        assertEquals("Wrong number of actual values", 5, actualValues.length);
    }

    public void testExactMultipleMaxLengthForInListWorksCorrectly() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(6), 3);
        assertEquals("(foo in (?, ?, ?) or foo in (?, ?, ?))", criterionSql(actual));
    }

    public void testVeryLongInListWorks() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(16), 3);
        assertEquals("(foo in (?, ?, ?) or foo in (?, ?, ?) or foo in (?, ?, ?) or foo in (?, ?, ?) or foo in (?, ?, ?) or foo in (?))",
            criterionSql(actual));
    }

    // i.e., 4 and 3 instead of 6 and 1
    public void testInListSegmentsAreBalanced() throws Exception {
        Criterion actual = MoreRestrictions.in("foo", createNumberList(7), 6);
        assertEquals("(foo in (?, ?, ?, ?) or foo in (?, ?, ?))", criterionSql(actual));
    }

    // this matches hibernate's behavior
    public void testDoesNotBlowUpWithAnEmptyValueList() throws Exception {
        Criterion actual = MoreRestrictions.in("bar", Collections.emptySet(), 6);
        assertEquals("(bar in ())", criterionSql(actual));
    }

    public void testMaxLengthZeroIsIllegalArgument() throws Exception {
        try {
            MoreRestrictions.in("foo", createNumberList(16), 0);
        } catch (IllegalArgumentException iae) {
            assertEquals("Wrong message", "maxLength must be positive", iae.getMessage());
        }
    }

    private String criterionSql(Criterion criterion) {
        return criterion.toSqlString(null, criteriaQuery);
    }

    public void testDefaultSegmentLengthIs800() throws Exception {
        Criterion for900 = MoreRestrictions.in("foo", createNumberList(900));
        assertEquals(-1, criterionSql(for900).indexOf("or"));
        Criterion for901 = MoreRestrictions.in("foo", createNumberList(901));
        assertTrue(criterionSql(for901).contains("or"));
    }

    private List<Integer> createNumberList(int size) {
        List<Integer> list = new ArrayList<Integer>(size);
        while (list.size() < size) list.add(list.size() + 1);
        return list;
    }

    private static class SimpleCriteriaQuery implements CriteriaQuery {
        public SessionFactoryImplementor getFactory() {
            throw new UnsupportedOperationException("getFactory not implemented");
        }

        public String getColumn(Criteria criteria, String propertyPath) throws HibernateException {
            return propertyPath;
        }

        public Type getType(Criteria criteria, String propertyPath) throws HibernateException {
            throw new UnsupportedOperationException("getType not implemented");
        }

        public String[] getColumnsUsingProjection(Criteria criteria, String propertyPath) throws HibernateException {
            return new String[] { propertyPath };
        }

        public Type getTypeUsingProjection(Criteria criteria, String propertyPath) throws HibernateException {
            return new org.hibernate.type.IntegerType();
        }

        public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value) throws HibernateException {
            throw new UnsupportedOperationException("getTypedValue not implemented");
        }

        public String getEntityName(Criteria criteria) {
            throw new UnsupportedOperationException("getEntityName not implemented");
        }

        public String getEntityName(Criteria criteria, String s) {
            throw new UnsupportedOperationException("getEntityName not implemented");
        }

        public String getSQLAlias(Criteria subcriteria) {
            throw new UnsupportedOperationException("getSQLAlias not implemented");
        }

        public String getSQLAlias(Criteria criteria, String propertyPath) {
            throw new UnsupportedOperationException("getSQLAlias not implemented");
        }

        public String getPropertyName(String propertyName) {
            return propertyName;
        }

        public String[] getIdentifierColumns(Criteria criteria) {
            throw new UnsupportedOperationException("getIdentifierColumns not implemented");
        }

        public Type getIdentifierType(Criteria subcriteria) {
            throw new UnsupportedOperationException("getIdentifierType not implemented");
        }

        public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object o) {
            throw new UnsupportedOperationException("getTypedIdentifierValue not implemented");
        }

        public String generateSQLAlias() {
            throw new UnsupportedOperationException("generateSQLAlias not implemented");
        }
    }
}
