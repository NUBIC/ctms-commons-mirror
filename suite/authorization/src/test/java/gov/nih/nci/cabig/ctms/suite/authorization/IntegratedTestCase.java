package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.InputStream;

/**
 * @author Rhett Sutphin
 */
public abstract class IntegratedTestCase extends DBTestCase {
    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream data = IntegratedTestCase.class.getResourceAsStream("shared-testdata.xml");
        if (data == null) {
            throw new IllegalStateException("Could not find shared testdata file");
        }
        return new FlatXmlDataSet(data);
    }

    @Override
    protected IDatabaseTester newDatabaseTester() throws Exception {
        return CsmIntegratedTestHelper.createDatabaseTester();
    }

    protected AuthorizationManager getAuthorizationManager() {
        return CsmIntegratedTestHelper.getAuthorizationManager();
    }
}
