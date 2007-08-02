package gov.nih.nci.cabig.ctms.web.tabs;

import gov.nih.nci.cabig.ctms.web.WebTestCase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Rhett Sutphin
 */
public class AlternatingFlowFormControllerTest extends WebTestCase {
    private TestController controller;
    private Flow<Object> flow;
    private Flow<Object> altFlow;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        flow = new Flow<Object>("Twelve");
        altFlow = new Flow<Object>("12A");
        controller = new TestController();
        controller.setFlowFactory(new StaticFlowFactory<Object>(flow));
    }

    public void testIsUsedAfterSetUsed() throws Exception {
        assertFalse(controller.isUseAlternateFlow(request));
        controller.useAlternateFlow(request);
        assertTrue(controller.isUseAlternateFlow(request));
    }

    public void testAlternateFlowUsedIfConfigured() throws Exception {
        request.getSession().setAttribute(TestController.class.getName() + ".FLOW.Twelve", altFlow);
        request.getSession().setAttribute(TestController.class.getName() + ".FLOW.Twelve.ALT_FLOW", "true");
        assertSame(altFlow, controller.getEffectiveFlow(request, "DC"));
    }

    public void testMainFlowUsedIfNotConfigured() throws Exception {
        assertSame(flow, controller.getEffectiveFlow(request, "DC"));
    }

    public void testMainFlowUsedIfConfiguredButAltMissing() throws Exception {
        request.getSession().setAttribute(TestController.class.getName() + ".FLOW.Twelve", null);
        request.getSession().setAttribute(TestController.class.getName() + ".FLOW.Twelve.ALT_FLOW", "true");
        assertSame(flow, controller.getEffectiveFlow(request, "DC"));
    }

    private static class TestController extends AlternatingFlowFormController<Object> {
        @Override
        protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
            throw new UnsupportedOperationException("processFinish not implemented");
        }
    }
}
