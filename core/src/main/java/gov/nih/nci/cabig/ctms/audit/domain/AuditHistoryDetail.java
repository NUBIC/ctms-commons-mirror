package gov.nih.nci.cabig.ctms.audit.domain;

/**
 * Value object for audit history details
 * @author Saurabh Agrawal
 */
public class AuditHistoryDetail {

    private AuditHistory auditHistory;

    private String attributeName;

    private String previousValue;

    private String currentValue;

    protected AuditHistoryDetail() {
    }

    public AuditHistoryDetail(final String attributeName, final String previousValue, final String currentValue) {
        this.attributeName = attributeName;
        this.previousValue = previousValue;
        this.currentValue = currentValue;
    }

    // //// BEAN PROPERTIES

    public String getAttributeName() {
        return attributeName;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public AuditHistory getAuditHistory() {
        return auditHistory;
    }

    public void setAuditHistory(final AuditHistory auditHistory) {
        this.auditHistory = auditHistory;
    }

}
