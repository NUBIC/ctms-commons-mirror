package gov.nih.nci.cabig.ctms.tools.spring;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class HibernatePropertiesFactoryBeanTest extends TestCase {
    private HibernatePropertiesFactoryBean factoryBean;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factoryBean = new HibernatePropertiesFactoryBean();
    }

    public void testPropertiesDefaultNotNull() throws Exception {
        assertNotNull(factoryBean.getProperties());
    }

    public void testGetObjectDefaultNotNull() throws Exception {
        assertNotNull(factoryBean.getObject());
    }

    public void testHibernateDialectAddedIfSet() throws Exception {
        factoryBean.setDialectName("jojo");
        assertEquals("jojo", ((Properties) factoryBean.getObject()).getProperty("hibernate.dialect"));
    }

    public void testPropertiesIncludedInObject() throws Exception {
        factoryBean.getProperties().setProperty("A", "zed");
        factoryBean.setDialectName("zappa");
        Properties actual = (Properties) factoryBean.getObject();
        assertEquals("Wrong number of properties", 2, actual.size());
        assertEquals("zed", actual.getProperty("A"));
        assertEquals("zappa", actual.getProperty("hibernate.dialect"));
    }
}
