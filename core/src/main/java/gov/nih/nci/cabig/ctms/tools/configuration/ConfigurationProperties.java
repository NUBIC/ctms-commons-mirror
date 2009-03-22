package gov.nih.nci.cabig.ctms.tools.configuration;

import java.util.Collection;

/**
 * Defines a collection of {@link DefaultConfigurationProperty}s for a certain
 * application.
 * 
 * @author Rhett Sutphin
 */
public interface ConfigurationProperties {
    int size();

    ConfigurationProperty<?> get(String key);

    Collection<ConfigurationProperty<?>> getAll();

    boolean containsKey(String key);

    String getNameFor(String key);

    String getDescriptionFor(String key);

    String getStoredDefaultFor(String key);
}
