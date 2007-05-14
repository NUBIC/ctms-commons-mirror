package gov.nih.nci.cabig.ctms.web.tabs;

/**
 * @author Rhett Sutphin
 */
public class SimpleTestTab<C> extends Tab<C> {
    public SimpleTestTab(int num) {
        super("Tab " + num, "Tab " + num, "v" + num);
    }
}
