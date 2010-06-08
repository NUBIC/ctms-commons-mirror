/**
 * 
 */
package gov.nih.nci.cabig.ctms.acegi.grid.authorization;

import gov.nih.nci.cabig.ctms.acegi.csm.authorization.CSMGroupAuthorizationCheck;
import gov.nih.nci.cagrid.authorization.GridGroupName;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class CSMGridGroupAuthorizationCheck extends CSMGroupAuthorizationCheck {

	private static final Logger logger = LoggerFactory.getLogger(CSMGridGroupAuthorizationCheck.class);
	
	protected boolean isMember(Authentication authentication, String groupName){
		boolean isMember = false;
	
		logger.debug("Checking membership in '" + groupName + "' for '" + authentication.getName() + "'");
		
		isMember = super.isMember(authentication, groupName);
		if(!isMember && GridGroupName.isGridGroupName(groupName)){
			GridGroupName gridGroupName = null;
			try {
				gridGroupName = new GridGroupName(groupName);
			} catch (MalformedURLException ex) {
				throw new RuntimeException("Error parsing '" + groupName + "': " + ex.getMessage(), ex);
			}
			logger.debug("Checking grid group membership");
			for(GrantedAuthority authority : authentication.getAuthorities()){
				logger.debug("comparing '" + gridGroupName.getName() + "' with '" + authority.getAuthority() + "'");
				if(authority.getAuthority().equals(gridGroupName.getName())){
					isMember = true;
					break;
				}
			}
		}
		
		return isMember;
	}

}
