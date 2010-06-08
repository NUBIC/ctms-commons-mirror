package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import gov.nih.nci.security.UserProvisioningManager;
import org.acegisecurity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractCSMAuthorizationCheck implements
		CSMAuthorizationCheck {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractCSMAuthorizationCheck.class);

	private AuthorizationSwitch authorizationSwitch;

	private CSMObjectIdGenerator objectIdGenerator;

	private UserProvisioningManager csmUserProvisioningManager;

	public UserProvisioningManager getCsmUserProvisioningManager() {
		return csmUserProvisioningManager;
	}

	public void setCsmUserProvisioningManager(
			UserProvisioningManager csmUserProvisioningManager) {
		this.csmUserProvisioningManager = csmUserProvisioningManager;
	}

	public CSMObjectIdGenerator getObjectIdGenerator() {
		return objectIdGenerator;
	}

	public void setObjectIdGenerator(CSMObjectIdGenerator objectIdGenerator) {
		this.objectIdGenerator = objectIdGenerator;
	}

	public boolean checkAuthorization(Authentication authentication,
			String privilege, Object object) {
		boolean isAuthorized = false;
		if (!getAuthorizationSwitch().isOn()) {
			
			logger.warn("###### AuthorizationSwitch is OFF #####");
            if (logger.isDebugEnabled()) {
                logger.debug("Specifically, " + getAuthorizationSwitch() +" is the one that's off");
            }

			isAuthorized = true;
		} else {
			
			if (logger.isDebugEnabled()) {
                logger.debug("###### " + getAuthorizationSwitch() +" is ON #####");
            }
			
			if (object != null) {

				Collection collection = null;
				if (object.getClass().isArray()) {
					collection = Arrays.asList((Object[]) object);
				} else if (object instanceof Collection) {
					collection = (Collection) object;
				} else {
					collection = new ArrayList();
					collection.add(object);
				}

				String[] objectIds = new String[collection.size()];
				int idx = 0;
				for (Iterator i = collection.iterator(); i.hasNext(); idx++) {
					objectIds[idx] = getObjectIdGenerator()
							.generateId(i.next());
				}
				isAuthorized = checkAuthorizationForObjectIds(authentication,
						privilege, objectIds);

			}
		}

		return isAuthorized;
	}

	public boolean checkAuthorizationForObjectIds(
			Authentication authentication, String privilege, String[] objectIds) {

		boolean isAuthorized = true;
		for (int i = 0; i < objectIds.length && isAuthorized; i++) {
			isAuthorized = checkAuthorizationForObjectId(authentication,
					privilege, objectIds[i]);
		}
		return isAuthorized;

	}

	public AuthorizationSwitch getAuthorizationSwitch() {
		return authorizationSwitch;
	}

    @Required
    public void setAuthorizationSwitch(AuthorizationSwitch authorizationSwitch) {
		this.authorizationSwitch = authorizationSwitch;
	}

}
