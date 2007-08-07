package gov.nih.nci.cabig.ctms.audit.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author Padmaja Vedula
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "audit_event_values")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "audit_event_values_id_seq") })
public class DataAuditEventValue {

	/** The id. */
	@Id
	@GeneratedValue(generator = "id-generator")
	@Column(name = "id")
	private Integer id;

	/** The version. */
	@Version
	private Integer version;

	/**
	 * {@inheritDoc}
	 */
	public Integer getId() {
		return id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "audit_event_id")
	private DataAuditEvent auditEvent;

	@Column(name = "attribute_name")
	private String attributeName;

	@Column(name = "previous_value")
	private String previousValue;

	@Column(name = "new_value")
	private String currentValue;

	/* for hibernate */
	protected DataAuditEventValue() {
	}

	public DataAuditEventValue(final String attributeName, final String previousValue, final String currentValue) {
		this.attributeName = attributeName;
		this.previousValue = previousValue;
		this.currentValue = currentValue;
	}

	// //// BEAN PROPERTIES

	public String getAttributeName() {
		return attributeName;
	}

	public DataAuditEvent getAuditEvent() {
		return auditEvent;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public void setAuditEvent(final DataAuditEvent auditEvent) {
		this.auditEvent = auditEvent;
	}

}
