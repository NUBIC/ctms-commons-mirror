package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public interface Configuration {
    /**
     * @return the properties supported by this instance
     */
    ConfigurationProperties getProperties();

    /**
     * Retrieve the current value for the given property. If the property is
     * not set, return the default value (if there is one).
     */
    <V> V get(ConfigurationProperty<V> property);

    /**
     * Set an explicit value for the given property.  Note that setting a
     * property to <code>null</code> is not the same as {@link #reset}ting it
     * to its default value &mdash; if <code>set(PROP, null)</code> is called, any
     * subsequent <code>get(PROP)</code> invocations must return null, even if
     * PROP has a default value.
     *
     * @see #reset
     */
    <V> void set(ConfigurationProperty<V> property, V value);

    /**
     * @return true if the property has an explicit value (including when the explicit value is null),
     *      otherwise false
     * @see #set
     */
    boolean isSet(ConfigurationProperty<?> property);

    /**
     * Clear any explicitly set value for the given property.  Subsequent calls to {@link #get}
     * will return the default value for the property (or null if there is no default).
     * @see #set
     */
    <V> void reset(ConfigurationProperty<V> property);

    /**
     * Return a read-only map interface to the configuration data.  The keys are
     * the configuration property keys and the values are the same values returned
     * from {@link #get}.  The map is provided as alternate access scheme for
     * non-Java languages which have a special syntax for maps (e.g., JSP).
     *
     * @see gov.nih.nci.cabig.ctms.tools.configuration.DefaultConfigurationMap
     */
    Map<String, Object> getMap();
}
