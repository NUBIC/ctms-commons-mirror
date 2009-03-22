package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.SessionFactory;

/**
 * Provides a base DAO class for an KV-style configuration system for an application.
 * The actual database records are modeled by {@link ConfigurationEntry}.  The database
 * table should be created something like this (this is for PostgreSQL; others will be different):
 * <pre>CREATE TABLE configuration (
 *   key TEXT PRIMARY KEY,
 *   value TEXT,
 *   version INTEGER NOT NULL DEFAULT 0
 * );</pre>
 * Or, using Bering:
 * <pre>    void up() {
 *       createTable("configuration") { t ->
 *           t.includePrimaryKey = false
 *           t.addColumn("key", "string", primaryKey: true)
 *           t.addColumn("value", "string")
 *           t.addVersionColumn()
 *       }
 *   }</pre> 
 *
 * <p>
 * If you want to call your table something besides "configuration", see
 * {@link #getConfigurationEntryClass()}.
 *
 * @see ConfigurationEntry
 * @see DefaultConfigurationProperties
 * @see DefaultConfigurationProperty
 * @author Rhett Sutphin
 */
@Transactional(readOnly = true)
public abstract class DatabaseBackedConfiguration extends AbstractConfiguration {
    private HibernateTemplate hibernateTemplate;

    @Override
    protected <V> ConfigurationEntry getEntry(ConfigurationProperty<V> property) {
        return (ConfigurationEntry) getHibernateTemplate()
            .get(getConfigurationEntryClass(), property.getKey());
    }

    @Override
    protected void store(ConfigurationEntry entry) {
        getHibernateTemplate().saveOrUpdate(entry);
    }

    @Override
    protected void remove(ConfigurationEntry entry) {
        getHibernateTemplate().delete(entry);
    }

    @Override // overridden to add @Transactional
    @Transactional(readOnly = false)
    public <V> void set(ConfigurationProperty<V> property, V value) {
        super.set(property, value);
    }

    @Override // overridden to add @Transactional
    @Transactional(readOnly = false)
    public <V> void reset(ConfigurationProperty<V> property) {
        super.reset(property);
    }

    private HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    ////// CONFIGURATION

    // both sessionFactory and hibernateTemplate setters are included to
    // preserve backwards compatibility (this used to be a subclass of
    // HibernateDaoSupport)

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
