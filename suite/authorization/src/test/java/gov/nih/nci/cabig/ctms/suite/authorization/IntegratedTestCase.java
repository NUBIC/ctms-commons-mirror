package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Rhett Sutphin
 */
public abstract class IntegratedTestCase extends DBTestCase {
    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getResourceAsStream("shared-testdata.xml"));
    }

    @Override
    protected IDatabaseTester newDatabaseTester() throws Exception {
        return CsmIntegratedTestHelper.createDatabaseTester();
    }

    protected AuthorizationManager getAuthorizationManager() {
        return CsmIntegratedTestHelper.getAuthorizationManager();
    }
}
