package gov.nih.nci.cabig.ctms.tools.hibernate;

import gov.nih.nci.cabig.ctms.CommonsError;
import gov.nih.nci.cabig.ctms.CommonsSystemException;
import gov.nih.nci.cabig.ctms.lang.ComparisonTools;
import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A base Hibernate UserType for instances of {@link gov.nih.nci.cabig.ctms.domain.CodedEnum}.
 * Required parameter:
 * <dl>
 *   <dt><code>enumClass</code></dt>
 *   <dd>Typesafe enumeration class of which this type instance will load instances</dd>
 * </dl>
 * Optional parameters:
 * <dl>
 *   <dt><code>factoryMethod</code></dt>
 *   <dd>The public static method to call to obtain an instance of the class from a database key.
 *       Default is <kbd>getByCode</kbd>.</dd>
 *   <dt><code>keyMethod</code></dt>
 *   <dd>The public method to call on an instance of the class to get the database key under
 *       which it should be stored.  Default is <kbd>getCode</kbd>.</dd>
 * </dl>
 * <p>
 * A subclass of this type will be necessary for each code type (integer, string, etc.) you want to
 * support.
 * </p>
 * @author Rhett Sutphin
 */
@SuppressWarnings("RawUseOfParameterizedType") // UserType interface uses unparameterized Class
public abstract class CodedEnumType implements UserType, ParameterizedType {
    protected static final String ENUM_CLASS_PARAM_KEY = "enumClass";
    protected static final String FACTORY_METHOD_PARAM_KEY = "factoryMethod";
    protected static final String KEY_METHOD_PARAM_KEY = "keyMethod";

    private static final String DEFAULT_FACTORY_METHOD_NAME = "getByCode";
    private static final String DEFAULT_KEY_METHOD_NAME = "getCode";

    private static final Class<?>[] NO_PARAMS = new Class[0];

    private Logger log = HibernateTypeTools.getLogger(getClass());

    private Properties parameterValues;

    protected final Properties getParameterValues() {
        return parameterValues;
    }

    ////// IMPLEMENTATION of ParameterizedType

    public void setParameterValues(Properties parameters) {
        this.parameterValues = new Properties(createDefaults());
        if (parameters != null) {
            this.parameterValues.putAll(parameters);
        }

        // call various methods so that they have an opportunity to fail during initialization
        getEnumClass();
        getFactoryMethod();
        getKeyMethod();
    }

    private Properties createDefaults() {
        Properties defaults = new Properties();
        defaults.put(FACTORY_METHOD_PARAM_KEY, DEFAULT_FACTORY_METHOD_NAME);
        defaults.put(KEY_METHOD_PARAM_KEY, DEFAULT_KEY_METHOD_NAME);
        return defaults;
    }

    ////// IMPLEMENTATION OF UserType

    /**
     * The JDBC typecode for the column in which the enum's code is stored.  E.g., if the
     * database column is an integer, this method should return {@link java.sql.Types#INTEGER}.
     */
    protected abstract int codeSqlType();

    /**
     * The java type of the parameter to the factory method.  E.g., if the factory method is
     * getByCode(int code), this method should return {@link Integer#TYPE}.
     */
    protected abstract Class codeJavaType();

    /**
     * Extract the key from the given result set.
     */
    protected abstract Object getKeyObject(ResultSet rs, String colname) throws SQLException;

    public final int[] sqlTypes() {
        return new int[] { codeSqlType() };
    }

    public Class returnedClass() {
        return getEnumClass();
    }

    private Class getEnumClass() {
        if (getEnumClassName() == null) {
            throw new CommonsError("required enumClass parameter not specified");
        }
        try {
            return Class.forName(getEnumClassName());
        } catch (ClassNotFoundException e) {
            throw new CommonsError("enumClass " + getEnumClassName() + " does not exist", e);
        }
    }

    private String getEnumClassName() {
        return getParameterValues().getProperty(ENUM_CLASS_PARAM_KEY);
    }

    private Method getFactoryMethod() {
        return getParameterNamedMethod(FACTORY_METHOD_PARAM_KEY, new Class[] { codeJavaType() });
    }

    private Method getKeyMethod() {
        return getParameterNamedMethod(KEY_METHOD_PARAM_KEY, NO_PARAMS);
    }

    private Method getParameterNamedMethod(String paramKey, Class[] parameterTypes) {
        String methodName = getParameterValues().getProperty(paramKey);
        try {
            return getEnumClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new CommonsError("enumClass " + getEnumClassName()
                + " has no method named " + methodName, e);
        }
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        Object key = getKeyObject(rs, names[0]);
        Object value = null;

        if (key != null) {
            Method factoryMethod = getFactoryMethod();
            try {
                value = factoryMethod.invoke(null, key);
            } catch (IllegalArgumentException iae) {
                throw new CommonsSystemException("Invocation of " + factoryMethod
                    + " with key=" + key + " (" + key.getClass().getName() + ") failed", iae);
            } catch (IllegalAccessException e) {
                throw new CommonsSystemException("Cannot access factoryMethod " + factoryMethod, e);
            } catch (InvocationTargetException e) {
                throw new CommonsSystemException("Invocation of " + factoryMethod + " failed", e);
            }
        }

        HibernateTypeTools.logReturn(log, names[0], value);
        return value;
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        Method keyMethod = getKeyMethod();
        Object key = null;
        try {
            if (value != null) {
                key = keyMethod.invoke(value);
            }
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Could not call keyMethod " + keyMethod + " on value " + value, iae);
        } catch (IllegalAccessException e) {
            throw new CommonsSystemException("Cannot access keyMethod " + keyMethod, e);
        } catch (InvocationTargetException e) {
            throw new CommonsSystemException("Invocation of " + keyMethod + " failed", e);
        }
        HibernateTypeTools.logBind(log, index, key);
        st.setObject(index, key, codeSqlType());
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return ComparisonTools.nullSafeEquals(x, y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
