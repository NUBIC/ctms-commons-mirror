/**
 * 
 */
package gov.nih.nci.cabig.ctms.acegi.grid.authorization;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class GridUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory
			.getLogger(GridUserDetailsService.class);

	private GridGroupSearch gridGroupSearch;

	public GridGroupSearch getGridGroupSearch() {
		return gridGroupSearch;
	}

	public void setGridGroupSearch(GridGroupSearch gridGroupSearch) {
		this.gridGroupSearch = gridGroupSearch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String identity)
			throws UsernameNotFoundException, DataAccessException {

		List<String> groupNames = null;
		try {
			groupNames = getGridGroupSearch().getGridGroupNames(identity);
		} catch (Exception ex) {
			throw new RuntimeException("Error getting group names: "
					+ ex.getMessage(), ex);
		}

		logger.debug("User is member of " + groupNames.size() + " groups.");

		GrantedAuthority[] auths = new GrantedAuthority[groupNames.size()];
		int idx = 0;
		for (Iterator i = groupNames.iterator(); i.hasNext(); idx++) {
			String groupName = (String) i.next();
			logger.debug("Adding group: " + groupName);
			auths[idx] = new GrantedAuthorityImpl(groupName);
		}

		return new User(identity, "ignored", true, true, true, true, auths);
	}

}
