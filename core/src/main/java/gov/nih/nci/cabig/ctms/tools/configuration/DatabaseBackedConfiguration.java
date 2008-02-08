package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

import java.util.Map;

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
 * @see ConfigurationProperties
 * @see ConfigurationProperty
 * @author Rhett Sutphin
 */
@Transactional(readOnly = true)
public abstract class DatabaseBackedConfiguration extends HibernateDaoSupport implements Configuration {
    private Map<String, Object> map;

    public <V> V get(ConfigurationProperty<V> property) {
        ConfigurationEntry entry = getEntry(property);
        if (entry == null) {
            return property.getDefault();
        } else {
            return entry.getValue() == null
                ? null
                : property.fromStorageFormat(entry.getValue());
        }
    }

    private <V> ConfigurationEntry getEntry(ConfigurationProperty<V> property) {
        return (ConfigurationEntry) getHibernateTemplate().get(getConfigurationEntryClass(), property.getKey());
    }

    @Transactional(readOnly = false)
    public <V> void set(ConfigurationProperty<V> property, V value) {
        ConfigurationEntry entry = getEntry(property);
        if (entry == null) {
            try {
                entry = getConfigurationEntryClass().newInstance();
                entry.setKey(property.getKey());
            } catch (InstantiationException e) {
                throw new CommonsSystemException(
                    "Could not instantiate a new configuration entry of class %s", e,
                    getConfigurationEntryClass().getName());
            } catch (IllegalAccessException e) {
                throw new CommonsSystemException(
                    "Could not instantiate a new configuration entry of class %s", e,
                    getConfigurationEntryClass().getName());
            }
        }
        entry.setValue(value == null ? null : property.toStorageFormat(value));
        getHibernateTemplate().saveOrUpdate(entry);
    }

    public boolean isSet(ConfigurationProperty<?> property) {
        return getEntry(property) != null;
    }

    @Transactional(readOnly = false)
    public <V> void reset(ConfigurationProperty<V> property) {
        ConfigurationEntry entry = getEntry(property);
        if (entry == null) return;
        getHibernateTemplate().delete(entry);
    }

    /**
     * Allows subclasses to specify a subclass of {@link ConfigurationEntry} to
     * use.  The primary benefit of this would be to allow for a configuration
     * stored in a table not called "configuration".
     * <p>
     * The default is {@link DefaultConfigurationEntry}, which should be fine
     * unless you want to have more than one type of configuration in your
     * application.
     */
    protected Class<? extends ConfigurationEntry> getConfigurationEntryClass() {
        return DefaultConfigurationEntry.class;
    }

    public java.util.Map<String, Object> getMap() {
        if (map == null) map = new DefaultConfigurationMap(this);
        return map;
    }
}
