package gov.nih.nci.cabig.ctms.web.tabs;

import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * A sub-controller class describing the behavior of one page in a {@link Flow}.
 * Tab classes may be shared between flows, but tab instances must not be. 
 *
 * @author Rhett Sutphin
 */
public class Tab<C> {
    private Integer number;
    private String longTitle;
    private String shortTitle;
    private String viewName;

    public Tab() { }

    public Tab(String longTitle, String shortTitle, String viewName) {
        this.longTitle = longTitle;
        this.shortTitle = shortTitle;
        this.viewName = viewName;
    }

    ////// TEMPLATE METHODS

    public Map<String, Object> referenceData() {
        return new HashMap<String, Object>();
    }

    public Map<String, Object> referenceData(C command) {
        return referenceData();
    }

    public void validate(C command, Errors errors) {
    }

    public boolean isAllowDirtyForward() {
        return true;
    }

    public boolean isAllowDirtyBack() {
        return true;
    }

    public void postProcess(HttpServletRequest request, C command, Errors errors) {
    }

    public int getTargetNumber() {
        return getNumber() + 1;
    }

    ////// BEAN PROPERTIES

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
