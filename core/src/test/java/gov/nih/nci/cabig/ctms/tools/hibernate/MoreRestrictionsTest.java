package gov.nih.nci.cabig.ctms.tools.hibernate;

import junit.framework.TestCase;
import org.hibernate.*;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.Region;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.*;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.sql.Connection;
import java.util.*;
import java.util.Collections;

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
           return new SessionFactoryImplementor(){
               public TypeResolver getTypeResolver() {
                   return null; 
               }

               public Properties getProperties() {
                   return null; 
               }

               public EntityPersister getEntityPersister(String s) throws MappingException {
                   return null; 
               }

               public CollectionPersister getCollectionPersister(String s) throws MappingException {
                   return null; 
               }

               public Dialect getDialect() {
                   return new PostgreSQLDialect();
               }

               public Interceptor getInterceptor() {
                   return null; 
               }

               public QueryPlanCache getQueryPlanCache() {
                   return null; 
               }

               public Type[] getReturnTypes(String s) throws HibernateException {
                   return new Type[0]; 
               }

               public String[] getReturnAliases(String s) throws HibernateException {
                   return new String[0]; 
               }

               public ConnectionProvider getConnectionProvider() {
                   return null; 
               }

               public String[] getImplementors(String s) throws MappingException {
                   return new String[0]; 
               }

               public String getImportedClassName(String s) {
                   return null; 
               }

               public TransactionManager getTransactionManager() {
                   return null; 
               }

               public QueryCache getQueryCache() {
                   return null; 
               }

               public QueryCache getQueryCache(String s) throws HibernateException {
                   return null; 
               }

               public UpdateTimestampsCache getUpdateTimestampsCache() {
                   return null; 
               }

               public StatisticsImplementor getStatisticsImplementor() {
                   return null; 
               }

               public NamedQueryDefinition getNamedQuery(String s) {
                   return null; 
               }

               public NamedSQLQueryDefinition getNamedSQLQuery(String s) {
                   return null; 
               }

               public ResultSetMappingDefinition getResultSetMapping(String s) {
                   return null; 
               }

               public IdentifierGenerator getIdentifierGenerator(String s) {
                   return null; 
               }

               public Region getSecondLevelCacheRegion(String s) {
                   return null; 
               }

               public Map getAllSecondLevelCacheRegions() {
                   return null; 
               }

               public SQLExceptionConverter getSQLExceptionConverter() {
                   return null; 
               }

               public Settings getSettings() {
                   return null; 
               }

               public Session openTemporarySession() throws HibernateException {
                   return null; 
               }

               public Session openSession(Connection connection, boolean b, boolean b1, ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
                   return null; 
               }

               public Set<String> getCollectionRolesByEntityParticipant(String s) {
                   return null; 
               }

               public EntityNotFoundDelegate getEntityNotFoundDelegate() {
                   return null; 
               }

               public SQLFunctionRegistry getSqlFunctionRegistry() {
                   return null; 
               }

               public FetchProfile getFetchProfile(String s) {
                   return null; 
               }

               public SessionFactoryObserver getFactoryObserver() {
                   return null; 
               }

               public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
                   return null; 
               }

               public Type getIdentifierType(String s) throws MappingException {
                   return null; 
               }

               public String getIdentifierPropertyName(String s) throws MappingException {
                   return null; 
               }

               public Type getReferencedPropertyType(String s, String s1) throws MappingException {
                   return null; 
               }

               public Session openSession() throws HibernateException {
                   return null; 
               }

               public Session openSession(Interceptor interceptor) throws HibernateException {
                   return null; 
               }

               public Session openSession(Connection connection) {
                   return null; 
               }

               public Session openSession(Connection connection, Interceptor interceptor) {
                   return null; 
               }

               public Session getCurrentSession() throws HibernateException {
                   return null; 
               }

               public StatelessSession openStatelessSession() {
                   return null; 
               }

               public StatelessSession openStatelessSession(Connection connection) {
                   return null; 
               }

               public ClassMetadata getClassMetadata(Class aClass) {
                   return null; 
               }

               public ClassMetadata getClassMetadata(String s) {
                   return null; 
               }

               public CollectionMetadata getCollectionMetadata(String s) {
                   return null; 
               }

               public Map<String, ClassMetadata> getAllClassMetadata() {
                   return null; 
               }

               public Map getAllCollectionMetadata() {
                   return null; 
               }

               public Statistics getStatistics() {
                   return null; 
               }

               public void close() throws HibernateException {
                  
               }

               public boolean isClosed() {
                   return false; 
               }

               public Cache getCache() {
                   return null; 
               }

               public void evict(Class aClass) throws HibernateException {
                  
               }

               public void evict(Class aClass, Serializable serializable) throws HibernateException {
                  
               }

               public void evictEntity(String s) throws HibernateException {
                  
               }

               public void evictEntity(String s, Serializable serializable) throws HibernateException {
                  
               }

               public void evictCollection(String s) throws HibernateException {
                  
               }

               public void evictCollection(String s, Serializable serializable) throws HibernateException {
                  
               }

               public void evictQueries(String s) throws HibernateException {
                  
               }

               public void evictQueries() throws HibernateException {
                  
               }

               public Set getDefinedFilterNames() {
                   return null; 
               }

               public FilterDefinition getFilterDefinition(String s) throws HibernateException {
                   return null; 
               }

               public boolean containsFetchProfileDefinition(String s) {
                   return false; 
               }

               public TypeHelper getTypeHelper() {
                   return null; 
               }

               public Reference getReference() throws NamingException {
                   return null; 
               }
           };
        }

        public String getColumn(Criteria criteria, String propertyPath) throws HibernateException {
            return propertyPath;
        }

        public Type getType(Criteria criteria, String propertyPath) throws HibernateException {
           return new IntegerType();
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

        public String[] getColumns(String s, Criteria criteria) throws HibernateException {
            return new String[]{s};
        }

        public String[] findColumns(String s, Criteria criteria) throws HibernateException {
            return new String[]{s};
        }
    }
}
