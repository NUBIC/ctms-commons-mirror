package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.GridIdentifiable;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

/**
 * @author Rhett Sutphin
 */
public interface GridIdentifiableDao<T extends GridIdentifiable & DomainObject> extends DomainObjectDao<T> {
    /**
     * Return the existing object with the given grid ID, or null if there isn't one.
     */
    T getByGridId(String gridId);
}
