package gov.nih.nci.cabig.ctms.maven;

import org.springframework.beans.factory.FactoryBean;

import java.util.Properties;

/**
 * @author Rhett Sutphin
 */
public class TestingFactoryBean implements FactoryBean {
    public static final Properties PROPERTIES = new Properties();
    static {
        PROPERTIES.setProperty("angel", "valencia");
        PROPERTIES.setProperty("matinee", "death");
    }

    public Object getObject() throws Exception {
        return PROPERTIES;
    }

    public Class getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
