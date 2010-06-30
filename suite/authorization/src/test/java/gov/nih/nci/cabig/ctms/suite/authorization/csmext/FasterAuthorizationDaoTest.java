package gov.nih.nci.cabig.ctms.suite.authorization.csmext;

import gov.nih.nci.cabig.ctms.suite.authorization.CsmIntegratedTestHelper;
import gov.nih.nci.cabig.ctms.suite.authorization.IntegratedTestCase;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.Set;

/**
 * @author Rhett Sutphin
 */
// TODO: write separate (optional) performance tests
@SuppressWarnings({ "unchecked" })
public class FasterAuthorizationDaoTest extends IntegratedTestCase {
    public void testGetProtectionElementPrivilegeContextReturnsSameAsBaseForEve() throws Exception {
        verifyIdenticalContextsRetrieved(-22L);
    }

    public void testGetProtectionElementPrivilegeContextReturnsSameAsBaseForAlice() throws Exception {
        verifyIdenticalContextsRetrieved(-26L);
    }

    public void testGetProtectionElementPrivilegeContextEmptyButLoadableForLane() throws Exception {
        Set actual = CsmIntegratedTestHelper.getFasterAuthorizationDao().
            getProtectionElementPrivilegeContextForUser("-20");
        assertEquals("Should have no contexts", 0, actual.size());
    }

    private void verifyIdenticalContextsRetrieved(Long userId) throws CSObjectNotFoundException {
        Set<ProtectionElementPrivilegeContext> original = CsmIntegratedTestHelper.getAuthorizationDao().
            getProtectionElementPrivilegeContextForUser(userId.toString());
        assertFalse("Test setup failure: original has no contexts", original.isEmpty());
        Set<ProtectionElementPrivilegeContext> faster = CsmIntegratedTestHelper.getFasterAuthorizationDao().
            getProtectionElementPrivilegeContextForUser(userId.toString());

        assertEquals("Different number of contexts for original vs. faster",
            original.size(), faster.size());

        for (ProtectionElementPrivilegeContext oContext : original) {
            ProtectionElementPrivilegeContext fContext = findMatchingProtectionElementContext(oContext.getProtectionElement(), faster);
            assertNotNull("No context in faster results for original PE " + oContext.getProtectionElement(),
                fContext);

            assertEquals("Original and faster contexts for " + oContext.getProtectionElement() + " have different numbers of privs",
                oContext.getPrivileges().size(), fContext.getPrivileges().size());
            for (Privilege oPriv : ((Set<Privilege>) oContext.getPrivileges())) {
                assertTrue("Faster context for " + oContext.getProtectionElement() + " is missing privilege " + oPriv + ": " + fContext.getPrivileges(),
                    fContext.getPrivileges().contains(oPriv));
            }
        }
    }

    private static ProtectionElementPrivilegeContext findMatchingProtectionElementContext(
        ProtectionElement pe, Set<ProtectionElementPrivilegeContext> contexts
    ) {
        for (ProtectionElementPrivilegeContext context : contexts) {
            if (context.getProtectionElement().getObjectId().equals(pe.getObjectId())) return context;
        }
        return null;
    }
}
