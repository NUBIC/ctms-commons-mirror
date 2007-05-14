package gov.nih.nci.cabig.ctms.editors;

import gov.nih.nci.cabig.ctms.domain.GridIdentifiable;
import gov.nih.nci.cabig.ctms.dao.GridIdentifiableDao;

/**
 * A {@link java.beans.PropertyEditor} that supports binding domain objects by their IDs or their
 * grid IDs.
 *
 * @see DaoBasedEditor
 * @see GridIdentifiableDao
 * @author Rhett Sutphin
 */
public class GridIdentifiableDaoBasedEditor extends DaoBasedEditor {
    private GridIdentifiableDao<?> gridDao;

    public GridIdentifiableDaoBasedEditor(GridIdentifiableDao<?> dao) {
        super(dao);
        gridDao = dao;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            super.setAsText(text);
            return;
        } catch (IllegalArgumentException iae) {
            // Fall through
        }

        GridIdentifiable value = gridDao.getByGridId(text);
        if (value == null) {
            throw new IllegalArgumentException("There is no "
                + gridDao.domainClass().getSimpleName() + " with id or gridId " + text);
        } else {
            setValue(value);
        }
    }
}
