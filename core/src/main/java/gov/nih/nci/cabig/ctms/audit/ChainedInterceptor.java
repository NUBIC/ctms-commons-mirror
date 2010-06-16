package gov.nih.nci.cabig.ctms.audit;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of the {@link Interceptor} interface that allows the chaining
 * of several different instances of the same interface.
 *
 * @author Saurabh Agrawal
 * @see Interceptor
 */
public class ChainedInterceptor implements Interceptor {

    // Interceptors to be chained
    private Interceptor[] interceptors;

    /**
     * Constructor
     */
    public ChainedInterceptor() {
        super();
    }

    public void afterTransactionBegin(Transaction transaction) {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterTransactionBegin(transaction);
        }
    }

    public void afterTransactionCompletion(Transaction transaction) {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterTransactionCompletion(transaction);
        }

    }

    public void beforeTransactionCompletion(Transaction transaction) {
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeTransactionCompletion(transaction);
        }
    }

    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        int[] result = null;
        for (Interceptor element : interceptors) {
            result = element.findDirty(entity, id, currentState, previousState, propertyNames, types);
            if (result != null) {
                /*
                     * If any interceptor has returned something not null, stop the
                     * chain
                     */
                break;
            }
        }
        return result;
    }

    public Object getEntity(String entityName, Serializable id) {
        Object result = null;
        for (Interceptor element : interceptors) {
            result = element.getEntity(entityName, id);
            if (result != null) {
                /*
                     * If any interceptor has returned something not null, stop the
                     * chain
                     */
                break;
            }
        }
        return result;
    }

    public String getEntityName(Object object) {
        String result = null;
        for (Interceptor element : interceptors) {
            result = element.getEntityName(object);
            if (result != null) {
                /*
                     * If any interceptor has returned something not null, stop the
                     * chain
                     */
                break;
            }
        }
        return result;
    }

    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
        Object result = null;
        for (Interceptor element : interceptors) {
            result = element.instantiate(entityName, entityMode, id);
            if (result != null) {
                /*
                     * If any interceptor has returned something not null, stop the
                     * chain
                     */
                break;
            }
        }
        return result;

    }

    public Boolean isTransient(Object entity) {
        Boolean result = null;
        for (Interceptor element : interceptors) {
            result = element.isTransient(entity);
            if (result != null) {
                // If any interceptor has returned either true or false,
                // stop the chain
                break;
            }
        }
        return result;
    }

    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        for (Interceptor interceptor : interceptors) {
            interceptor.onCollectionRecreate(collection, key);
        }
    }

    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        for (Interceptor interceptor : interceptors) {
            interceptor.onCollectionRemove(collection, key);
        }
    }

    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
        for (Interceptor interceptor : interceptors) {
            interceptor.onCollectionUpdate(collection, key);
        }
    }

    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        for (Interceptor element : interceptors) {
            element.onDelete(entity, id, state, propertyNames, types);
        }
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
        throws CallbackException {
        boolean result = false;
        for (Interceptor element : interceptors) {
            if (element.onFlushDirty(entity, id, currentState, previousState, propertyNames, types)) {
                /*
                     * Returns true if one interceptor in the chain has modified the
                     * object current state result = true;
                     */
            }
        }
        return result;
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean result = false;
        for (Interceptor element : interceptors) {
            if (element.onLoad(entity, id, state, propertyNames, types)) {
                /*
                     * Returns true if one interceptor in the chain has modified the
                     * object state result = true;
                     */
            }
        }
        return result;
    }

    public String onPrepareStatement(String sql) {
        String result = null;
        for (Interceptor element : interceptors) {
            result = element.onPrepareStatement(sql);
            if (result != null) {
                /*
                     * If any interceptor has returned something not null, stop the
                     * chain
                     */
                break;
            }
        }
        return result;

    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean result = false;
        for (Interceptor element : interceptors) {
            if (element.onSave(entity, id, state, propertyNames, types)) {
                /*
                     * Returns true if one interceptor in the chain has modified the
                     * object state result = true;
                     */
            }
        }

        return result;
    }

    public void postFlush(Iterator entities) throws CallbackException {
        for (Interceptor element : interceptors) {
            element.postFlush(entities);
        }

    }

    public void preFlush(Iterator entities) throws CallbackException {
        for (Interceptor element : interceptors) {
            element.preFlush(entities);
        }
    }

    /**
     * Sets the instances of the {@link Interceptor} interface that are chained
     * within this interceptor.
     *
     * @param interceptors
     */
    @Required
    public void setInterceptors(Interceptor[] interceptors) {
        this.interceptors = interceptors;
    }

}