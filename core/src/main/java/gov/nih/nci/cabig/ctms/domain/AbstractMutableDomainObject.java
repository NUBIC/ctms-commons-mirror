package gov.nih.nci.cabig.ctms.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * This class provides a default implementation of MutableDomainObject.
 * It is annotated to be suitable for use as a mapped superclass with hibernate annotations,
 * though it can of course be used with traditional XML-based mapping as well.
 * <p>
 * If used with hibernate annotations, each concrete subclass will need to define
 * an ID generator called "id-generator".  For example:
 *
 * <pre>@GenericGenerator(name="id-generator", strategy = "native",
 *     parameters = {
 *         @Parameter(name="sequence", value="seq_mytable_id")
 *     }
 * )</pre>
 *
 * @author Rhett Sutphin
 */
@MappedSuperclass
public abstract class AbstractMutableDomainObject implements MutableDomainObject {
    private Integer id;
    private Integer version;
    private String gridId;

    @Id @GeneratedValue(generator = "id-generator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    ////// LOGIC

    public boolean hasGridId() {
        return getGridId() != null;
    }
}