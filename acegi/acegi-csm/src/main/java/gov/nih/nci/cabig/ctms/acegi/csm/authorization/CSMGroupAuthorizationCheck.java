package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSException;
import org.acegisecurity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CSMGroupAuthorizationCheck extends AbstractCSMAuthorizationCheck {

	private String requiredPermission;

	private static final Logger logger = LoggerFactory
			.getLogger(CSMGroupAuthorizationCheck.class);

	public boolean checkAuthorizationForObjectId(Authentication authentication,
			String privilege, String objectId) {

		String checkPrivilege = privilege;
		if (checkPrivilege == null) {
			checkPrivilege = getRequiredPermission();
		}

		boolean isAuthorized = false;
		try {
			List groups = getCsmUserProvisioningManager().getAccessibleGroups(
					objectId, checkPrivilege);
			if (groups == null) {
				logger.debug("found no groups for " + objectId);
			} else {
				for (Iterator i = groups.iterator(); i.hasNext();) {
					Group group = (Group) i.next();
					if (authentication != null
							&& isMember(authentication, group
									.getGroupName())) {
						isAuthorized = true;
						break;
					}
				}
			}
		} catch (CSException e) {
            logger.debug("Ignoring error while finding groups", e);
		}
		return isAuthorized;
	}

	protected boolean isMember(Authentication authentication, String groupName) {
		
		boolean isMember = false;
		
		String userId = authentication.getName();
		UserProvisioningManager mgr = (UserProvisioningManager) getCsmUserProvisioningManager();
		Set groups = null;
		try {
			User user = mgr.getUser(userId);
			if (user != null) {
				groups = mgr.getGroups(user.getUserId().toString());
			}
		} catch (CSObjectNotFoundException ex) {
			throw new RuntimeException("Error getting groups: "
					+ ex.getMessage(), ex);
		}

		if (groups != null) {
			for (Iterator i = groups.iterator(); i.hasNext();) {
				Group group = (Group) i.next();
				if (group.getGroupName().equals(groupName)) {
					isMember = true;
					break;
				}
			}
			logger.debug("isMember? " + isMember);
		} else {
			logger.debug("found no groups for user " + userId);
		}
		return isMember;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}

	public void setRequiredPermission(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}

}
