package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.provisioning.AuthorizationManagerImpl;

import java.util.HashMap;
import java.util.Map;

public class UserProvisioningManagerFactory {
    public static AuthorizationManager newUserProvisioningManager(String contextName, Map props) {
        try {
            return new AuthorizationManagerImpl(contextName, new HashMap(props));
        } catch (CSConfigurationException ex) {
            throw new RuntimeException("Error instantiating UserProvisioningManager", ex);
        }
    }
}
