package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.MutableDomainObject;

/**
 * @author Rhett Sutphin
 */
public interface MutableDomainObjectDao<T extends MutableDomainObject>
    extends DomainObjectDao<T>, GridIdentifiableDao<T>
{
    /**
     * Save (or update) the given object.
     */
    void save(T obj);
}
