package gov.nih.nci.cabig.ccts.security;

import gov.nih.nci.cabig.ccts.dao.UserDao;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        WebSSOUser user = null;
        Authentication a = SecurityUtils.getAuthentication();
        if (a != null) user = (WebSSOUser)a.getPrincipal();
        httpRequest.setAttribute("user", user);

        boolean exists = false;
        if (user != null) {
            exists = getUserDao().userExists(user.getOriginalUsername());
        }
        httpRequest.setAttribute("exists", exists);
	}

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
