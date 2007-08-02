package gov.nih.nci.cabig.ctms.web.tabs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.BindException;
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

    @Override
    @SuppressWarnings({ "unchecked", "RawUseOfParameterizedType" })
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
     * Allow subclasses to select alternate flows using their own logic.
     */
    @SuppressWarnings("unchecked")
    protected Flow<C> getEffectiveFlow(HttpServletRequest request, C command) {
        return getFlow(command);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected int getPageCount(HttpServletRequest request, Object command) {
        return getFlow((C) command).getTabCount();
    }

    private Tab<C> getCurrentPage(HttpServletRequest request, C command) {
        int page = getCurrentPage(request);
        return getTab(command, page);
    }

    /**
     * Delegates to the tab to determine the view name.
     *
     * <p>
     *   IF YOU OVERRIDE THIS METHOD, YOU MUST CALL IT WITH super.  Even if you disregard the
     *   output.
     *   <span class="bogus">Bogus implementation detail alert:  {@link Tab#onDisplay} is invoked
     *   from this method.  This is because {@link AbstractWizardFormController#showPage} (the
     *   natural place to call <code>onDisplay</code>) is final.
     * </p>
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    protected String getViewName(HttpServletRequest request, Object command, int page) {
        Tab<C> tab = getTab((C) command, page);
        log.debug("Pre-processing tab " + page + " (" + tab.getShortTitle() + ") before rendering view");
        tab.onDisplay(request, (C) command);
        return tab.getViewName();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected Object currentFormObject(HttpServletRequest request, Object oCommand) throws Exception {
        Tab<C> tab = getCurrentPage(request, (C) oCommand);
        log.debug("Pre-processing tab " + tab.getNumber() + " (" + tab.getShortTitle() + ") before binding");
        tab.beforeBind(request, (C) oCommand);
        return oCommand;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected void onBind(HttpServletRequest request, Object oCommand, BindException errors) throws Exception {
        Tab<C> tab = getCurrentPage(request, (C) oCommand);
        log.debug("Invoking onBind for tab " + tab.getNumber() + " (" + tab.getShortTitle() + ')');
        tab.onBind(request, (C) oCommand, errors);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void validatePage(Object oCommand, Errors errors, int page, boolean finish) {
        C command = (C) oCommand;
        Tab<C> tab = getFlow(command).getTab(page);
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
    @Deprecated
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

    // XXX: this is potentially expensive.  Create a map of commands to flows (using weakrefs to
    // prevent leaks) if necessary.
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
    @Deprecated
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
