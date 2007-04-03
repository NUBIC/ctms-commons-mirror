package gov.nih.nci.cabig.ctms.web.chrome;

import java.util.List;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Required;
import gov.nih.nci.cabig.ctms.tools.spring.ControllerUrlResolver;

/**
 * @author Rhett Sutphin
 */
public class Section {
    private List<String> pathMappings = new LinkedList<String>();
    private String displayName;
    private String mainController;
    private List<Task> tasks = new LinkedList<Task>();

    private ControllerUrlResolver urlResolver;

    ////// LOGIC

    public String getMainUrl() {
        return urlResolver.resolve(getMainController()).getUrl(true);
    }

    /*
     * Sugar for setting only one mapping.
     */
    public void setPathMapping(String singleMapping) {
        this.pathMappings.clear();
        pathMappings.add(singleMapping);
    }

    ////// BEAN PROPERTIES

    public List<String> getPathMappings() {
        return pathMappings;
    }

    public void setPathMappings(List<String> pathMappings) {
        this.pathMappings = pathMappings;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Required
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMainController() {
        return mainController;
    }

    @Required
    public void setMainController(String mainController) {
        this.mainController = mainController;
    }

    @Required
    public void setUrlResolver(ControllerUrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    ////// OBJECT METHODS

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('[').append(getDisplayName())
            .append(']').toString();
    }
}
