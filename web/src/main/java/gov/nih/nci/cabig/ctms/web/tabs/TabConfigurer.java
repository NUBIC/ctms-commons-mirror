package gov.nih.nci.cabig.ctms.web.tabs;

/**
 * Defines the interface for objects which provide IoC dependency resolution for tabs.
 *
 * @author Rhett Sutphin
 */
public interface TabConfigurer {
    void injectDependencies(Flow<?> flow);
}
