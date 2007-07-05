package gov.nih.nci.cabig.ctms.tools.configuration;

import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class ExampleConfiguration extends DatabaseBackedConfiguration {
    private static final ConfigurationProperties PROPERTIES =
        new ConfigurationProperties(new ClassPathResource("details.properties", ExampleConfiguration.class));

    public static final ConfigurationProperty<String> SMTP_HOST
        = PROPERTIES.add(new ConfigurationProperty.Text("smtpHost"));
    public static final ConfigurationProperty<Integer> SMTP_PORT
        = PROPERTIES.add(new ConfigurationProperty.Int("smtpPort"));
    public static final ConfigurationProperty<List<String>> ADDRESSES
        = PROPERTIES.add(new ConfigurationProperty.Csv("addresses"));

    @Override
    public ConfigurationProperties getProperties() {
        return PROPERTIES;
    }
}
