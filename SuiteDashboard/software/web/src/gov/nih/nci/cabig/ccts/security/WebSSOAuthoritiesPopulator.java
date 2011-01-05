package gov.nih.nci.cabig.ccts.security;

import gov.nih.nci.cagrid.common.Utils;
import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.cas.CasAuthoritiesPopulator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.log4j.Logger;
import org.cagrid.gaards.cds.client.CredentialDelegationServiceClient;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  Create a WebSSOUser object from CAS assertion 
 */
public class WebSSOAuthoritiesPopulator implements CasAuthoritiesPopulator {

    private String hostCertificate;
    private String hostKey;
    private UserDetailsService userDetailsService;

    public static final String ATTRIBUTE_DELIMITER = "$";
    public static final String KEY_VALUE_PAIR_DELIMITER = "^";
    public static final String CAGRID_SSO_FIRST_NAME = "CAGRID_SSO_FIRST_NAME";
    public static final String CAGRID_SSO_LAST_NAME = "CAGRID_SSO_LAST_NAME";
    public static final String CAGRID_SSO_GRID_IDENTITY = "CAGRID_SSO_GRID_IDENTITY";
    public static final String CAGRID_SSO_DELEGATION_SERVICE_EPR = "CAGRID_SSO_DELEGATION_SERVICE_EPR";

    Logger log = Logger.getLogger(WebSSOAuthoritiesPopulator.class);

    public UserDetails getUserDetails(String casUserId) throws AuthenticationException {
        
        Map<String, String> attrMap = new HashMap<String, String>();
        StringTokenizer stringTokenizer = new StringTokenizer(casUserId, ATTRIBUTE_DELIMITER);
        while (stringTokenizer.hasMoreTokens()) {
            String attributeKeyValuePair = stringTokenizer.nextToken();
            attrMap.put(attributeKeyValuePair.substring(0, attributeKeyValuePair
                    .indexOf(KEY_VALUE_PAIR_DELIMITER)), attributeKeyValuePair.substring(
                    attributeKeyValuePair.indexOf(KEY_VALUE_PAIR_DELIMITER) + 1,
                    attributeKeyValuePair.length()));
        }

        String gridIdentity = attrMap.get(CAGRID_SSO_GRID_IDENTITY);

        String userName = "";
        if (gridIdentity != null) {
            userName = gridIdentity.substring(gridIdentity.indexOf("/CN=") + 4, gridIdentity.length());
        } else {
            log.error(CAGRID_SSO_GRID_IDENTITY + " is null");
        }

        System.out.println("1." + userName);
        System.out.println("2." + casUserId);

        WebSSOUser user = null;
        try {
            UserDetails ud = userDetailsService.loadUserByUsername(userName);
            user = new WebSSOUser(ud);
        } catch (UsernameNotFoundException ex) {
            throw new AuthenticationCredentialsNotFoundException(ex.getMessage(), ex);
        }

        user.setGridId(gridIdentity);
        user.setDelegatedEPR(attrMap.get(CAGRID_SSO_DELEGATION_SERVICE_EPR));
        user.setFirstName(attrMap.get(CAGRID_SSO_FIRST_NAME));
        user.setLastName(attrMap.get(CAGRID_SSO_LAST_NAME));

        try {
            GlobusCredential hostCredential = new GlobusCredential(hostCertificate, hostKey);
            DelegatedCredentialReference delegatedCredentialReference = (DelegatedCredentialReference) Utils.deserializeObject(new StringReader(attrMap.get(CAGRID_SSO_DELEGATION_SERVICE_EPR)), DelegatedCredentialReference.class, CredentialDelegationServiceClient.class.getResourceAsStream("client-config.wsdd"));
            DelegatedCredentialUserClient delegatedCredentialUserClient = new DelegatedCredentialUserClient(delegatedCredentialReference, hostCredential);
            GlobusCredential userCredential = delegatedCredentialUserClient.getDelegatedCredential();
            user.setGridCredential(userCredential);
        } catch (Exception e) {
            log.error("Could not retrieve user credential from CDS service", e);
        }

        user.setOriginalUsername(userName);
        return user;
    }

    public String getHostCertificate() {
        return hostCertificate;
    }

    public void setHostCertificate(String hostCertificate) {
        this.hostCertificate = hostCertificate;
    }

    public String getHostKey() {
        return hostKey;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
