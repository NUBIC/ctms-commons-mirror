package gov.nih.nci.cabig.ctms.audit.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Padmaja Vedula
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "audit_events")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_AUDIT_EVENTS_ID") })
public class DataAuditEvent {

    /** The id. */
    @Id
    @GeneratedValue(generator = "id-generator")
    @Column(name = "id")
    private Integer id;

    /** The version. */
    @Version
    private Integer version;

    public Integer getId() {
        return id;
    }

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "username", column = @Column(name = "user_name")),
        @AttributeOverride(name = "ip", column = @Column(name = "ip_address")),
        @AttributeOverride(name = "time", column = @Column(name = "time")) })
    private final DataAuditInfo info;

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "className", column = @Column(name = "class_name")),
        @AttributeOverride(name = "id", column = @Column(name = "object_id")) })
    private DataReference reference;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @OneToMany(mappedBy = "auditEvent")
    @javax.persistence.OrderBy
    // order by ID for testing consistency
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private final List<DataAuditEventValue> values = new ArrayList<DataAuditEventValue>();

    /* for Hibernate */
    public DataAuditEvent() {
        info = new DataAuditInfo();
        reference = new DataReference();
    }

    public DataAuditEvent(final Object entity, final Operation operation, final DataAuditInfo info) {
        reference = DataReference.create(entity);
        this.operation = operation;
        this.info = info;
    }

    public void addValue(final DataAuditEventValue value) {
        value.setAuditEvent(this);
        getValues().add(value);
    }

    public void addValues(final List<DataAuditEventValue> values) {
        for (DataAuditEventValue value : values) {
            addValue(value);
        }
    }

    public DataAuditInfo getInfo() {
        return info;
    }

    public Operation getOperation() {
        return operation;
    }

    public DataReference getReference() {
        return reference;
    }

    public List<DataAuditEventValue> getValues() {
        return values;
    }

    public void setReference(final DataReference reference) {
        this.reference = reference;
    }

}
