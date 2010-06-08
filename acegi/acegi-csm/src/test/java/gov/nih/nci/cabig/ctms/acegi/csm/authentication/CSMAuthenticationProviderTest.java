package gov.nih.nci.cabig.ctms.acegi.csm.authentication;

import junit.framework.TestCase;
import org.acegisecurity.providers.dao.UserCache;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.Authentication;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.GrantedAuthority;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;
import gov.nih.nci.security.AuthenticationManager;
import gov.nih.nci.security.exceptions.CSLoginException;

import javax.security.auth.Subject;

/**
 * @author Rhett Sutphin
 */
public class CSMAuthenticationProviderTest extends TestCase {
    private static final String USERNAME = "joe";
    private static final String PASSWORD = "secret-secret";
    private static final UserDetails USER_DETAILS
        = new User(USERNAME, PASSWORD, true, true, true, true, new GrantedAuthority[0]);
    private static final UsernamePasswordAuthenticationToken AUTHENTICATION
        = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private UserCache userCache;
    private CSMAuthenticationProvider provider;

    public void setUp() throws Exception {
        super.setUp();
        authenticationManager = createMock(AuthenticationManager.class);
        userDetailsService = createMock(UserDetailsService.class);
        userCache = createMock(UserCache.class);

        provider = new CSMAuthenticationProvider();
        provider.setCsmAuthenticationManager(authenticationManager);
        provider.setUserCache(userCache);
        provider.setUserDetailsService(userDetailsService);

        userCache.putUserInCache(USER_DETAILS);
        expectLastCall().anyTimes();
    }

    private void replayMocks() {
        replay(authenticationManager);
        replay(userDetailsService);
        replay(userCache);
    }

    private void verifyMocks() {
        verify(authenticationManager);
        verify(userDetailsService);
        verify(userCache);
    }

    public void testAuthenticateVerifiedUncached() throws Exception {
        expect(userCache.getUserFromCache(USERNAME)).andReturn(null);
        expect(authenticationManager.authenticate(USERNAME, PASSWORD)).andReturn(new Subject());
        expect(userDetailsService.loadUserByUsername(USERNAME)).andReturn(USER_DETAILS);

        replayMocks();
        Authentication response = provider.authenticate(AUTHENTICATION);
        verifyMocks();

        assertNotNull("User not authenticated", response);
        assertNotNull("Principal missing", response.getPrincipal());
        assertSame("Principal wrong", USER_DETAILS, response.getPrincipal());
    }

    public void testAuthenticateVerifiedCached() throws Exception {
        expect(userCache.getUserFromCache(USERNAME)).andReturn(USER_DETAILS);
        expect(authenticationManager.authenticate(USERNAME, PASSWORD)).andReturn(new Subject());

        replayMocks();
        Authentication response = provider.authenticate(AUTHENTICATION);
        verifyMocks();

        assertNotNull("User not authenticated", response);
        assertNotNull("Principal missing", response.getPrincipal());
        assertSame("Principal wrong", USER_DETAILS, response.getPrincipal());
    }

    public void testAuthenticateUnverifiedUncached() throws Exception {
        expect(userCache.getUserFromCache(USERNAME)).andReturn(null);
        expect(authenticationManager.authenticate(USERNAME, PASSWORD)).andThrow(new CSLoginException());
        expect(userDetailsService.loadUserByUsername(USERNAME)).andReturn(USER_DETAILS);

        replayMocks();
        try {
            provider.authenticate(AUTHENTICATION);
            fail("Exception not thrown");
        } catch (BadCredentialsException bce) {
            verifyMocks();
        }
    }
    
    public void testAuthenticateUnverifiedCached() throws Exception {
        expect(userCache.getUserFromCache(USERNAME)).andReturn(USER_DETAILS);
        expect(authenticationManager.authenticate(USERNAME, PASSWORD)).andThrow(new CSLoginException()).times(2);
        // Acegi reloads after failed auth from cache
        expect(userDetailsService.loadUserByUsername(USERNAME)).andReturn(USER_DETAILS);

        replayMocks();
        try {
            provider.authenticate(AUTHENTICATION);
            fail("Exception not thrown");
        } catch (BadCredentialsException bce) {
            verifyMocks();
        }
    }

}
