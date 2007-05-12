package gov.nih.nci.cabig.ctms.domain;

/**
 * @author Sujith Vellat Thayyilthodi
 * @author Rhett Sutphin
 */
public interface GridIdentifiable {
    /**
     * @return the grid-scoped unique identifier for this object
     */
    String getGridId();

    /**
     * Specify the grid-scoped unique identifier for this object
     * @param gridId
     */
    void setGridId(String gridId);

    /**
     * Return true if the gridId is not null else return false.
     * */
    boolean hasGridId();
}
