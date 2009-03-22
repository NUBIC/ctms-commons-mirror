package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.core.io.ClassPathResource;

/**
 * @author Rhett Sutphin
 */
public class AlternateConfiguration extends DatabaseBackedConfiguration {
    private static final DefaultConfigurationProperties PROPERTIES =
        new DefaultConfigurationProperties(new ClassPathResource("details.properties", AlternateConfiguration.class));

    public static final ConfigurationProperty<String> SMTP_HOST
        = PROPERTIES.add(new DefaultConfigurationProperty.Text("smtpHost"));

    public ConfigurationProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected Class<? extends ConfigurationEntry> getConfigurationEntryClass() {
        return AlternateConfigurationEntry.class;
    }
}
