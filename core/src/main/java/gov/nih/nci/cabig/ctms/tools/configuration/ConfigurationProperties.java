package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Map;
import java.util.TreeMap;
import java.util.Properties;
import java.util.Collection;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import gov.nih.nci.cabig.ctms.CommonsSystemException;

/**
 * Captures a collection of {@link ConfigurationProperty}s for a certain
 * application.  Handles loading a .properties file containing default values
 * and the human-readable names and descriptions for each property.
 * <p>
 * This details properties file should contain up to three entries for each
 * <code>ConfigurationProperty</code> in the system.  The property names are
 * the the configuration property key, followed by a period, followed by either
 * <code>default</code>, <code>name</code>, or <code>description</code>.  For
 * example, if you had added configuration property like this:
 * <pre>configurationProperties.add(new ConfigurationProperty.Int("smtpPort"))</pre>
 * Your properties file might contain lines like this:
 * <pre>smtpPort.default=25
 * smtpPort.name=Outgoing e-mail server (SMTP) port
 * smtpPort.description=The port on which to communicate with the SMTP server</pre>
 *
 * The three properties are:
 * <dl>
 *   <dt>name</dt>
 *     <dd>The human-readable name for the configuration property.  Required.</dd>
 *   <dt>default</dt>
 *     <dd>The default value for the configuration property (if it should have a non-null default).
 *         Optional.</dd>
 *   <dt>description</dt>
 *     <dd>A longer human-readable description, suitable for inline help.  Optional.</dd>
 * </dl>
 *
 * @author Rhett Sutphin
 * @see DatabaseBackedConfiguration
 */
public class ConfigurationProperties {
    private Map<String, ConfigurationProperty<?>> props = new TreeMap<String, ConfigurationProperty<?>>();
    private Properties details;

    /**
     * Loads the details properties from the given resource.  A typical use might be
     * <pre>public class MyAppConfiguration extends DatabaseBackedConfiguration {
     *     private static final ConfigurationProperties PROPERTIES
     *         = new ConfigurationProperties(new ClassPathResource("details.properties", MyAppConfiguration.class));
     *     // ...
     * }</pre>
     *
     * This would look for a file named "details.properties" in the same package as
     * <pre>MyAppConfiguration</pre>.
     *
     * @see org.springframework.core.io.ClassPathResource
     */
    public ConfigurationProperties(Resource detailsProperties) {
        this(loadDetails(detailsProperties));
    }

    protected ConfigurationProperties(Properties detailsProperties) {
        this.details = detailsProperties;
    }

    private static Properties loadDetails(Resource resource) {
        Properties details = new Properties();
        try {
            details.load(resource.getInputStream());
        } catch (IOException e) {
            throw new CommonsSystemException("Failed to load property details from " + resource, e);
        }
        return details;
    }

    public <V> ConfigurationProperty<V> add(ConfigurationProperty<V> prop) {
        props.put(prop.getKey(), prop);
        prop.setCollection(this);
        return prop;
    }

    public int size() {
        return props.size();
    }

    public ConfigurationProperty<?> get(String key) {
        return props.get(key);
    }

    public Collection<ConfigurationProperty<?>> getAll() {
        return props.values();
    }

    public boolean containsKey(String key) {
        return props.containsKey(key);
    }

    public String getNameFor(String key) {
        return getDetails().getProperty(key + ".name");
    }

    public String getDescriptionFor(String key) {
        return getDetails().getProperty(key + ".description");
    }

    public String getStoredDefaultFor(String key) {
        return getDetails().getProperty(key + ".default");
    }

    private Properties getDetails() {
        return details;
    }

    public static ConfigurationProperties union(ConfigurationProperties... items) {
        Properties mergedDetails = new Properties();
        for (ConfigurationProperties collection : items) {
            mergedDetails.putAll(collection.getDetails());
        }
        ConfigurationProperties union = new ConfigurationProperties(mergedDetails);
        for (ConfigurationProperties collection : items) {
            for (ConfigurationProperty<?> property : collection.getAll()) {
                union.add(property.clone());
            }
        }
        return union;
    }

    public static ConfigurationProperties empty() {
        return new Empty();
    }

    private static class Empty extends ConfigurationProperties {
        public Empty() {
            super(new Properties());
        }

        public <V> ConfigurationProperty<V> add(ConfigurationProperty<V> prop) {
            throw new UnsupportedOperationException("add not supported for empty property list");
        }
    }
}
