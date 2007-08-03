package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Ensures that the configured webapp is running.
 * <ul>
 *   <li>If it's not deployed, it deploys it</li>
 *   <li>If it's stopped, it starts it</li>
 *   <li>If it's running, it reloads it</li>
 * </ul>
 *
 * @author Rhett Sutphin
 * @goal go
 */
public class GoMojo extends AbstractConditionalMojo {
    @Override
    protected void executeInternal() throws MojoExecutionException, MojoFailureException {
        WebApplication app = findThisApplication();
        if (app == null) {
            deploy();
        } else if (app.isStopped()) {
            start();
        } else {
            reload();
        }
    }
}
