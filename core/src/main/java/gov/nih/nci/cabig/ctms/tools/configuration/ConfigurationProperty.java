package gov.nih.nci.cabig.ctms.tools.configuration;

/**
 * @author Rhett Sutphin
 */
public interface ConfigurationProperty<V> extends Cloneable {
    String getKey();

    String getName();

    String getDescription();

    V getDefault();

    String getControlType();

    /**
     * Convert the value into a string for persistence.  The provided value
     * will never be null.
     */
    String toStorageFormat(V value);

    /**
     * Convert back from the persisted string form into a
     * java object.  The stored value will never be null.
     * (I.e., nulls are handled before this method is called.)
     */
    V fromStorageFormat(String stored);

    ConfigurationProperty<V> clone();
}
