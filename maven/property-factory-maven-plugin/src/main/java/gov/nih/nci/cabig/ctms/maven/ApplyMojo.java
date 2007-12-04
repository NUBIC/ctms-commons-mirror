package gov.nih.nci.cabig.ctms.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Properties;
import java.util.Collection;

/**
 * @author Rhett Sutphin
 * @goal apply
 */
public class ApplyMojo extends AbstractMojo {
    /**
     * An XML fragment describing how to create the property factory.  The syntax is the same as
     * a <code>&lt;bean&gt;</code> element in a spring XML application context.
     *
     * @parameter
     * @required
     */
    private String beanXml;

    /**
     * The project to which to apply the properties.
     *
     * @parameter expression="${project}"
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Properties toApply = effectiveProperties();
            if (getLog().isDebugEnabled()) getLog().debug("Applying properties from factory to project:");
            for (Object o : toApply.keySet()) {
                String propName = (String) o;
                String propValue = toApply.getProperty(propName);
                project.getProperties().setProperty(propName, propValue);
                if (getLog().isDebugEnabled()) {
                    getLog().debug(String.format("    %s=%s", propName, propValue));
                }
            }
            getLog().info(String.format("Applied %d properties from factory to project", toApply.size()));
        } catch (RuntimeException e) {
            // rethrow any spring or other exception so that it is handled by
            // maven's display code (i.e., stacktrace only shown with -e or -X)
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    // package-level for testing
    Properties effectiveProperties() throws MojoExecutionException {
        XmlBeanFactory beanFactory = new XmlBeanFactory(createResource());
	Collection beans = beanFactory.getBeansOfType(Properties.class).values();
	if (beans.isEmpty()) throw new MojoExecutionException("Provided XML fragment does not define any bean of type Properties.");
        return (Properties) beans.iterator().next();
    }

    private Resource createResource() {
        StringBuilder doc = new StringBuilder();
        doc.append("<beans xmlns='http://www.springframework.org/schema/beans'")
            .append(" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'")
            .append(" xsi:schemaLocation='http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd'")
            .append(">\n")
            .append(beanXml)
            .append("\n</beans>");

        if (getLog().isDebugEnabled()) {
            getLog().debug("Loading property factory using XML\n" + doc);
        }

        return new ByteArrayResource(doc.toString().getBytes());
    }

    ////// BEAN PROPERTIES (for testing)

    public String getBeanXml() {
        return beanXml;
    }

    public void setBeanXml(String beanXml) {
        this.beanXml = beanXml;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
}
