package gov.nih.nci.cabig.ccts.security;

import gov.nih.nci.cabig.ccts.dao.UserDao;
import gov.nih.nci.cabig.ccts.domain.UserGroupType;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserFilter implements Filter {

    private UserDao userDao;
	private static final Log log = LogFactory.getLog(UserFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
        prepareUser(httpRequest);
        chain.doFilter(httpRequest, httpResponse);
	}

	private void prepareUser(HttpServletRequest httpRequest) {
        log.debug(">>> Adjusting roles names...");

        WebSSOUser user = null;
        Authentication a = SecurityUtils.getAuthentication();
        if (a != null) user = (WebSSOUser)a.getPrincipal();
        else {
            log.debug(">>> No authentication information.");
        }

        log.debug(">>> User: " + user);
        log.debug(">>> User Authorities: " + user.getAuthorities());
        log.debug(">>> UserGroupType size : " + UserGroupType.values().length);

        Map<String, String> roles = new HashMap<String, String>();
        for (UserGroupType r : UserGroupType.values()) {
            roles.put(r.getCsmName(), r.getDisplayName());
        }

        log.debug(">>> Roles size : " + roles.size());

        List rolesAsArray = new ArrayList();
        log.debug(String.format(">>> User has %d roles.", user.getAuthorities().length));

        for (GrantedAuthority role : user.getAuthorities()) {
            String r = roles.get(role.getAuthority());
            log.debug(String.format(">>> Role: %s", r));
            if (r != null) rolesAsArray.add(r);
        }

        boolean exists = false;
        if (user != null) {
            exists = getUserDao().userExists(user.getOriginalUsername());
        }

        httpRequest.getSession().setAttribute("user", user);
        httpRequest.getSession().setAttribute("exists", exists);
        httpRequest.getSession().setAttribute("roles", rolesAsArray);
	}

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
