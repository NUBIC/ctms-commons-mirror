package gov.nih.nci.cabig.ctms.domain;

import javax.persistence.MappedSuperclass;
import javax.persistence.Id;

/**
 * This class provides a default implementation of {@link DomainObject} alone, suitable
 * for use with immutable objects (e.g., lookup table entries).
 *
 * @author Rhett Sutphin
 */
@MappedSuperclass
public class AbstractImmutableDomainObject implements DomainObject {
    private Integer id;

    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
