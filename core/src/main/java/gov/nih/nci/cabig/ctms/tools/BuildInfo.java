package gov.nih.nci.cabig.ctms.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rhett Sutphin
 */
public class BuildInfo {
    private static final String DEFAULT_APPLICATION_NAME = "Untitled CTMS application";
    private static final ThreadLocal<DateFormat> TIMESTAMP_FORMAT = new ThreadLocal<DateFormat>();

    private String applicationName;
    private String versionNumber;

    private String username;
    private String hostname;
    private Date timestamp;

    ////// LOGIC

    public String getBuildName() {
        StringBuilder name = new StringBuilder();
        if (getApplicationName() == null) {
            name.append(DEFAULT_APPLICATION_NAME);
        } else {
            name.append(getApplicationName());
        }
        name.append(' ');
        if (getVersionNumber() == null) {
            name.append("[unknown SNAPSHOT]");
        } else {
            name.append("v. ").append(getVersionNumber());
        }

        if (isSnapshot()) {
            name.append(" (");

            boolean hasUser = getUsername() != null;
            boolean hasHost = getHostname() != null;
            if (hasUser) name.append(getUsername());
            if (hasUser && hasHost) name.append('@');
            if (hasHost) name.append(getHostname());
            if (hasUser || hasHost) name.append(' ');

            if (getTimestamp() == null) {
                name.append("[unknown time]");
            } else {
                name.append(getTimestampFormat().format(getTimestamp()));
            }
            name.append(')');
        }

        return name.toString();
    }

    protected DateFormat getTimestampFormat() {
        DateFormat format = TIMESTAMP_FORMAT.get();
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TIMESTAMP_FORMAT.set(format);
        }
        return format;
    }

    public boolean isSnapshot() {
        return versionNumber == null
            || versionNumber.toUpperCase().endsWith("SNAPSHOT") 
            || versionNumber.toUpperCase().endsWith(".DEV");
    }

    ////// BEAN PROPERTIES

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    ////// OBJECT METHODS

    @Override
    public String toString() {
        return getBuildName();
    }
}
