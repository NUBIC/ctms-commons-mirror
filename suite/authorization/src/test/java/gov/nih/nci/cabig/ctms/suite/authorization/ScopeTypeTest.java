package gov.nih.nci.cabig.ctms.suite.authorization;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class ScopeTypeTest extends TestCase {
    public void testAllScopeCsmName() throws Exception {
        assertEquals(ScopeType.SITE.getAllScopeCsmName(), "HealthcareSite");
        assertEquals(ScopeType.STUDY.getAllScopeCsmName(), "Study");
    }

    public void testSingleScopeCsmNamePrefix() throws Exception {
        assertEquals(ScopeType.SITE.getScopeCsmNamePrefix(), "HealthcareSite.");
        assertEquals(ScopeType.STUDY.getScopeCsmNamePrefix(), "Study.");
    }

    public void testGetDisplayName() throws Exception {
        assertEquals("site", ScopeType.SITE.getName());
        assertEquals("study", ScopeType.STUDY.getName());
    }

    public void testGetPluralName() throws Exception {
        assertEquals("sites", ScopeType.SITE.getPluralName());
        assertEquals("studies", ScopeType.STUDY.getPluralName());
    }
}
