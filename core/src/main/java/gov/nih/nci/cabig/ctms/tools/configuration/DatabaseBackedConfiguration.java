package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.Collection;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

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
public abstract class DatabaseBackedConfiguration extends HibernateDaoSupport {
    private java.util.Map<String, Object> map;

    /**
     * Subclasses must implement this.  Most of the time, the returned
     * instance will be <code>static</code>ly configured in the subclass.
     */
    public abstract ConfigurationProperties getProperties();

    public <V> V get(ConfigurationProperty<V> property) {
        ConfigurationEntry entry
            = (ConfigurationEntry) getHibernateTemplate().get(getConfigurationEntryClass(), property.getKey());
        if (entry == null) {
            return property.getDefault();
        } else {
            return entry.getValue() == null
                ? null
                : property.fromStorageFormat(entry.getValue());
        }
    }

    @Transactional(readOnly = false)
    public <V> void set(ConfigurationProperty<V> property, V value) {
        ConfigurationEntry entry
            = (ConfigurationEntry) getHibernateTemplate().get(getConfigurationEntryClass(), property.getKey());
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
        if (map == null) map = new Map();
        return map;
    }

    private class Map implements java.util.Map<String, Object> {
        public int size() {
            return getProperties().size();
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean containsKey(Object key) {
            return getProperties().containsKey((String) key);
        }

        public boolean containsValue(Object value) {
            // if you want to actually implement this, you need to do an
            // exhaustive search, so let's skip it for now
            throw new UnsupportedOperationException("not implemented");
        }

        public Object get(Object key) {
            ConfigurationProperty<?> property = getProperties().get((String) key);
            return property == null ? null : DatabaseBackedConfiguration.this.get(property);
        }

        ////// COLLECTIVE INTERFACES NOT IMPLEMENTED //////

        public Set<String> keySet() {
            throw new UnsupportedOperationException("keySet not implemented");
        }

        public Collection<Object> values() {
            throw new UnsupportedOperationException("values not implemented");
        }

        public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException("entrySet not implemented");
        }

        ////// READ-ONLY //////

        public Object put(String key, Object value) {
            throw new UnsupportedOperationException("Configuration map is read-only");
        }

        public Object remove(Object key) {
            throw new UnsupportedOperationException("Configuration map is read-only");
        }

        public void putAll(java.util.Map<? extends String, ? extends Object> t) {
            throw new UnsupportedOperationException("Configuration map is read-only");
        }

        public void clear() {
            throw new UnsupportedOperationException("Configuration map is read-only");
        }
    }
}
