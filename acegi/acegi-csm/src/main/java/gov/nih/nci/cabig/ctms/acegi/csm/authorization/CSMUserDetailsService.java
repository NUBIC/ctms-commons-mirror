package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import java.util.Iterator;
import java.util.Set;

public class CSMUserDetailsService implements UserDetailsService {
    private static final Log log = LogFactory.getLog(CSMUserDetailsService.class);

    private String rolePrefix = "ROLE_";

	private UserProvisioningManager csmUserProvisioningManager;

	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
        log.debug("Getting user details for " + userName);
		GrantedAuthority[] authorities = null;

		UserProvisioningManager mgr = getCsmUserProvisioningManager();
		Set groups;
		try {
            gov.nih.nci.security.authorization.domainobjects.User loadedUser = mgr.getUser(userName);
            if(loadedUser == null){
                throw new UsernameNotFoundException("User does not exist in CSM.");
            }
            log.debug("Retrieved user obj " + loadedUser + " with ID " + (loadedUser == null ? "<null>" : loadedUser.getUserId()));
            groups = mgr.getGroups(loadedUser.getUserId().toString());
		} catch (CSObjectNotFoundException ex) {
			throw new AuthenticationServiceException("Error getting groups: "
					+ ex.getMessage(), ex);
		}
		if (groups == null || groups.size() == 0) {
			authorities = new GrantedAuthority[0];
		} else {
			String prefix = getRolePrefix();
			if (prefix == null) {
				prefix = "";
			}
			authorities = new GrantedAuthority[groups.size()];
			int idx = 0;
			for (Iterator i = groups.iterator(); i.hasNext(); idx++) {
				Group group = (Group) i.next();
				authorities[idx] = new GrantedAuthorityImpl(prefix
						+ group.getGroupName());
			}
		}

		return new User(userName, "ignored", true, true, true, true,
				authorities);

	}

	public String getRolePrefix() {
		return rolePrefix;
	}

	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	public UserProvisioningManager getCsmUserProvisioningManager() {
		return csmUserProvisioningManager;
	}

    @Required
    public void setCsmUserProvisioningManager(UserProvisioningManager userProvisioningManager) {
		this.csmUserProvisioningManager = userProvisioningManager;
	}
}
