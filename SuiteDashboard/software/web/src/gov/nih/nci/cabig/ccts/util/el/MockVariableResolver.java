package gov.nih.nci.cabig.ccts.util.el;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;
import java.util.HashMap;
import java.util.Map;

public class MockVariableResolver implements VariableResolver {

    private Map variableMap;

    public MockVariableResolver() {
        this.variableMap = new HashMap();
    }

    public void addVariable(String variable, Object value) {
        this.variableMap.put(variable, value);
    }

    public Object resolveVariable(String pName) throws ELException {
        return this.variableMap.get(pName);
    }

}
