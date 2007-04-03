package gov.nih.nci.cabig.ctms.tools.spring;

/**
 * Interface for objects which reverse spring's
 * {@link org.springframework.web.servlet.HandlerMapping}s.
 *
 * @author Rhett Sutphin
 */
public interface ControllerUrlResolver {
    ResolvedControllerReference resolve(String controllerBeanName);
}
