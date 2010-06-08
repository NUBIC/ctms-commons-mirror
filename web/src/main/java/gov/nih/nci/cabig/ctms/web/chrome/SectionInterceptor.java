package gov.nih.nci.cabig.ctms.web.chrome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Interceptor which figures out which section a requested page is from
 * and associates that section with the current request.
 * <p>
 * Adds three attributes to the request:
 * <ul>
 *   <li><kbd>sections</kbd> - the list of {@link Section}s provided to this interceptor.
 *   <li><kbd>currentSection</kbd> - {@link Section} containing the requested page, if one is found.
 *   <li><kbd>currentTask</kbd> - {@link Task} matching requested page, if one is found.
 * </ul>
 *
 * @author Rhett Sutphin, Priyatam
 */
public class SectionInterceptor extends HandlerInterceptorAdapter {
    private List<Section> sections;
    private String attributePrefix;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("preHandle invoked");
        String controllerPath = urlPathHelper.getPathWithinServletMapping(request);
        Section currentSection = findSection(urlPathHelper.getPathWithinServletMapping(request));

        Task currentTask = null;
        if (currentSection != null) {
            currentTask = findTask(currentSection, controllerPath);
        }

        request.setAttribute(prefix("currentSection"), currentSection);
        request.setAttribute(prefix("currentTask"), currentTask);        
        request.setAttribute(prefix("sections"), getSections());
        return true;
    }

    private Section findSection(String controllerPath) {
        for (Section section : getSections()) {
            for (String pattern : section.getPathMappings()) {
                if (pathMatcher.match(pattern, controllerPath)) {
                    return section;
                }
            }
        }
        return null;
    }
    
    private Task findTask(Section section, String controllerPath) {
        if (section.getTasks() != null) {
            for (Task task : section.getTasks()) {
                if (task.getUrl().indexOf(controllerPath) > -1) {
                    return task;
                }
            }
        }
        return null;
    }
    

    protected String prefix(String attr) {
        if (getAttributePrefix() == null) {
            return attr;
        } else {
            return getAttributePrefix() + attr;
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    @Required
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getAttributePrefix() {
        return attributePrefix;
    }

    public void setAttributePrefix(String attributePrefix) {
        this.attributePrefix = attributePrefix;
    }
}
