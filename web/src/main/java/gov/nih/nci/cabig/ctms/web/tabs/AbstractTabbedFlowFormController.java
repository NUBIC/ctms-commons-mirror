package gov.nih.nci.cabig.ctms.web.tabs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

/**
 * More object-oriented version of {@link AbstractWizardFormController}.
 * Controller is configured with a {@link Flow} of {@link Tab}s -- most controller
 * page-specific controller methods are delegated to implementations in the tabs.
 * <p>
 * The type parameter <kbd>C</kbd> is the class of the command that will be used with the
 * controller.
 *
 * @author Rhett Sutphin
 * @author Priyatam
 */
public abstract class AbstractTabbedFlowFormController<C> extends AbstractWizardFormController {
    private final Log log = LogFactory.getLog(getClass());

    private FlowFactory<C> flowFactory;

    private TabConfigurer tabConfigurer;

    protected AbstractTabbedFlowFormController() {
        setFlowFactory(new StaticFlowFactory<C>());
    }

    // TODO: refactor this alternate flow stuff to use a flow factory
    public String getFlowAttributeName() {
        return getClass().getName() + ".FLOW." + getFlow().getName();
    }

    public String getAlternateFlowAttributeName() {
        return getClass().getName() + ".FLOW." + getFlow().getName() + ".ALT_FLOW";
    }

    /**
     * Check if Alternate flow (Sub Flows
     *
     * @param request
     * @return
     */
    public boolean isUseAlternateFlow(HttpServletRequest request) {
        return request.getSession().getAttribute(getAlternateFlowAttributeName()) != null;
    }

    /**
     * Set Alternate Flow (Sub flows)
     *
     * @param request
     */
    public void useAlternateFlow(HttpServletRequest request) {
        request.getSession().setAttribute(getAlternateFlowAttributeName(), "true");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map referenceData(HttpServletRequest request, Object oCommand, Errors errors, int page)
        throws Exception {
        C command = (C) oCommand;

        // The super invocation includes all refdata from #referenceData(request, page)
        Map<String, Object> refdata = super.referenceData(request, command, errors, page);
        if (refdata == null) {
            refdata = new HashMap<String, Object>();
        }

        Tab<C> current = getFlow(command).getTab(page);
        refdata.put("tab", current);
        refdata.put("flow", getEffectiveFlow(request, command));
        refdata.putAll(current.referenceData(command));
        log.debug("Returning reference data for page " + page);
        log.debug("Command is " + command);
        return refdata;
    }

    /**
     * Select current flow or alternate
     */
    @SuppressWarnings("unchecked")
    private Flow<C> getEffectiveFlow(HttpServletRequest request, C command) {
        Flow<C> effective;
        if (isUseAlternateFlow(request)) {
            Flow<C> altFlow = (Flow<C>) request.getSession().getAttribute(getFlowAttributeName());
            effective = altFlow == null ? getFlow(command) : altFlow;
        } else {
            effective = getFlow(command);
        }
        return effective;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected int getPageCount(HttpServletRequest request, Object command) {
        return getFlow((C) command).getTabCount();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected String getViewName(HttpServletRequest request, Object command, int page) {
        return getTab((C) command, page).getViewName();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void validatePage(Object oCommand, Errors errors, int page, boolean finish) {
        C command = (C) oCommand;
        Tab<C> tab = getFlow(command).getTab(page);

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
        getFlow(command).getTab(page).postProcess(request, command, errors);
    }

    ////// FLOW ACCESS

    /**
     * Returns the flow for this controller, but only if this instance is using a static
     * flow factory.  Included for backwards compatibility.
     */
    public Flow<C> getFlow() {
        // XXX: best form would be to use a flag instead of instanceof, but I can't
        // think of a situation where you'd need a static flow factory and StaticFlowFactory
        // wouldn't work.
        if (getFlowFactory() instanceof StaticFlowFactory) {
            return ((StaticFlowFactory<C>) getFlowFactory()).getFlow();
        } else {
            throw new CommonsSystemException("getFlow() only works with StaticFlowFactory.  You are using " +
                getFlowFactory().getClass().getSimpleName() + '.');
        }
    }

    // TODO: this is potentially expensive.  Create a map of commands to flows (using weakrefs to prevent leaks).
    public Flow<C> getFlow(C command) {
        Flow<C> flow = getFlowFactory().createFlow(command);
        injectDependencies(flow);
        return flow;
    }

    protected Tab<C> getTab(C command, int page) {
        return getFlow(command).getTab(page);
    }

    private void injectDependencies(Flow<C> flow) {
        if (getTabConfigurer() != null) {
            getTabConfigurer().injectDependencies(flow);
        } else {
            log.debug("No tab configurer for " + getClass().getName()
                + ".  Skipping tab dependency injection.");
        }
    }

    /**
     * Syntactic sugar for backwards compatibility.  Equivalent to
     * <code>setFlowFactory(new {@link StaticFlowFactory}&lt;C&gt;(flow)</code>.
     *
     * @param flow
     */
    public void setFlow(Flow<C> flow) {
        setFlowFactory(new StaticFlowFactory<C>(flow));
    }

    ////// CONFIGURATION

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

    public FlowFactory<C> getFlowFactory() {
        return flowFactory;
    }

    public void setFlowFactory(FlowFactory<C> flowFactory) {
        if (flowFactory == null) throw new NullPointerException("FlowFactory is required");
        this.flowFactory = flowFactory;
    }
}
