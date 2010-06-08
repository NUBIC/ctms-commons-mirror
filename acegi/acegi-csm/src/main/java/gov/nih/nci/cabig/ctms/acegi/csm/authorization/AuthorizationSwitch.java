/**
 * 
 */
package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * @author Rhett Sutphin
 */
public class AuthorizationSwitch {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationSwitch.class);

    private boolean on = true;

    private static AuthorizationSwitch instance = new AuthorizationSwitch();
    
    public static AuthorizationSwitch getInstance(){
    	return instance;
    }
    
    private AuthorizationSwitch() {
        if (log.isDebugEnabled()) log.debug(this + " created");
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if (log.isDebugEnabled()) log.debug(this + " turned " + (on ? "on" : "off"));
        this.on = on;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append("[0x")
            .append(System.identityHashCode(this)).append(']').toString();
    }
}
