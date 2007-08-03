package gov.nih.nci.cabig.ctms.maven;

import junit.framework.TestCase;

import java.util.Properties;
import java.util.Collection;

import org.apache.maven.project.MavenProject;

/**
 * @author Rhett Sutphin
 */
public class ApplyMojoTest extends TestCase {
    private ApplyMojo mojo;
    private MavenProject project;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = new MavenProject();

        mojo = new ApplyMojo();
        mojo.setProject(project);
        mojo.setBeanXml("<bean class='" + TestingFactoryBean.class.getName() + "'/>");
    }

    public void testReadXmlConfig() throws Exception {
        Properties actual = mojo.effectiveProperties();
        assertNotNull("No properties found", actual);
        assertEquals("Wrong properties found", TestingFactoryBean.PROPERTIES, actual);
    }

    public void testExecute() throws Exception {
        mojo.execute();
        Collection<Object> projectPropertyNames = project.getProperties().keySet();
        for (Object testPropName : TestingFactoryBean.PROPERTIES.keySet()) {
            assertTrue("Property " + testPropName + " not added to project", projectPropertyNames.contains(testPropName));
            assertEquals("Property " + testPropName + " has wrong value in project",
                TestingFactoryBean.PROPERTIES.get(testPropName),
                project.getProperties().get(testPropName));
        }
    }
}
