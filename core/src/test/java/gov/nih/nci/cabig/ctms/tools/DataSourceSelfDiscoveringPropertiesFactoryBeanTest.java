package gov.nih.nci.cabig.ctms.tools;

import gov.nih.nci.cabig.ctms.CommonsConfigurationException;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.util.Properties;

import static gov.nih.nci.cabig.ctms.tools.DataSourceSelfDiscoveringPropertiesFactoryBean.*;

/**
 * @author Rhett Sutphin
 */
public class DataSourceSelfDiscoveringPropertiesFactoryBeanTest extends TestCase {
    private DataSourceSelfDiscoveringPropertiesFactoryBean factoryBean;
    private File thisDir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        thisDir = new File(getClass().getResource("/").toURI());
        System.setProperty("catalina.base",
            new File(thisDir, "../resources/catalina_base").getCanonicalPath());
        System.setProperty("catalina.home",
            new File(thisDir, "../resources/catalina_home").getCanonicalPath());

        factoryBean = new TestDataSourcePropertiesFactoryBean();
        factoryBean.setDatabaseConfigurationName("empty");
    }

    public void testDefaultPropertiesFoundAndBound() throws Exception {
        factoryBean.setDatabaseConfigurationName(null);
        assertDefaultLoadedProperties();
    }

    public void testEmptyConfNameUsesDefault() throws Exception {
        factoryBean.setDatabaseConfigurationName(" \t");
        assertDefaultLoadedProperties();
    }

    public void testDbConfigNameUsed() throws Exception {
        factoryBean.setDatabaseConfigurationName("alternate");
        assertLoadedProperties("jdbc:db:alternate", "java.lang.Long", "alt", "tla");
    }

    public void testDefaultDialectForPostgreSQLFromRDBMSProperty() throws Exception {
        factoryBean.getDefaults().setProperty(RDBMS_PROPERTY_NAME, "PostgreSQL");
        assertEquals(DEFAULT_POSTGRESQL_DIALECT,
            getActualProperties().getProperty(HIBERNATE_DIALECT_PROPERTY_NAME));
    }

    public void testDefaultDialectForPostgreSQLFromDriver() throws Exception {
        factoryBean.getDefaults().setProperty(DRIVER_PROPERTY_NAME, "org.postgresql.Driver");
        assertEquals(DEFAULT_POSTGRESQL_DIALECT,
            getActualProperties().getProperty(HIBERNATE_DIALECT_PROPERTY_NAME));
    }

    public void testExplicitHibernateDialectTrumps() throws Exception {
        String expectedDialect = "org.hibernate.dialect.PostgreSQLDialect";
        factoryBean.getDefaults().setProperty(RDBMS_PROPERTY_NAME, "PostgreSQL");
        factoryBean.getDefaults().setProperty(HIBERNATE_DIALECT_PROPERTY_NAME, expectedDialect);
        Assert.assertEquals(expectedDialect, getActualProperties().getProperty(HIBERNATE_DIALECT_PROPERTY_NAME));
    }

    public void testExceptionIfNoDirectoryMatched() throws Exception {
        factoryBean.setApplicationDirectoryName("vesuvius");
        try {
            factoryBean.getObject();
            fail("Exception not thrown");
        } catch (CommonsConfigurationException cce) {
            assertTrue("Exception has wrong message: " + cce.getMessage(),
                cce.getMessage().startsWith("Datasource configuration not found.  Looked in ["));
            assertTrue("Exception missing path: " + cce.getMessage(),
                cce.getMessage().contains(new File("/etc/vesuvius/empty.properties").getAbsolutePath()));
        }
    }

    public void testExceptionNotThrownInNullTolerantMode() throws Exception {
        factoryBean.setApplicationDirectoryName("vesuvius");
        factoryBean.setNullTolerant(true);
        assertNull(factoryBean.getObject());
    }

    public void testSearchLocationsPreservedInNullTolerantMode() throws Exception {
        factoryBean.setApplicationDirectoryName("vesuvius");
        factoryBean.setNullTolerant(true);
        factoryBean.getObject();
        
        assertNotNull("Locations not preserved", factoryBean.getSearchedLocations());
        assertEquals("Wrong number of locations", 4, factoryBean.getSearchedLocations().size());
        assertTrue("Missing one of the expected paths: " + factoryBean.getSearchedLocations(),
            factoryBean.getSearchedLocations().contains(new File("/etc/vesuvius/empty.properties").getAbsolutePath()));
    }

    public void testCatalinaBasePreferredOverCatalinaHome() throws Exception {
        factoryBean.setApplicationDirectoryName("etna");
        factoryBean.setDatabaseConfigurationName(null);
        assertLoadedProperties("jdbc:db:etna", "java.lang.String", "base-user", "pass-default");
    }

    private void assertLoadedProperties(
        String expectedUrl, String expectedDriver, String expectedUser, String expectedPass
    ) throws Exception {
        Properties actual = getActualProperties();
        Assert.assertEquals("Wrong URL", expectedUrl, actual.getProperty(URL_PROPERTY_NAME));
        Assert.assertEquals("Wrong driver", expectedDriver, actual.getProperty(DRIVER_PROPERTY_NAME));
        Assert.assertEquals("Wrong user", expectedUser, actual.getProperty(USERNAME_PROPERTY_NAME));
        Assert.assertEquals("Wrong password", expectedPass, actual.getProperty(PASSWORD_PROPERTY_NAME));
    }

    private void assertDefaultLoadedProperties() throws Exception {
        // these values are in datasource.properties
        assertLoadedProperties("jdbc:db:default", "java.lang.String", "default-user", "pass-default");
    }

    private Properties getActualProperties() throws Exception {
        return (Properties) factoryBean.getObject();
    }
}
