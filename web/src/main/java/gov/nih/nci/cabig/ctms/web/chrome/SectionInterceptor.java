package gov.nih.nci.cabig.ctms.web.chrome;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Interceptor which figures out which section a requested page is from
 * and associates that section with the current request.
 * <p>
 * Adds two attributes to the request:
 * <ul>
 *   <li><kbd>currentSection</kbd> - {@link Section} containing the requested page, if one is found.
 *   <li><kbd>sections</kbd> - the list of {@link Section}s provided to this interceptor.
 * </ul>
 *
 * @author Rhett Sutphin, Priyatam
 */
public class SectionInterceptor extends HandlerInterceptorAdapter {
    private List<Section> sections;
    private String attributePrefix;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String controllerPath = urlPathHelper.getPathWithinServletMapping(request);
        Section currentSection = findSection(urlPathHelper.getPathWithinServletMapping(request));
        Task currentTask = findTask(currentSection, controllerPath);
        
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
    
    private Task findTask(Section section, String controllerPath){
    	for (Task task : section.getTasks()) {
    		if (task.getUrl().indexOf(controllerPath) > -1) {
    			return task;  	                	
    		}                		
    	}
    	 return null;
    }
    

    private String prefix(String attr) {
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
