package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class ExampleConfiguration extends DatabaseBackedConfiguration {
    private static final DefaultConfigurationProperties PROPERTIES =
        new DefaultConfigurationProperties(new ClassPathResource("details.properties", ExampleConfiguration.class));

    public static final ConfigurationProperty<String> SMTP_HOST
        = PROPERTIES.add(new DefaultConfigurationProperty.Text("smtpHost"));
    public static final ConfigurationProperty<Integer> SMTP_PORT
        = PROPERTIES.add(new DefaultConfigurationProperty.Int("smtpPort"));
    public static final ConfigurationProperty<List<String>> ADDRESSES
        = PROPERTIES.add(new DefaultConfigurationProperty.Csv("addresses"));

    public ConfigurationProperties getProperties() {
        return PROPERTIES;
    }
}
