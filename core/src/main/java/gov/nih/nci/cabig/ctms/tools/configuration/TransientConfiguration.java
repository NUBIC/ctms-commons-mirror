package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * {@link Configuration} implementation which does not persist changes
 * on {@link #set}.  Also provides methods for copying into and out of
 * another (presumably more permanent) {@link Configuration}.
 *
 * @author Rhett Sutphin
 */
public class TransientConfiguration extends AbstractConfiguration {
    private Map<String, ConfigurationEntry> entries;
    private ConfigurationProperties properties;

    public TransientConfiguration(ConfigurationProperties properties) {
        this.properties = properties;
        entries = new HashMap<String, ConfigurationEntry>();
    }

    public ConfigurationProperties getProperties() {
        return properties;
    }

    /**
     * Create a transient copy of the source configuration.
     * @param source
     */
    public static Configuration create(Configuration source) {
        return create(source, source.getProperties());
    }

    /**
     * Create a transient copy of the source configuration, using the specified set of properties.
     * @param source
     */
    public static Configuration create(Configuration source, ConfigurationProperties props) {
        TransientConfiguration copy = new TransientConfiguration(source.getProperties());
        copy.copyFrom(source, props.getAll());
        return copy;
    }

    @SuppressWarnings({ "unchecked" })
    public void copyFrom(Configuration source, Collection<ConfigurationProperty<?>> toCopy) {
        for (ConfigurationProperty<?> property : toCopy) {
            if (source.isSet(property)) {
                this.set((ConfigurationProperty<Object>) property, source.get(property));
            }
        }
    }

    /**
     * Copies values from this configuration to the target.  The list of properties
     * to copy is taken from the target.
     */
    @SuppressWarnings({ "unchecked" })
    public void copyTo(Configuration target, Collection<ConfigurationProperty<?>> toCopy) {
        for (ConfigurationProperty<?> property : toCopy) {
            if (isSet(property)) {
                target.set((ConfigurationProperty<Object>) property, this.get(property));
            } else {
                target.reset(property);
            }
        }
    }

    @Override
    protected <V> ConfigurationEntry getEntry(ConfigurationProperty<V> property) {
        return entries.get(property.getKey());
    }

    @Override
    protected void store(ConfigurationEntry entry) {
        entries.put(entry.getKey(), entry);
    }

    @Override
    protected void remove(ConfigurationEntry entry) {
        entries.remove(entry.getKey());
    }
}
