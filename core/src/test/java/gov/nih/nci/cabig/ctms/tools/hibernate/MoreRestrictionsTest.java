package gov.nih.nci.cabig.ctms.tools.hibernate;

import junit.framework.TestCase;
import org.hibernate.Cache;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
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
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
            return new SimpleSessionFactoryImplementor();
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

        public String[] getColumns(String propertyPath, Criteria criteria) throws HibernateException {
            return new String[] { propertyPath };
        }

        public String[] findColumns(String propertyPath, Criteria criteria) throws HibernateException {
            return new String[] { propertyPath };
        }
    }

    private static class SimpleSessionFactoryImplementor implements SessionFactoryImplementor {
        public Dialect getDialect() {
            return new PostgreSQLDialect();
        }

        ////// STUBS

        public TypeResolver getTypeResolver() {
            throw new UnsupportedOperationException("getTypeResolver not implemented");
        }

        public Properties getProperties() {
            throw new UnsupportedOperationException("getProperties not implemented");
        }

        public EntityPersister getEntityPersister(String entityName) throws MappingException {
            throw new UnsupportedOperationException("getEntityPersister not implemented");
        }

        public CollectionPersister getCollectionPersister(String role) throws MappingException {
            throw new UnsupportedOperationException("getCollectionPersister not implemented");
        }

        public Interceptor getInterceptor() {
            throw new UnsupportedOperationException("getInterceptor not implemented");
        }

        public QueryPlanCache getQueryPlanCache() {
            throw new UnsupportedOperationException("getQueryPlanCache not implemented");
        }

        public Type[] getReturnTypes(String queryString) throws HibernateException {
            throw new UnsupportedOperationException("getReturnTypes not implemented");
        }

        public String[] getReturnAliases(String queryString) throws HibernateException {
            throw new UnsupportedOperationException("getReturnAliases not implemented");
        }

        public ConnectionProvider getConnectionProvider() {
            throw new UnsupportedOperationException("getConnectionProvider not implemented");
        }

        public String[] getImplementors(String className) throws MappingException {
            throw new UnsupportedOperationException("getImplementors not implemented");
        }

        public String getImportedClassName(String name) {
            throw new UnsupportedOperationException("getImportedClassName not implemented");
        }

        public TransactionManager getTransactionManager() {
            throw new UnsupportedOperationException("getTransactionManager not implemented");
        }

        public QueryCache getQueryCache() {
            throw new UnsupportedOperationException("getQueryCache not implemented");
        }

        public QueryCache getQueryCache(String regionName) throws HibernateException {
            throw new UnsupportedOperationException("getQueryCache not implemented");
        }

        public UpdateTimestampsCache getUpdateTimestampsCache() {
            throw new UnsupportedOperationException("getUpdateTimestampsCache not implemented");
        }

        public StatisticsImplementor getStatisticsImplementor() {
            throw new UnsupportedOperationException("getStatisticsImplementor not implemented");
        }

        public NamedQueryDefinition getNamedQuery(String queryName) {
            throw new UnsupportedOperationException("getNamedQuery not implemented");
        }

        public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
            throw new UnsupportedOperationException("getNamedSQLQuery not implemented");
        }

        public ResultSetMappingDefinition getResultSetMapping(String name) {
            throw new UnsupportedOperationException("getResultSetMapping not implemented");
        }

        public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
            throw new UnsupportedOperationException("getIdentifierGenerator not implemented");
        }

        public Region getSecondLevelCacheRegion(String regionName) {
            throw new UnsupportedOperationException("getSecondLevelCacheRegion not implemented");
        }

        public Map getAllSecondLevelCacheRegions() {
            throw new UnsupportedOperationException("getAllSecondLevelCacheRegions not implemented");
        }

        public SQLExceptionConverter getSQLExceptionConverter() {
            throw new UnsupportedOperationException("getSQLExceptionConverter not implemented");
        }

        public Settings getSettings() {
            throw new UnsupportedOperationException("getSettings not implemented");
        }

        public Session openTemporarySession() throws HibernateException {
            throw new UnsupportedOperationException("openTemporarySession not implemented");
        }

        public Session openSession(Connection connection, boolean flushBeforeCompletionEnabled, boolean autoCloseSessionEnabled, ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
            throw new UnsupportedOperationException("openSession not implemented");
        }

        public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
            throw new UnsupportedOperationException("getCollectionRolesByEntityParticipant not implemented");
        }

        public EntityNotFoundDelegate getEntityNotFoundDelegate() {
            throw new UnsupportedOperationException("getEntityNotFoundDelegate not implemented");
        }

        public SQLFunctionRegistry getSqlFunctionRegistry() {
            throw new UnsupportedOperationException("getSqlFunctionRegistry not implemented");
        }

        public FetchProfile getFetchProfile(String name) {
            throw new UnsupportedOperationException("getFetchProfile not implemented");
        }

        public SessionFactoryObserver getFactoryObserver() {
            throw new UnsupportedOperationException("getFactoryObserver not implemented");
        }

        public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
            throw new UnsupportedOperationException("getIdentifierGeneratorFactory not implemented");
        }

        public Type getIdentifierType(String className) throws MappingException {
            throw new UnsupportedOperationException("getIdentifierType not implemented");
        }

        public String getIdentifierPropertyName(String className) throws MappingException {
            throw new UnsupportedOperationException("getIdentifierPropertyName not implemented");
        }

        public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
            throw new UnsupportedOperationException("getReferencedPropertyType not implemented");
        }

        public Session openSession() throws HibernateException {
            throw new UnsupportedOperationException("openSession not implemented");
        }

        public Session openSession(Interceptor interceptor) throws HibernateException {
            throw new UnsupportedOperationException("openSession not implemented");
        }

        public Session openSession(Connection connection) {
            throw new UnsupportedOperationException("openSession not implemented");
        }

        public Session openSession(Connection connection, Interceptor interceptor) {
            throw new UnsupportedOperationException("openSession not implemented");
        }

        public Session getCurrentSession() throws HibernateException {
            throw new UnsupportedOperationException("getCurrentSession not implemented");
        }

        public StatelessSession openStatelessSession() {
            throw new UnsupportedOperationException("openStatelessSession not implemented");
        }

        public StatelessSession openStatelessSession(Connection connection) {
            throw new UnsupportedOperationException("openStatelessSession not implemented");
        }

        public ClassMetadata getClassMetadata(Class entityClass) {
            throw new UnsupportedOperationException("getClassMetadata not implemented");
        }

        public ClassMetadata getClassMetadata(String entityName) {
            throw new UnsupportedOperationException("getClassMetadata not implemented");
        }

        public CollectionMetadata getCollectionMetadata(String roleName) {
            throw new UnsupportedOperationException("getCollectionMetadata not implemented");
        }

        public Map<String, ClassMetadata> getAllClassMetadata() {
            throw new UnsupportedOperationException("getAllClassMetadata not implemented");
        }

        public Map getAllCollectionMetadata() {
            throw new UnsupportedOperationException("getAllCollectionMetadata not implemented");
        }

        public Statistics getStatistics() {
            throw new UnsupportedOperationException("getStatistics not implemented");
        }

        public void close() throws HibernateException {
            throw new UnsupportedOperationException("close not implemented");
        }

        public boolean isClosed() {
            throw new UnsupportedOperationException("isClosed not implemented");
        }

        public Cache getCache() {
            throw new UnsupportedOperationException("getCache not implemented");
        }

        public void evict(Class persistentClass) throws HibernateException {
            throw new UnsupportedOperationException("evict not implemented");
        }

        public void evict(Class persistentClass, Serializable id) throws HibernateException {
            throw new UnsupportedOperationException("evict not implemented");
        }

        public void evictEntity(String entityName) throws HibernateException {
            throw new UnsupportedOperationException("evictEntity not implemented");
        }

        public void evictEntity(String entityName, Serializable id) throws HibernateException {
            throw new UnsupportedOperationException("evictEntity not implemented");
        }

        public void evictCollection(String roleName) throws HibernateException {
            throw new UnsupportedOperationException("evictCollection not implemented");
        }

        public void evictCollection(String roleName, Serializable id) throws HibernateException {
            throw new UnsupportedOperationException("evictCollection not implemented");
        }

        public void evictQueries(String cacheRegion) throws HibernateException {
            throw new UnsupportedOperationException("evictQueries not implemented");
        }

        public void evictQueries() throws HibernateException {
            throw new UnsupportedOperationException("evictQueries not implemented");
        }

        public Set getDefinedFilterNames() {
            throw new UnsupportedOperationException("getDefinedFilterNames not implemented");
        }

        public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
            throw new UnsupportedOperationException("getFilterDefinition not implemented");
        }

        public boolean containsFetchProfileDefinition(String name) {
            throw new UnsupportedOperationException("containsFetchProfileDefinition not implemented");
        }

        public TypeHelper getTypeHelper() {
            throw new UnsupportedOperationException("getTypeHelper not implemented");
        }

        public Reference getReference() throws NamingException {
            throw new UnsupportedOperationException("getReference not implemented");
        }
    }
}
