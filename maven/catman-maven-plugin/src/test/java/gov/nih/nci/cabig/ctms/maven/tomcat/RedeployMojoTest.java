package gov.nih.nci.cabig.ctms.maven.tomcat;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class RedeployMojoTest extends ManagerMojoTestCase<RedeployMojo> {
    @Override
    protected RedeployMojo createMojo() {
        return new RedeployMojo();
    }

    public void testRedeployWhenDeployed() throws Exception {
        ManagerResponse<List<WebApplication>> expectedResponse = createListResponse(
            new WebApplication("/someotherapp", "running", 0, "someotherapp"),
            new WebApplication(PATH, "running", 0, "widgets")
        );
        expect(getManager().list()).andReturn(expectedResponse);
        expect(getManager().undeploy(PATH)).andReturn(OKAY_RESPONSE);
        expect(getManager().deploy(PATH, CONTEXT_XML)).andReturn(OKAY_RESPONSE);

        doExecute();
    }

    public void testRedeployWhenNotDeployed() throws Exception {
        ManagerResponse<List<WebApplication>> expectedList = createListResponse(
            new WebApplication("/someotherapp", "running", 0, "someotherapp")
        );
        expect(getManager().list()).andReturn(expectedList);
        expect(getManager().deploy(PATH, CONTEXT_XML)).andReturn(OKAY_RESPONSE);

        doExecute();
    }

}
