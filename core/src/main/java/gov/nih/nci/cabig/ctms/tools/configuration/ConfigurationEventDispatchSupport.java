package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Rhett Sutphin
 */
public class ConfigurationEventDispatchSupport {
    private Set<ConfigurationListener> listeners;
    private Configuration configuration;

    public ConfigurationEventDispatchSupport(Configuration configuration) {
        this.configuration = configuration;
        this.listeners = new LinkedHashSet<ConfigurationListener>();
    }

    public void dispatchUpdate(ConfigurationProperty<?> property) {
        ConfigurationEvent event = new ConfigurationEvent(configuration, property);
        for (ConfigurationListener listener : listeners) {
            listener.configurationUpdated(event);
        }
    }

    public void addListener(ConfigurationListener listener) {
        listeners.add(listener);
    }
}
