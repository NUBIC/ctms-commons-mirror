package gov.nih.nci.cabig.ctms.web.tabs;

import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.validation.Errors;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * More object-oriented version of {@link AbstractWizardFormController}.
 * Controller is configured with a {@link Flow} of {@link Tab}s -- most controller
 * page-specific controller methods are delegated to implementations in the tabs.
 * <p>
 * The type parameter <kbd>C</kbd> is the class of the command that will be used with the
 * controller.
 *
 * @author Rhett Sutphin
 */
public abstract class AbstractTabbedFlowFormController<C> extends AbstractWizardFormController
    implements InitializingBean
{
    private final Log log = LogFactory.getLog(getClass());

    private Flow<C> flow;

    private TabConfigurer tabConfigurer;

    public Flow<C> getFlow() {
        return flow;
    }

    public void setFlow(Flow<C> flow) {
        this.flow = flow;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<?, ?> referenceData(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
        Map<String, Object> refdata = new HashMap<String, Object>();
        Tab<C> current = getFlow().getTab(page);
        refdata.put("tab", current);
        refdata.put("flow", getFlow());
        refdata.putAll(current.referenceData((C) command));
        log.debug("Returning reference data for page " + page);
        log.debug("Command is " + command);
        return refdata;
    }

    @Override
    protected int getPageCount(HttpServletRequest request, Object command) {
        return getFlow().getTabCount();
    }

    @Override
    protected String getViewName(HttpServletRequest request, Object command, int page) {
        return getFlow().getTab(page).getViewName();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void validatePage(Object oCommand, Errors errors, int page, boolean finish) {
        C command = (C) oCommand;
        Tab<C> tab = getFlow().getTab(page);

        // XXX TODO: this isn't threadsafe at all
        setAllowDirtyForward(tab.isAllowDirtyForward());
        setAllowDirtyBack(tab.isAllowDirtyBack());

        tab.validate(command, errors);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void postProcessPage(
        HttpServletRequest request, Object oCommand, Errors errors, int page
    ) throws Exception {
        C command = (C) oCommand;
        getFlow().getTab(page).postProcess(request, command, errors);
    }

    public void afterPropertiesSet() throws Exception {
        if (getTabConfigurer() != null) {
            getTabConfigurer().injectDependencies(getFlow());
        } else {
            log.debug("No tab configurer for " + getClass().getName()
                + ".  Skipping tab dependency injection.");
        }
    }

    public TabConfigurer getTabConfigurer() {
        return tabConfigurer;
    }

    /**
     * If there's a tabConfigurer provided, it will be used to inject dependencies into
     * the tabs.  This is useful if the flow is created programmatically instead of in the
     * application context.
     *
     * @param tabConfigurer
     */
    public void setTabConfigurer(TabConfigurer tabConfigurer) {
        this.tabConfigurer = tabConfigurer;
    }
}
