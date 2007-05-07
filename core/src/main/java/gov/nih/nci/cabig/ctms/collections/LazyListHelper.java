package gov.nih.nci.cabig.ctms.collections;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.list.LazyList;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Helps implementing a lazy lists within domain objects such that the decorated list
 * is used in the interface, but the raw underlying list is available for hibernate.
 *
 * Suggested use:
 *
 * <pre><code>public class Person {
 *     private LazyListHelper helper;
 *     public Person() {
 *         helper = new LazyListHelper();
 *         helper.add(Role.class);
 *     }
 *
 *     // ...
 *
 *     public List<Role> getRoles() {
 *         return helper.getLazyList(Role.class)
 *     }
 *
 *     @OneToMany
 *     @Etc.
 *     public List<Role> getRolesInternal() {
 *         return helper.getInternalList(Role.class)
 *     }
 *
 *     public void setRolesInternal(List<Role> roles) {
 *         return helper.setInternalList(Role.class, roles)
 *     }
 *
 *     // ...
 * }</pre></code>
 *
 * @see LazyList
 * @author Rhett Sutphin
 */
public class LazyListHelper {
    private Map<Class<?>, LazyState<?>> states;

    public LazyListHelper() {
        states = new HashMap<Class<?>, LazyState<?>>();
    }

    /**
     * Creates a list whose elements are the given type and whose contents will
     * be dynamically instantiated instances of the given class.  The provided
     * class must have a public default constructor.  If you need more elaborate
     * setup/initialization, use {@link #add(Class, Factory)}.
     *
     * @see InstantiateFactory
     */
    public <T> void add(Class<T> klass) {
        add(klass, new InstantiateFactory<T>(klass));
    }

    /**
     * Create a list whose elements are the given type and whose contents will be
     * filled on-demand using the given factory.
     */
    public <T> void add(Class<T> klass, Factory<T> factory) {
        states.put(klass, new LazyState<T>(factory));
    }

    public <T> void setInternalList(Class<T> klass, List<T> list) {
        getState(klass).setInternal(list);
    }

    public <T> List<T> getLazyList(Class<T> klass) {
        return getState(klass).getLazyList();
    }

    public <T> List<T> getInternalList(Class<T> klass) {
        return getState(klass).getInternal();
    }

    @SuppressWarnings("unchecked")
    private <T> LazyState<T> getState(Class<T> klass) {
        return (LazyState<T>) states.get(klass);
    }

    private static class LazyState<T> {
        private List<T> lazy;
        private List<T> internal;
        private Factory<T> factory;

        public LazyState(Factory<T> factory) {
            this.factory = factory;
            setInternal(new ArrayList<T>());
        }

        public synchronized void setInternal(List<T> list) {
            internal = list;
            lazy = LazyList.decorate(list, factory);
        }

        public List<T> getLazyList() {
            return lazy;
        }

        public List<T> getInternal() {
            return internal;
        }
    }
}
