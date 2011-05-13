package gov.nih.nci.cabig.ctms.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Utility methods for exposing various web context information as standard collections.
 * This is mainly used to make them easily traversable in JSP EL.
 *
 * Derived from NU's core-commons library (WebUtils, there).
 *
 * @author Rhett Sutphin
 */
public class WebTools {
    private static final Logger log = LoggerFactory.getLogger(WebTools.class);

    @SuppressWarnings({ "unchecked" })
    public static SortedMap<String, Object> sessionAttributesToMap(final HttpSession session) {
        if (session != null) {
            return namedAttributesToMap(session.getAttributeNames(), new AttributeAccessor() {
                public Object getAttribute(String name) { return session.getAttribute(name); }
            });
        } else {
            return emptySortedMap();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static SortedMap<String, Object> requestAttributesToMap(final HttpServletRequest request) {
        return namedAttributesToMap(request.getAttributeNames(), new AttributeAccessor() {
            public Object getAttribute(String name) { return request.getAttribute(name); }
        });
    }

    private final static Collection<String> EXCLUDED_REQUEST_PROPERTIES = Arrays.asList(
        "class", "session", "headerNames", "attributeNames", "parameterMap", "parameterNames",
        "reader", "inputStream", "locales", "cookies", "requestedSessionIdFromUrl"
    );

    public static SortedMap<String, Object> requestPropertiesToMap(HttpServletRequest request) {
        BeanWrapper wrapped = new BeanWrapperImpl(request);
        SortedMap<String, Object> map = new TreeMap<String, Object>();
        for (PropertyDescriptor descriptor : wrapped.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!EXCLUDED_REQUEST_PROPERTIES.contains(name) && descriptor.getReadMethod() != null) {
                Object propertyValue;
                try {
                    propertyValue = wrapped.getPropertyValue(name);
                } catch (InvalidPropertyException e) {
                    log.debug("Exception reading request property " + name, e);
                    propertyValue = e.getMostSpecificCause();
                }
                map.put(name, propertyValue);
            }
        }
        return map;
    }

    @SuppressWarnings({ "unchecked" })
    public static SortedMap<String, String[]> headersToMap(final HttpServletRequest request) {
        return namedAttributesToMap(request.getHeaderNames(), new AttributeAccessor<String[]>() {
            @SuppressWarnings({ "unchecked" })
            public String[] getAttribute(String headerName) {
                Enumeration<String> values = request.getHeaders(headerName);
                List<String> valList = new ArrayList<String>();
                while (values.hasMoreElements()) {
                    valList.add(values.nextElement());
                }
                return valList.toArray(new String[valList.size()]);
            }
        });
    }

    @SuppressWarnings({ "unchecked" })
    public static SortedMap<String, Object> servletContextAttributesToMap(final ServletContext context) {
        return namedAttributesToMap(context.getAttributeNames(), new AttributeAccessor<Object>() {
            public Object getAttribute(String name) {
                return context.getAttribute(name);
            }
        });
    }

    @SuppressWarnings({ "unchecked" })
    public static SortedMap<String, Object> contextInitializationParametersToMap(final ServletContext context) {
        return namedAttributesToMap(context.getInitParameterNames(), new AttributeAccessor<Object>() {
            public Object getAttribute(String name) {
                return context.getInitParameter(name);
            }
        });
    }

    private static SortedMap<String, Object> emptySortedMap() {
        return Collections.unmodifiableSortedMap(new TreeMap<String, Object>());
    }

    private static <T> SortedMap<String, T> namedAttributesToMap(
        Enumeration<String> names, AttributeAccessor<T> accessor
    ) {
        SortedMap<String, T> map = new TreeMap<String, T>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, accessor.getAttribute(name));
        }
        return Collections.unmodifiableSortedMap(map);
    }

    private static interface AttributeAccessor<T> {
        T getAttribute(String name);
    }
}
