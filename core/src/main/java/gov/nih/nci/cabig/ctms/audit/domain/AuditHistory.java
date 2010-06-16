package gov.nih.nci.cabig.ctms.audit.domain;

import gov.nih.nci.cabig.ctms.audit.domain.AuditHistoryDetail;
import gov.nih.nci.cabig.ctms.audit.domain.Operation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Value object for audit history
 * @author Saurabh Agrawal
 */

public class AuditHistory {

    private String url;

    private String username;

    private String ip;

    private Date time;

    private final String className;

    private final Integer entityId;

    private final Operation operation;

    private final Integer auditEventId;

    private final List<AuditHistoryDetail> auditHistoryDetails = new ArrayList<AuditHistoryDetail>();

    public AuditHistory(final String className, final Integer entityId, final Operation operation,
                        final Integer auditEventId) {
        super();
        this.className = className;
        this.entityId = entityId;
        this.operation = operation;
        this.auditEventId = auditEventId;
    }

    public void addValue(final AuditHistoryDetail value) {
        value.setAuditHistory(this);
        getAuditHistoryDetails().add(value);
    }

    public void addValues(final List<AuditHistoryDetail> values) {
        for (AuditHistoryDetail value : values) {
            addValue(value);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    public String getClassName() {
        return className;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<AuditHistoryDetail> getAuditHistoryDetails() {
        return auditHistoryDetails;
    }

    public Integer getAuditEventId() {
        return auditEventId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (auditEventId == null ? 0 : auditEventId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditHistory other = (AuditHistory) obj;
        if (auditEventId == null) {
            if (other.auditEventId != null) {
                return false;
            }
        }
        else if (!auditEventId.equals(other.auditEventId)) {
            return false;
        }
        return true;
    }

}
