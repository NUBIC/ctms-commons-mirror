package gov.nih.nci.cabig.ctms.audit;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Author: mmhohman Date: Jan 11, 2004
 * 
 * Last Checkin: $Author: rsutphin $ Date: $Date: 2004/09/13 23:35:12 $ Revision: $Revision: 1.11 $
 */
public class DataAuditInfo implements Serializable, Cloneable {
	private volatile static ThreadLocal<DataAuditInfo> localAuditInfo = new ThreadLocal<DataAuditInfo>();

	private String by;

	private String ip;

	private Date on;

	public DataAuditInfo() {
	}

	public DataAuditInfo(final String by, final String ip) {
		this(by, ip, new Timestamp(System.currentTimeMillis()));
	}

	public DataAuditInfo(final String by, final String ip, final Date on) {
		this.by = by;
		this.ip = ip;
		this.on = on;
	}

	public static DataAuditInfo getLocal() {
		return localAuditInfo.get();
	}

	public static void setLocal(final DataAuditInfo auditInfo) {
		localAuditInfo.set(auditInfo);
	}

	public String getBy() {
		return by;
	}

	public void setBy(final String by) {
		this.by = by;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(final String ip) {
		this.ip = ip;
	}

	public Date getOn() {
		return on;
	}

	public void setOn(final Date on) {
		this.on = on;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DataAuditInfo)) {
			return false;
		}

		final DataAuditInfo dataAuditInfo = (DataAuditInfo) o;

		if (getBy() != null ? !getBy().equals(dataAuditInfo.getBy()) : dataAuditInfo.getBy() != null) {
			return false;
		}
		if (getIp() != null ? !getIp().equals(dataAuditInfo.getIp()) : dataAuditInfo.getIp() != null) {
			return false;
		}
		if (getOn() != null ? !getOn().equals(dataAuditInfo.getOn()) : dataAuditInfo.getOn() != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		result = getBy() != null ? getBy().hashCode() : 0;
		result = 29 * result + (getIp() != null ? getIp().hashCode() : 0);
		result = 29 * result + (getOn() != null ? getOn().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return new StringBuffer().append(getClass().getName()).append('[').append(getBy()).append(", ").append(getIp())
				.append(", ").append(getOn()).append(']').toString();
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new Error("Clone is supported", e);
		}
	}
}
