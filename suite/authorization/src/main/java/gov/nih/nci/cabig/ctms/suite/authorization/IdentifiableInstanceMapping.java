package gov.nih.nci.cabig.ctms.suite.authorization;

import java.util.List;

/**
 * Defines a bridge from the an object class' shared identifier to the actual object for a
 * particular application.  E.g., you might have a site mapping which is able to convert between
 * application site instances and the suite shared identity string for the site.
 *
 * @author Rhett Sutphin
 */
public interface IdentifiableInstanceMapping<S> {
    /**
     * Extract and return the shared identity from the given application instance object.
     */
    String getSharedIdentity(S instance);

    /**
     * Efficiently load the application instance objects for the given identities.
     */
    List<S> getApplicationInstances(List<String> identities);

    /**
     * Indicate whether the given object is an instance of the class which would be returned from
     * {@link #getApplicationInstances}.
     */
    boolean isInstance(Object o);
}
