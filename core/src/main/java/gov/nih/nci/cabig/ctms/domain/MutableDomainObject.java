package gov.nih.nci.cabig.ctms.domain;

/**
 * @author Rhett Sutphin
 * @author Sujith Vellat Thayyilthodi
 */
public interface MutableDomainObject extends DomainObject, GridIdentifiable {
    /**
     * @return the optimistic locking version value for this object.
     */
    Integer getVersion();

    /**
     * Set the optimistic locking version value for this object.  In practice this should not be
     * called by application code -- just the persistence mechanism.
     * @param version
     */
    void setVersion(Integer version);
}
