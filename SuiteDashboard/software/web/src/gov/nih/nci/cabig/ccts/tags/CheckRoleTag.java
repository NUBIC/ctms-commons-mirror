package gov.nih.nci.cabig.ccts.tags;

import gov.nih.nci.cabig.ccts.security.SecurityUtils;
import org.acegisecurity.GrantedAuthority;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class CheckRoleTag extends SimpleTagSupport {

    boolean hasTheRole;
    private String roleName;

    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        checkTheRole();
        if (hasTheRole) getJspBody().invoke(null);
    }

    private void checkTheRole() {
        GrantedAuthority[] gas = SecurityUtils.getGrantedAuthorities();
        for (GrantedAuthority ga : gas) {
            if (ga.getAuthority().toLowerCase().equals(roleName.toLowerCase())) hasTheRole = true;
        }
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}

