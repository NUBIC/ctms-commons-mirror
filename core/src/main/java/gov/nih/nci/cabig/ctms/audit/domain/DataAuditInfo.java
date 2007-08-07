package gov.nih.nci.cabig.ctms.audit.domain;

import java.util.Date;

/**
 * Subclass of core-commons' DataAuditInfo that aliases the "on" property as "time". This is so that it can be used in HQL queries ("on" is
 * apparently a reserved word).
 * 
 * Also adds "url" property.
 * 
 * @author Rhett Sutphin
 */
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
		setBy(by);
		setOn(on);
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

}
