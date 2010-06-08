package gov.nih.nci.cabig.ctms.suite.authorization.domain;

/**
 * @author Rhett Sutphin
 */
public class TestStudy {
    private String ident;

    public TestStudy(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }
}
