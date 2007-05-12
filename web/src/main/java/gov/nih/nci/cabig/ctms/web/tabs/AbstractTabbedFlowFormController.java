package gov.nih.nci.cabig.ctms.web.tabs;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

/**
 * More object-oriented version of {@link AbstractWizardFormController}.
 * Controller is configured with a {@link Flow} of {@link Tab}s -- most controller
 * page-specific controller methods are delegated to implementations in the tabs.
 * <p>
 * The type parameter <kbd>C</kbd> is the class of the command that will be used with the
 * controller.
 *
 * @author Rhett Sutphin, Priyatam
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
    
    public String getFlowAttributeName() {
    	return getClass().getName() + ".FLOW." + getFlow().getName();
	}

    /**
     * Check if Alternate flow (Sub Flows
     * @param request
     * @return
     */
	public boolean isUseAlternateFlow(HttpServletRequest request) {
		return request.getSession().getAttribute(getClass().getName() + ".FLOW." +
			getFlow().getName()+".ALT_FLOW")!=null?true:false;
	}

	/**
	 * Set Alternate Flow (Sub flows)
	 * @param request
	 */
	public void useAlternateFlow(HttpServletRequest request) {
		request.getSession().setAttribute(getClass().getName() + ".FLOW." + 
			getFlow().getName()+".ALT_FLOW","true");
	}
    
    @Override
    @SuppressWarnings("unchecked")
    protected final Map<?, ?> referenceData(HttpServletRequest request, Object command, Errors errors, int page) 
    	throws Exception {
        Map<String, Object> refdata = new HashMap<String, Object>();
        Map refDataCall=referenceData(request, page);
        
        if(refDataCall!=null){
        	refdata.putAll(refDataCall);
        }
        Tab<C> current = getFlow().getTab(page);
        refdata.put("tab", current);
        
        // insert current flow or alternate flow
        if(isUseAlternateFlow(request)){
        	Flow altFlow=(Flow)request.getSession().getAttribute(getFlowAttributeName());
        	if(altFlow!=null)
        		refdata.put("flow", altFlow);
        	else
        		refdata.put("flow", getFlow());
        }else{
        	refdata.put("flow", getFlow());
        }
        
        // get refData from Subclasses
        refdata.putAll(referenceDataController(request, command, errors, page));
        
        // get refData from the Tabs 
        refdata.putAll(current.referenceData((C) command));
        return refdata;
    }

    /**
     * Template method for individual controllers to add refdata
     */
    protected Map<String, Object> referenceDataController(HttpServletRequest request, Object command, Errors errors, int page) {
    	//default implementation
    	return new HashMap<String, Object>();
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
        
        afterPostProcessPage(request, command, errors, page);
    }
    
    
    /**
     * Template method to do custom processing post 'postProcessPage'
     * This is useful when processing is needed by Controller rather than a Tab
     * (like saves/updates)
     * @param request
     * @param oCommand
     * @param errors
     * @param page
     */
    protected void afterPostProcessPage(HttpServletRequest request, Object oCommand, 
    	Errors errors, int page) {
    	//default null implementation
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
