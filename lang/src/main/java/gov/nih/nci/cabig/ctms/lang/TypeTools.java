package gov.nih.nci.cabig.ctms.lang;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

/**
 * @author Rhett Sutphin       
 */
public class TypeTools {
    public static Class<?> getCollectionPropertyType(Class<?> klass, String propertyName) {
        PropertyDescriptor descriptor = getPropertyDescriptor(klass, propertyName);
        if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
            return (Class<?>) getTypeParameters(klass, descriptor)[0];
        } else {
            throw new IllegalArgumentException("Property " + propertyName + " of "
                + klass.getName() + " is not a collection; it is a " + descriptor.getPropertyType().getName());
        }
    }

    public static Class<?> getMapPropertyKeyType(Class<?> klass, String propertyName) {
        PropertyDescriptor descriptor = getPropertyDescriptor(klass, propertyName);
        if (Map.class.isAssignableFrom(descriptor.getPropertyType())) {
            return (Class<?>) getTypeParameters(klass, descriptor)[0];
        } else {
            throw new IllegalArgumentException("Property " + propertyName + " of "
                + klass.getName() + " is not a map; it is a " + descriptor.getPropertyType().getName());
        }
    }

    public static Class<?> getMapPropertyValueType(Class<?> klass, String propertyName) {
        PropertyDescriptor descriptor = getPropertyDescriptor(klass, propertyName);
        if (Map.class.isAssignableFrom(descriptor.getPropertyType())) {
            return (Class<?>) getTypeParameters(klass, descriptor)[1];
        } else {
            throw new IllegalArgumentException("Property " + propertyName + " of "
                + klass.getName() + " is not a map; it is a " + descriptor.getPropertyType().getName());
        }
    }

    private static Type[] getTypeParameters(Class<?> klass, PropertyDescriptor descriptor) {
        Type returnType = descriptor.getReadMethod().getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            return ((ParameterizedType) returnType).getActualTypeArguments();
        } else {
            throw new IllegalArgumentException(
                "Property " + descriptor.getName() + " of " + klass.getName() + " is not generic");
        }
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> klass, String propertyName) {
        BeanInfo info = getBeanInfo(klass);
        PropertyDescriptor descriptor = null;
        for (PropertyDescriptor d : info.getPropertyDescriptors()) {
            if (d.getName().equals(propertyName)) {
                descriptor = d;
                break;
            }
        }
        if (descriptor == null) {
            throw new IllegalArgumentException(
                klass.getName() + " has no property " + propertyName);
        }
        return descriptor;
    }

    private static BeanInfo getBeanInfo(Class<?> klass) {
        try {
            return Introspector.getBeanInfo(klass);
        } catch (IntrospectionException e) {
            throw new CommonsSystemException("Failed to introspect on %s", e, klass.getName());
        }
    }

    private TypeTools() { }
}
