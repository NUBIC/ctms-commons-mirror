package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.acegisecurity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nih.nci.security.exceptions.CSException;

public class CSMUserAuthorizationCheck extends AbstractCSMAuthorizationCheck {
	
	private String requiredPermission;
	
	private static final Logger logger = LoggerFactory.getLogger(CSMUserAuthorizationCheck.class);
	
	public boolean checkAuthorizationForObjectId(Authentication authentication, String privilege, String objectId) {
		boolean isAuthorized = false;
		try{
			if(authentication != null && getCsmUserProvisioningManager().checkPermission(authentication.getName(), objectId, privilege)){
				isAuthorized = true;
			}
		} catch (CSException e) {
            logger.debug("Ignoring error while checking permission", e);
		}
		return isAuthorized;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}

	public void setRequiredPermission(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}

}
