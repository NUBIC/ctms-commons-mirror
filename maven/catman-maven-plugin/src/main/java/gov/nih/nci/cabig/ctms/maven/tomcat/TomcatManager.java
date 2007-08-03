package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.io.File;

/**
 * Programmatic interface for interacting with the tomcat manager utility.
 *
 * @author Rhett Sutphin
 */
public class TomcatManager {
    public static final String MANAGER_REALM = "Tomcat Manager Application";

    private URI uri;
    private String username;
    private String password;

    private HttpClient http;

    public TomcatManager(String uri, String username, String password) {
        this.uri = createUri(uri);
        this.username = username;
        this.password = password;
        http = createHttpClient();
    }

    private URI createUri(String uriText) {
        if (!uriText.endsWith("/")) uriText = uriText + '/';
        try {
            return new URI(uriText);
        } catch (URISyntaxException e) {
            // TODO: make specific
            throw new RuntimeException("Invalid URI: " + uriText, e);
        }
    }

    protected HttpClient createHttpClient() {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(MANAGER_REALM, uri.getHost(),
            new UsernamePasswordCredentials(username, password));
        httpClient.getState().setAuthenticationPreemptive(true);
        return httpClient;
    }

    public ManagerResponse<List<WebApplication>> list() {
        ManagerResponse<String> raw = execute("list");
        String[] rows = raw.getPayload().split("\n");
        List<WebApplication> apps = new ArrayList<WebApplication>(rows.length);
        for (String row : rows) {
            String[] cols = row.split(":");
            apps.add(new WebApplication(cols[0], cols[1], new Integer(cols[2]), cols[3]));
        }
        ManagerResponse<List<WebApplication>> response = new ManagerResponse<List<WebApplication>>(raw.getStatusLine());
        response.setPayload(apps);
        return response;
    }

    public ManagerResponse<String> deploy(String path, File contextXml) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("config", contextXml.toURI().toString());
        params.put("path", path);
        return execute("deploy", params);
    }

    public ManagerResponse<String> start(String path) {
        return execute("start", Collections.singletonMap("path", path));
    }

    public ManagerResponse<String> stop(String path) {
        return execute("stop", Collections.singletonMap("path", path));
    }

    public ManagerResponse<String> reload(String path) {
        return execute("reload", Collections.singletonMap("path", path));
    }

    public ManagerResponse<String> undeploy(String path) {
        return execute("undeploy", Collections.singletonMap("path", path));
    }

    private ManagerResponse<String> execute(String command) {
        return execute(command, Collections.<String, String>emptyMap());
    }

    private ManagerResponse<String> execute(String command, Map<String, String> parameters) {
        if (parameters.size() > 0) {
            StringBuilder qs = new StringBuilder("?");
            for (Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry =  it.next();
                qs.append(entry.getKey()).append('=').append(entry.getValue());
                if (it.hasNext()) qs.append('&');
            }
            command += qs.toString();
        }
        URI cmd = uri.resolve(command);

        HttpMethod get = createGetMethod(cmd);
        try {
            http.executeMethod(get);
        } catch (IOException e) {
            throw new ManagerConnectionException("Error while connecting to " + cmd, e);
        }

        int status = get.getStatusCode();
        String result = get.getResponseBodyAsString();
        if (status == 200) {
            return ManagerResponse.createRawResponse(result);
        } else {
            throw new ManagerConnectionException("Could not connect to " + cmd + " (HTTP " + status + ')');
        }
    }

    protected HttpMethod createGetMethod(URI target) {
        return new GetMethod(target.toString());
    }

    ////// ACCESSORS FOR TESTING ONLY

    HttpClient getHttp() {
        return http;
    }
}
