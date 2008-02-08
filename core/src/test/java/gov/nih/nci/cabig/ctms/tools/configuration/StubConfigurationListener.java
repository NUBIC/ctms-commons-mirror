package gov.nih.nci.cabig.ctms.tools.configuration;

/**
 * @author Rhett Sutphin
 */
public class StubConfigurationListener implements ConfigurationListener {
    private ConfigurationEvent lastUpdate;

    public void configurationUpdated(ConfigurationEvent update) {
        lastUpdate = update;
    }

    public ConfigurationEvent getLastUpdate() {
        return lastUpdate;
    }
}
