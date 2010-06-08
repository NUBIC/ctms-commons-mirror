package gov.nih.nci.cabig.ctms.suite.authorization.domain;

/**
 * @author Rhett Sutphin
 */
public class TestSite {
    private String ident;

    public TestSite(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }
}
