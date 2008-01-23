package gov.nih.nci.cabig.ctms.audit.domain;

import javax.persistence.Embeddable;
import java.util.Date;

/**
 * Subclass of core-commons' DataAuditInfo that aliases the "on" property as "time". This is so that it can be used in HQL queries ("on" is
 * apparently a reserved word).
 *
 * Also adds "url" property.
 *
 * @author Rhett Sutphin
 */
// TODO: the separation between this class and its superclass is unnecessary.  Remove the superclass.
public class DataAuditInfo extends gov.nih.nci.cabig.ctms.audit.DataAuditInfo {
    public static DataAuditInfo copy(final gov.nih.nci.cabig.ctms.audit.DataAuditInfo source) {
        DataAuditInfo copy = new DataAuditInfo(source.getBy(), source.getIp(), source.getOn());
        if (source instanceof DataAuditInfo) {
            copy.setUrl(((DataAuditInfo) source).getUrl());
        }
        return copy;
    }

    private String url;
    private String username;
    private String ip;
    private Date time;

    public DataAuditInfo() {
    }

    public DataAuditInfo(final String by, final String ip, final Date on) {
        this.ip = ip;
        username = by;
        time = on;
    }

    public DataAuditInfo(final String by, final String ip, final Date on, final String url) {
        this(by, ip, on);
        this.url = url;
    }

    @Override
    public String getIp() {
        return ip;
    }

    public Date getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void setIp(final String ip) {
        this.ip = ip;
    }

    public void setTime(final Date on) {
        time = on;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    //// Override superclass properties, too

    @Override
    @Deprecated
    public String getBy() {
        return getUsername();
    }

    @Override
    @Deprecated
    public void setBy(final String by) {
        setUsername(by);
    }

    @Override
    @Deprecated
    public Date getOn() {
        return getTime();
    }

    @Override
    @Deprecated
    public void setOn(final Date on) {
        setTime(on);
    }

    @Override
    public String toString() {
        return new StringBuffer(getClass().getName())
            .append('[').append(getUsername())
            .append(", ").append(getIp())
            .append(", ").append(getTime())
            .append(", ").append(getUrl())
            .append(']').toString();
    }
}
