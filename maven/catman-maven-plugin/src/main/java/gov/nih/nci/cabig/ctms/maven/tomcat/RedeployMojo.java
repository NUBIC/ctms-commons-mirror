package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

/**
 * @author Rhett Sutphin
 * @goal redeploy
 */
public class RedeployMojo extends AbstractConditionalMojo {
    @Override
    protected void executeInternal() throws MojoExecutionException, MojoFailureException {
        WebApplication thisApp = findThisApplication();
        if (thisApp != null) undeploy();

        deploy();
    }
}
