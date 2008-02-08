package gov.nih.nci.cabig.ctms.tools.configuration;

/**
 * @author Rhett Sutphin
 */
public interface ConfigurationListener {
    void configurationUpdated(ConfigurationEvent update);
}
