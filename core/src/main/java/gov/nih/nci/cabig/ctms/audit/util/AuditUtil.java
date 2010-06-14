package gov.nih.nci.cabig.ctms.audit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class AuditUtil {
    private static final Logger log = LoggerFactory.getLogger(AuditUtil.class);

    /**
     * Gets the id of the persisted object
     *
     * @param entity the object to get the id from
     * @return object Id or null if either id is null or entity does not
     *         implement Integer getId() method.
     */
    public static Integer getObjectId(final Object entity) {
        Class<?> objectClass = entity.getClass();
        Method[] methods = objectClass.getMethods();

        // FIXME:Saurabh make sure it works for the Long also
        Integer persistedObjectId = null;
        for (Method element : methods) {
            if ("getId".equals(element.getName())) {
                try {
                    persistedObjectId = (Integer) element.invoke(entity);
                    break;
                } catch (RuntimeException e) {
                    log.error("Extracting ID from instance of " + entity.getClass() + " failed", e);
                } catch (InvocationTargetException e) {
                    log.error("Extracting ID from instance of " + entity.getClass() + " failed", e);
                } catch (IllegalAccessException e) {
                    log.error("Extracting ID from instance of " + entity.getClass() + " failed", e);
                }
            }
        }
        return persistedObjectId;
    }
}
