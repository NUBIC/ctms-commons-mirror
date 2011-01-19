package gov.nih.nci.cabig.ccts.tags;

import gov.nih.nci.cabig.ccts.security.SecurityUtils;
import gov.nih.nci.cabig.ccts.util.BooleanDelimiter;
import gov.nih.nci.cabig.ccts.util.el.EL;
import org.acegisecurity.GrantedAuthority;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Arrays;

public class CheckRoleTag extends SimpleTagSupport {

    private static final Log log = LogFactory.getLog(CheckRoleTag.class);
    private boolean hasTheRoles;
    private String roleName;

    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        evaluateRoles();
        if (hasTheRoles) getJspBody().invoke(null);
    }

    private void evaluateRoles() {
        String rolesString = new String(roleName);

        log.debug(">>> EVALUATING: " + roleName);

        String[] roles = BooleanDelimiter.parseBoolean(roleName);
        if (roles.length == 0) {
            hasTheRoles = false;
            return;
        }

        log.debug(">>> FOUND ROLES: " + Arrays.toString(roles));

        for (String role : roles) {
            boolean hasTheRole = evaluateOneRole(role);
            rolesString = rolesString.replace(role, String.valueOf(hasTheRole));
            log.debug(String.format(">>> %s: %b, NEW STRING: %s ", role, hasTheRole, rolesString));
        }

        log.debug(">>> FINAL STRING: " + rolesString);
        hasTheRoles = Boolean.parseBoolean(new EL().evaluate("${" + rolesString + "}"));
        log.debug(">>> HAS THE ROLES: " + hasTheRoles);
    }

    private boolean evaluateOneRole(String role) {

        if (role == null || StringUtils.isEmpty(role)) return false;

        GrantedAuthority[] gas = null;

/*
        // for testing
        gas = new GrantedAuthority[] {
                new GrantedAuthority() { public String getAuthority() {return "user_administrator2";}},
                new GrantedAuthority() { public String getAuthority() {return "person_and_organization_information_manager";}}
        };
*/
        gas = SecurityUtils.getGrantedAuthorities();

        for (GrantedAuthority ga : gas) {
            if (ga.getAuthority().toLowerCase().equals(role.trim().toLowerCase())) return true;
        }

        return false;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}

