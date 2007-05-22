package gov.nih.nci.cabig.ctms.web.tabs;

import java.util.List;
import java.util.ArrayList;

/**
 * {@link FlowFactory} which produces flows whose names are dependent on a particular
 * invocation's command.  The set of tabs is always the same.
 *
 * @author Rhett Sutphin
 */
public abstract class NameChangingFlowFactory<C> implements FlowFactory<C> {
    private List<Tab<C>> tabs;

    protected NameChangingFlowFactory() {
        tabs = new ArrayList<Tab<C>>();
    }

    public void addTab(Tab<C> tab) {
        tabs.add(tab);
    }

    public Flow<C> createFlow(C command) {
        Flow<C> newFlow = new Flow<C>(createName(command));
        // Be aware:  Flow#addTab has side effects, so this could be bad.
        // It isn't bad, though, because the side effect will always be the same.
        // Thus it doesn't matter that we're sharing tab instances across different
        // flow instances.
        for (Tab<C> tab : tabs) {
            newFlow.addTab(tab);
        }
        return newFlow;
    }

    /**
     * Template method to allow instances to define the flow name.
     */
    protected abstract String createName(C command);
}
