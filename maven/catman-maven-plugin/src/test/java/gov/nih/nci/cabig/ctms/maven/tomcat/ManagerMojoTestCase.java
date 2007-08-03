package gov.nih.nci.cabig.ctms.maven.tomcat;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.List;
import java.util.Arrays;
import java.io.File;

/**
 * @author Rhett Sutphin
 */
public abstract class ManagerMojoTestCase<M extends AbstractDeployMojo> extends TestCase {
    protected static final String PATH = "/widgets";
    protected static final File CONTEXT_XML = new File("/home/dev/widgets/context.xml");
    protected static final ManagerResponse<String> OKAY_RESPONSE = new ManagerResponse<String>("OK - It's okay");

    private TomcatManager manager;
    private M mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = createMock(TomcatManager.class);

        mojo = createMojo();
        mojo.setPath(PATH);
        mojo.setContextFile(CONTEXT_XML);
        mojo.setTomcatManager(manager);
    }

    protected abstract M createMojo();

    public M getMojo() {
        return mojo;
    }

    public TomcatManager getManager() {
        return manager;
    }

    protected void doExecute() throws MojoFailureException, MojoExecutionException {
        replay(getManager());
        getMojo().execute();
        verify(getManager());
    }

    protected ManagerResponse<List<WebApplication>> createListResponse(WebApplication... expectedList) {
        ManagerResponse<List<WebApplication>> expectedResponse = new ManagerResponse<List<WebApplication>>("OK - really");
        expectedResponse.setPayload(Arrays.asList(expectedList));
        return expectedResponse;
    }
}
