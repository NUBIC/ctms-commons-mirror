package gov.nih.nci.cabig.ctms.web.tabs;

/**
 * @author Rhett Sutphin
 */
public interface FlowFactory<C> {
    /**
     * Create and return a flow, optionally basing its construction on the context of the
     * request.
     */
    Flow<C> createFlow(C command);
}
