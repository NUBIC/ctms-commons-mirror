package gov.nih.nci.cabig.ctms.maven.tomcat;

/**
 * @author Rhett Sutphin
 */
public class ManagerResponse<P> {
    private P payload;
    private String statusLine;

    public ManagerResponse(String statusLine) {
        this.statusLine = statusLine;
        this.payload = null;
    }

    public static ManagerResponse<String> createRawResponse(String responseBody) {
        int nlIndex = responseBody.indexOf('\n');
        String firstLine;
        String remainder;
        if (nlIndex >= 0) {
            firstLine = responseBody.substring(0, nlIndex);
            remainder = responseBody.substring(nlIndex).trim();
        } else {
            firstLine = responseBody;
            remainder = null;
        }

        ManagerResponse<String> raw = new ManagerResponse<String>(firstLine);
        raw.setPayload(remainder);
        return raw;
    }

    ////// LOGIC

    public boolean isOK() {
        return statusLine.startsWith("OK");
    }

    public String getStatusMessage() {
        return statusLine.split(" - ")[1];
    }

    ////// BEAN ACCESSORS

    public String getStatusLine() {
        return statusLine;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }
}
