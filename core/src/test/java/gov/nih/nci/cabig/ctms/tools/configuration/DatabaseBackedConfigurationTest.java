package gov.nih.nci.cabig.ctms.tools.configuration;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class DatabaseBackedConfigurationTest extends CommonsTestCase {
    private DatabaseBackedConfiguration configuration;
    private SessionFactory sessionFactory;
    private JdbcTemplate jdbc;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sessionFactory = new AnnotationConfiguration()
            .addAnnotatedClass(ConfigurationEntry.class)
            .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
            .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:test" + Math.random())
            .setProperty("hibernate.connection.username", "sa")
            .setProperty("hibernate.connection.password", "")
            .buildSessionFactory();
        configuration = new ExampleConfiguration();
        configuration.setSessionFactory(sessionFactory);
        SingleConnectionDataSource ds
            = new SingleConnectionDataSource(sessionFactory.openSession().connection(), false);
        ds.setAutoCommit(true);
        jdbc = new JdbcTemplate(ds);

        jdbc.execute(
            "CREATE TABLE configuration (key VARCHAR(255) PRIMARY KEY, value VARCHAR(255), version INTEGER DEFAULT '0' NOT NULL)");
    }

    @Override
    protected void tearDown() throws Exception {
        jdbc.getDataSource().getConnection().close();
        sessionFactory.close();
        super.tearDown();
    }

    private void insertPair(String key, String value) throws SQLException {
        jdbc.execute(String.format(
            "INSERT INTO configuration (key, value) VALUES ('%s', '%s')", key, value));
    }

    public void testGetExisting() throws Exception {
        insertPair(ExampleConfiguration.SMTP_PORT.getKey(), "34");
        assertEquals("Value not loaded", 34,
            (int) configuration.get(ExampleConfiguration.SMTP_PORT));
    }

    public void testGetDefaultIfNotSet() throws Exception {
        assertEquals("Test setup failure: missing expected default", 25,
            (int) ExampleConfiguration.SMTP_PORT.getDefault());
        assertEquals("Default not returned", 25,
            (int) configuration.get(ExampleConfiguration.SMTP_PORT));
    }
    
    public void testGetNullIfNotSetAndNoDefault() throws Exception {
        assertNull(configuration.get(ExampleConfiguration.ADDRESSES));
    }

    public void testSetProperty() throws Exception {
        configuration.set(ExampleConfiguration.SMTP_HOST, "mail.example.net");
        assertStoredValue("mail.example.net", ExampleConfiguration.SMTP_HOST);
    }

    public void testSetNullWorks() throws Exception {
        insertPair(ExampleConfiguration.SMTP_PORT.getKey(), "17");
        assertStoredValue("17", ExampleConfiguration.SMTP_PORT);

        configuration.set(ExampleConfiguration.SMTP_PORT, null);
        assertStoredValue(null, ExampleConfiguration.SMTP_PORT);
    }

    public void testMap() throws Exception {
        insertPair("smtpPort", "55");

        Map<String, Object> actual = configuration.getMap();
        assertEquals("Concrete value doesn't appear in the map", 55,
            actual.get("smtpPort"));
        assertEquals("Default value does not appear in the map", "localhost",
            actual.get("smtpHost"));
    }

    public void testMapReturnsNullForMissing() throws Exception {
        assertNull(configuration.getMap().get("bogus"));
    }

    private <V> void assertStoredValue(final String expected, ConfigurationProperty<V> property) {
        final int[] count = new int[1];
        jdbc.query("SELECT value FROM configuration WHERE key=?",
            new Object[] { property.getKey() }, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                assertEquals(expected, rs.getString("value"));
                count[0]++;
            }
        } );
        assertEquals("Wrong number of values found", 1, count[0]);
    }
}
