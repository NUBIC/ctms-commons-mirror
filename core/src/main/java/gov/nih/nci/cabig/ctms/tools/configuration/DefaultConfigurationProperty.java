package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.CommonsError;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Defines the desired type for a configuration property and provides a mechanism for
 * converting it to and from a string for persistence.
 *
 * @author Rhett Sutphin
*/
public abstract class DefaultConfigurationProperty<V> implements Cloneable, ConfigurationProperty<V> {
    private final String key;
    private String name, description, storedDefault;

    protected DefaultConfigurationProperty(String key) {
        this.key = key;
    }

    public void setCollection(ConfigurationProperties collection) {
        if (collection != null) {
            String newName = collection.getNameFor(getKey());
            if (newName != null) this.name = newName;

            String newDescription = collection.getDescriptionFor(getKey());
            if (newDescription != null) this.description = newDescription;

            String newDefault = collection.getStoredDefaultFor(getKey());
            if (newDefault != null) this.storedDefault = newDefault;
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public V getDefault() {
        return storedDefault == null ? null : fromStorageFormat(storedDefault);
    }

    public String getControlType() {
        return "text";
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public DefaultConfigurationProperty<V> clone() {
        try {
            DefaultConfigurationProperty<V> clone = (DefaultConfigurationProperty<V>) super.clone();
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

        DefaultConfigurationProperty that = (DefaultConfigurationProperty) o;

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

    ////// IMPLEMENTATIONS

    public static class Text extends DefaultConfigurationProperty<String> {
        public Text(String key) { super(key); }

        public String toStorageFormat(String value) {
            return value;
        }

        public String fromStorageFormat(String stored) {
            return stored;
        }
    }

    public static class Csv extends DefaultConfigurationProperty<List<String>> {
        public Csv(String key) { super(key); }

        public String toStorageFormat(List<String> value) {
            return StringUtils.join(value.iterator(), ", ");
        }

        public List<String> fromStorageFormat(String stored) {
            String[] values = stored.split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            return Arrays.asList(values);
        }
    }

    public static class Int extends DefaultConfigurationProperty<Integer> {
        public Int(String key) { super(key); }

        public String toStorageFormat(Integer value) {
            return value.toString();
        }

        public Integer fromStorageFormat(String stored) {
            return new Integer(stored);
        }
    }

    public static class Bool extends DefaultConfigurationProperty<Boolean> {
        public Bool(String key) {
            super(key);
        }

        @Override
        public String getControlType() {
            return "boolean";
        }

        public String toStorageFormat(Boolean value) {
            return value.toString();
        }

        public Boolean fromStorageFormat(String stored) {
            return Boolean.valueOf(stored);
        }
    }
}
