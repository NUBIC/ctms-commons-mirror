package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.DomainObject;

/**
 * @author Rhett Sutphin
 */
public interface DomainObjectDao<T extends DomainObject> {
    Class<T> domainClass();

    /**
     * Return the existing object with the given ID, or null if there isn't one.
     */
    T getById(int id);
}
