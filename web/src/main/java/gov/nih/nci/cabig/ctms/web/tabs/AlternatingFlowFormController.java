package gov.nih.nci.cabig.ctms.web.tabs;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Rhett Sutphin
 * @author Priyatam
 */
@Deprecated // TODO: refactor this alternate flow stuff to use a flow factory
public abstract class AlternatingFlowFormController<C> extends AbstractTabbedFlowFormController<C> {
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

    /**
     * Select current flow or alternate
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Flow<C> getEffectiveFlow(HttpServletRequest request, C command) {
        Flow<C> effective;
        if (isUseAlternateFlow(request)) {
            Flow<C> altFlow = (Flow<C>) request.getSession().getAttribute(getFlowAttributeName());
            effective = altFlow == null ? getFlow(command) : altFlow;
        } else {
            effective = super.getEffectiveFlow(request, command);
        }
        return effective;
    }


    @Override
    public void setFlowFactory(FlowFactory<C> flowFactory) {
        if (flowFactory instanceof StaticFlowFactory) {
            super.setFlowFactory(flowFactory);
        } else {
            throw new UnsupportedOperationException("You may not use a non-static flow factory with "
                + getClass().getSimpleName());
        }
    }
}
