package gov.nih.nci.cabig.ctms.maven.tomcat;

import static org.easymock.EasyMock.expect;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class GoMojoTest extends ManagerMojoTestCase<GoMojo> {
    @Override
    protected GoMojo createMojo() {
        return new GoMojo();
    }

    public void testRunWhenNotDeployed() throws Exception {
        ManagerResponse<List<WebApplication>> expectedList = createListResponse(
            new WebApplication("/someotherapp", "running", 0, "someotherapp")
        );
        expect(getManager().list()).andReturn(expectedList);
        expect(getManager().deploy(PATH, CONTEXT_XML)).andReturn(OKAY_RESPONSE);

        doExecute();
    }
    
    public void testRunWhenStopped() throws Exception {
        ManagerResponse<List<WebApplication>> expectedResponse = createListResponse(
            new WebApplication("/someotherapp", "running", 0, "someotherapp"),
            new WebApplication(PATH, "stopped", 0, "widgets")
        );
        expect(getManager().list()).andReturn(expectedResponse);
        expect(getManager().start(PATH)).andReturn(OKAY_RESPONSE);

        doExecute();
    }

    public void testRunWhenRunning() throws Exception {
        ManagerResponse<List<WebApplication>> expectedResponse = createListResponse(
            new WebApplication("/someotherapp", "running", 0, "someotherapp"),
            new WebApplication(PATH, "running", 0, "widgets")
        );
        expect(getManager().list()).andReturn(expectedResponse);
        expect(getManager().reload(PATH)).andReturn(OKAY_RESPONSE);

        doExecute();
    }
}
