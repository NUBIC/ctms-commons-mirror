package gov.nih.nci.cabig.ccts.security;

import gov.nih.nci.cabig.ctms.acegi.csm.authorization.CSMUserDetailsService;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class CSMUserDetailsServiceImpl extends CSMUserDetailsService {
    private static final Log logger = LogFactory.getLog(CSMUserDetailsServiceImpl.class);

    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
        logger.debug((new StringBuilder()).append("Getting user details for ").append(userName).toString());

        logger.debug(">>> Get user:" + userName);

        GrantedAuthority authorities[] = null;
        UserProvisioningManager mgr = getCsmUserProvisioningManager();
        gov.nih.nci.security.authorization.domainobjects.User loadedUser = null;
        Set groups;

        logger.debug(">>> Manager: " + mgr);

/*
        boolean accountNonExpired = true;
        Date today = new Date();
*/

        try {
            loadedUser = mgr.getUser(userName);
            logger.debug(">>> LoadedUser: " + loadedUser);
            if (loadedUser == null) {
                throw new UsernameNotFoundException("User does not exist in CSM.");
            }
            logger.debug((new StringBuilder()).append("Retrieved user obj ").append(loadedUser).append(" with ID ").append(loadedUser != null ? ((Object) (loadedUser.getUserId())) : "<null>").toString());
            groups = mgr.getGroups(loadedUser.getUserId().toString());
        } catch (CSObjectNotFoundException ex) {
            throw new AuthenticationServiceException((new StringBuilder()).append("Error getting groups: ").append(ex.getMessage()).toString(), ex);
        }

        if (groups == null || groups.size() == 0) {
            authorities = new GrantedAuthority[0];
        } else {
            authorities = new GrantedAuthority[groups.size() + 1];
            int idx = 0;
            for (Iterator i = groups.iterator(); i.hasNext();) {
                Group group = (Group) i.next();
                authorities[idx] = new GrantedAuthorityImpl(group.getGroupName());
                logger.debug(">>> Authority: " + group.getGroupName());
                idx++;
            }

            logger.debug(">>> Found: " + groups.size() + " authorities.");
            authorities[idx] = new GrantedAuthorityImpl("ROLE_USER");
            logger.debug(">>> Total authorities: " + groups.size());
        }

        User user = new User(userName, "ignored_password", true, true, true, true, authorities);
        return user;
    }

}
