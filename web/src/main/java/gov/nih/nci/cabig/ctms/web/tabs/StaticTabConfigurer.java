package gov.nih.nci.cabig.ctms.web.tabs;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.ui.ModelMap;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;

/**
 * Configurer which uses a set list of known objects.  Useful for configuring tabs
 * with mock objects during testing.
 *
 * @author Rhett Sutphin
 */
public class StaticTabConfigurer extends DefaultTabConfigurer {
    private StaticListableBeanFactory staticFactory;

    @SuppressWarnings("unchecked")
    public StaticTabConfigurer(Object... beans) {
        staticFactory = new StaticListableBeanFactory();
        ModelMap modelMap = new ModelMap();
        modelMap.addAllObjects(Arrays.asList(beans));
        for (Object o : modelMap.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            staticFactory.addBean(entry.getKey(), entry.getValue());
        }
    }

    public void addBean(String name, Object bean) {
        staticFactory.addBean(name, bean);
    }

    @Override
    protected AutowireCapableBeanFactory getBeanFactory() {
        return new DefaultListableBeanFactory(staticFactory);
    }
}
