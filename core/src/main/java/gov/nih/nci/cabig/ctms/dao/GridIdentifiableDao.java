package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.GridIdentifiable;

/**
 * @author Rhett Sutphin
 */
public interface GridIdentifiableDao<T extends GridIdentifiable> {
    /**
     * Return the existing object with the given grid ID, or null if there isn't one.
     */
    T getByGridId(String gridId);
}
