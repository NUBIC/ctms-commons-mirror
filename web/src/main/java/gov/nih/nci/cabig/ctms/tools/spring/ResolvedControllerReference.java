package gov.nih.nci.cabig.ctms.tools.spring;

import org.springframework.web.servlet.mvc.Controller;

import java.io.Serializable;

/**
 * Encapsulates a controller's URL, both context- and servlet-relative.
 *
 * @author Rhett Sutphin
 */
public class ResolvedControllerReference implements Serializable {
    private String beanName;
    private String className;
    private String servletName;
    private String url;

    public ResolvedControllerReference(String beanName, String className, String servletName, String url) {
        this.beanName = beanName;
        this.className = className;
        this.servletName = servletName;
        this.url = url;
    }

    /**
     * The context-relative URL for the configured controller.
     */
    public String getUrl(boolean contextRelative) {
        if (contextRelative && servletName != null) {
            return '/' + servletName + url;
        } else {
            return url;
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Controller> getControllerClass() {
        try {
            return (Class<? extends Controller>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class for controller bean " + beanName + " not found", e);
        }
    }
}
