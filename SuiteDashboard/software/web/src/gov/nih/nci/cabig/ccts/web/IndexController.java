package gov.nih.nci.cabig.ccts.web;

import gov.nih.nci.cabig.ccts.dao.UserDao;
import gov.nih.nci.cabig.ccts.security.SecurityUtils;
import gov.nih.nci.cabig.ccts.security.WebSSOUser;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Properties;

public class IndexController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ModelAndView mvc = new ModelAndView("/WEB-INF/views/index.jsp");
        return mvc;
    }

}
