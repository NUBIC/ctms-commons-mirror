package gov.nih.nci.cabig.ctms.maven.tomcat;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.io.File;

/**
 * @author Rhett Sutphin
 */
public class TomcatManagerTest extends TestCase {
    private static final String USERNAME = "joe";
    private static final String PASSWORD = "dc";
    private static final String URL = "http://localhost:9999/manager";

    private TestableTomcatManger manager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = new TestableTomcatManger();
    }
    
    public void testCreatedHttpClient() throws Exception {
        TomcatManager realManager = new TomcatManager(URL, USERNAME, PASSWORD);
        HttpState actualState = realManager.getHttp().getState();
        assertTrue(actualState.isAuthenticationPreemptive());
        Credentials actualCreds = actualState.getCredentials(TomcatManager.MANAGER_REALM, "localhost");
        assertNotNull(actualCreds);
        assertTrue(actualCreds instanceof UsernamePasswordCredentials);
        assertEquals(USERNAME, ((UsernamePasswordCredentials) actualCreds).getUserName());
        assertEquals(PASSWORD, ((UsernamePasswordCredentials) actualCreds).getPassword());
    }

    public void testStart() throws Exception {
        manager.expectHttp(URL + "/start?path=/tomcat-docs",
            "OK - Started application at context path /tomcat-docs");

        ManagerResponse<?> actual = manager.start("/tomcat-docs");
        assertTrue(actual.isOK());
    }

    public void testStop() throws Exception {
        manager.expectHttp(URL + "/stop?path=/tomcat-docs",
            "OK - Stopped application at context path /tomcat-docs");

        ManagerResponse<?> actual = manager.stop("/tomcat-docs");
        assertTrue(actual.isOK());
    }

    public void testReload() throws Exception {
        manager.expectHttp(URL + "/reload?path=/tomcat-docs",
            "OK - Stopped application at context path /tomcat-docs");

        ManagerResponse<?> actual = manager.reload("/tomcat-docs");
        assertTrue(actual.isOK());
    }

    public void testUndeploy() throws Exception {
        manager.expectHttp(URL + "/undeploy?path=/tomcat-docs",
            "OK - Undeployed application at context path /tomcat-docs");

        ManagerResponse<?> actual = manager.undeploy("/tomcat-docs");
        assertTrue(actual.isOK());
    }

    public void testDeployWithContextXml() throws Exception {
        manager.expectHttp(URL + "/deploy?config=file:/home/tomcat/dev/context.xml&path=/local-app",
            "OK - Deployed application at context path /local-app");

        ManagerResponse<?> actual = manager.deploy("/local-app", new File("/home/tomcat/dev/context.xml"));
        assertTrue(actual.isOK());
    }

    public void testListResults() throws Exception {
        // This is the body from an actual Tomcat 5.5.17 instance
        String body = "OK - Listed applications for virtual host localhost\n" +
            "/webdav:running:0:webdav\n" +
            "/servlets-examples:running:1:servlets-examples\n" +
            "/jsp-examples:running:0:jsp-examples\n" +
            "/balancer:stopped:0:balancer\n" +
            "/host-manager:running:0:/usr/local/apache-tomcat-5.5.17/server/webapps/host-manager\n" +
            "/tomcat-docs:running:0:tomcat-docs\n" +
            "/:running:0:ROOT\n" +
            "/manager:running:0:/usr/local/apache-tomcat-5.5.17/server/webapps/manager";
        manager.expectHttp(URL + "/list", body);

        ManagerResponse<List<WebApplication>> actual = manager.list();

        assertTrue(actual.isOK());
        assertEquals("Listed applications for virtual host localhost", actual.getStatusMessage());
        assertEquals("Wrong number of applications listed", 8, actual.getPayload().size());
        assertWebApplication("/webdav", "running", 0, "webdav", actual.getPayload().get(0));
        assertWebApplication("/servlets-examples", "running", 1, "servlets-examples", actual.getPayload().get(1));
        assertWebApplication("/jsp-examples", "running", 0, "jsp-examples", actual.getPayload().get(2));
        assertWebApplication("/balancer", "stopped", 0, "balancer", actual.getPayload().get(3));
        assertWebApplication("/host-manager", "running", 0, "/usr/local/apache-tomcat-5.5.17/server/webapps/host-manager", actual.getPayload().get(4));
        assertWebApplication("/tomcat-docs", "running", 0, "tomcat-docs", actual.getPayload().get(5));
        assertWebApplication("/", "running", 0, "ROOT", actual.getPayload().get(6));
        assertWebApplication("/manager", "running", 0, "/usr/local/apache-tomcat-5.5.17/server/webapps/manager", actual.getPayload().get(7));
    }

    public void testList404s() throws Exception {
        manager.expectHttp(URL + "/list", null, 404);

        try {
            manager.list();
            fail("Exception not thrown");
        } catch (ManagerConnectionException mce) {
            assertEquals(mce.getMessage(), "Could not connect to " + URL + "/list (HTTP 404)");
        }
    }

    private void assertWebApplication(
        String expectedDeployedPath, String expectedStatus, int expectedSessionCount,
        String expectedFilePath, WebApplication actual
    ) {
        assertEquals("Wrong deployed path", expectedDeployedPath, actual.getDeployedPath());
        assertEquals("Wrong status", expectedStatus, actual.getStatusText());
        assertEquals("Wrong session count", expectedSessionCount, (int) actual.getSessionCount());
        assertEquals("Wrong file path", expectedFilePath, actual.getFilePath());
    }

    private static class TestableTomcatManger extends TomcatManager {
        private List<HttpStruct> expectedReqs;

        public TestableTomcatManger() {
            super(URL, USERNAME, PASSWORD);
            expectedReqs = new LinkedList<HttpStruct>();
        }

        @Override
        protected HttpClient createHttpClient() {
            return new MockHttpClient();
        }

        public void expectHttp(String uri, String responseBody, Integer responseCode) {
            try {
                expectedReqs.add(new HttpStruct(new URI(uri), responseBody, responseCode));
            } catch (URISyntaxException e) {
                throw new Error("Test setup problem -- bad URI string: " + uri, e);
            }
        }

        public void expectHttp(String uri, String responseBody) {
            expectHttp(uri, responseBody, 200);
        }

        @Override
        protected HttpMethod createGetMethod(URI uri) {
            HttpStruct nextExpected = pop(uri);
            return new MockHttpMethod(nextExpected);
        }

        private HttpStruct pop(URI uri) {
            HttpStruct next = expectedReqs.remove(0);
            assertEquals("Next requested URI wasn't the next expected", next.getUri(), uri);
            return next;
        }
    }

    private static class HttpStruct {
        private URI uri;
        private int code;
        private String body;

        public HttpStruct(URI uri, String body, int code) {
            this.uri = uri;
            this.body = body;
            this.code = code;
        }

        public URI getUri() {
            return uri;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
    }

    private static class MockHttpClient extends HttpClient {
        @Override
        public int executeMethod(HttpMethod httpMethod) throws IOException, HttpException {
            return ((MockHttpMethod) httpMethod).getValues().getCode();
        }
    }

    private static class MockHttpMethod extends HttpMethodBase {
        private HttpStruct values;

        public MockHttpMethod(HttpStruct values) {
            this.values = values;
        }

        @Override
        public String getName() {
            return "MOCK";
        }

        public HttpStruct getValues() {
            return values;
        }

        @Override
        public String getResponseBodyAsString() {
            return values.getBody();
        }

        @Override
        public int getStatusCode() {
            return values.getCode();
        }
    }
}
