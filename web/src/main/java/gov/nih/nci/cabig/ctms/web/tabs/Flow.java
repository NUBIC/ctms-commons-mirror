package gov.nih.nci.cabig.ctms.web.tabs;

import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.List;
import java.util.LinkedList;

/**
 * A collection of {@link Tab}s.
 *
 * @see AbstractTabbedFlowFormController
 * @author Rhett Sutphin
 */
public class Flow<C> {
    private String name;
    private List<Tab<C>> tabs;

    public Flow(String name) {
        this.name = name;
        this.tabs = new LinkedList<Tab<C>>();
    }

    public void addTab(Tab<C> tab) {
        tab.setNumber(tabs.size());
        tab.setFlow(this);
        this.tabs.add(tab);
    }

    public int getTabCount() {
        return getTabs().size();
    }

    public Tab<C> getTab(int number) {
        return getTabs().get(number);
    }

    public List<Tab<C>> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab<C>> tabs) {
        getTabs().clear();
        for (Tab<C> tab : tabs) addTab(tab);
    }

    public String getName() {
        return name;
    }

    ////// OBJECT METHODS

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
            .append('[').append(getName()).append(']')
            .toString();
    }
}
