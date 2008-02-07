package gov.nih.nci.cabig.ctms.tools.configuration;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

import gov.nih.nci.cabig.ctms.CommonsError;

/**
 * Defines the desired type for a configuration property and provides a mechanism for
 * converting it to and from a string for persistence.
 *
 * @author Rhett Sutphin
*/
public abstract class ConfigurationProperty<V> implements Cloneable {
    private final String key;
    private ConfigurationProperties collection;

    protected ConfigurationProperty(String key) {
        this.key = key;
    }

    // collaborator access only
    void setCollection(ConfigurationProperties collection) {
        this.collection = collection;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return collection == null ? null : collection.getNameFor(getKey());
    }

    public String getDescription() {
        return collection == null ? null : collection.getDescriptionFor(getKey());
    }

    public V getDefault() {
        String stored = collection == null ? null : collection.getStoredDefaultFor(getKey());
        return stored == null ? null : fromStorageFormat(stored);
    }

    public String getControlType() {
        return "text";
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public ConfigurationProperty<V> clone() {
        try {
            ConfigurationProperty<V> clone = (ConfigurationProperty<V>) super.clone();
            clone.setCollection(null);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new CommonsError("Clone is supported", e);
        }
    }

    @Override
    @SuppressWarnings({ "RawUseOfParameterizedType" })
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationProperty that = (ConfigurationProperty) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);
    }

    @Override
    public int hashCode() {
        return (key != null ? key.hashCode() : 0);
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('[').append(getKey()).append(']').toString();
    }

    /**
     * Convert the value into a string for persistence.  The provided value
     * will never be null.
     */
    public abstract String toStorageFormat(V value);

    /**
     * Convert back from the persisted string form into a
     * java object.  The stored value will never be null.
     * (I.e., nulls are handled before this method is called.)
     */
    public abstract V fromStorageFormat(String stored);

    ////// IMPLEMENTATIONS

    public static class Text extends ConfigurationProperty<String> {
        public Text(String key) { super(key); }

        @Override
        public String toStorageFormat(String value) {
            return value;
        }

        @Override
        public String fromStorageFormat(String stored) {
            return stored;
        }
    }

    public static class Csv extends ConfigurationProperty<List<String>> {
        public Csv(String key) { super(key); }

        @Override
        public String toStorageFormat(List<String> value) {
            return StringUtils.join(value.iterator(), ", ");
        }

        @Override
        public List<String> fromStorageFormat(String stored) {
            String[] values = stored.split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            return Arrays.asList(values);
        }
    }

    public static class Int extends ConfigurationProperty<Integer> {
        public Int(String key) { super(key); }

        @Override
        public String toStorageFormat(Integer value) {
            return value.toString();
        }

        @Override
        public Integer fromStorageFormat(String stored) {
            return new Integer(stored);
        }
    }

    public static class Bool extends ConfigurationProperty<Boolean> {
        public Bool(String key) {
            super(key);
        }

        @Override
        public String getControlType() {
            return "boolean";
        }

        @Override
        public String toStorageFormat(Boolean value) {
            return value.toString();
        }

        @Override
        public Boolean fromStorageFormat(String stored) {
            return Boolean.valueOf(stored);
        }
    }
}
