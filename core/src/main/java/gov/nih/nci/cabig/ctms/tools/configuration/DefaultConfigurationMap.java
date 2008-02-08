package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Set;
import java.util.Collection;

/**
 * Default way to implement {@link Configuration#getMap}.  Currently,
 * only the simplest methods are implemented, which is sufficient for
 * use in JSPs.  More could be implemented if a situation calls for it.
 * 
 * @author Rhett Sutphin
 */
class DefaultConfigurationMap implements java.util.Map<String, Object> {
    private Configuration configuration;

    public DefaultConfigurationMap(Configuration configuration) {
        this.configuration = configuration;
    }

    public int size() {
        return configuration.getProperties().size();
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        return configuration.getProperties().containsKey((String) key);
    }

    public boolean containsValue(Object value) {
        // if you want to actually implement this, you need to do an
        // exhaustive search, so let's skip it for now
        throw new UnsupportedOperationException("not implemented");
    }

    public Object get(Object key) {
        ConfigurationProperty<?> property = configuration.getProperties().get((String) key);
        return property == null ? null : configuration.get(property);
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
