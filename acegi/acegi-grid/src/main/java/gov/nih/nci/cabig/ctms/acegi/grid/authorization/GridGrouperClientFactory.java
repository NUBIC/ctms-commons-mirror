/**
 * 
 */
package gov.nih.nci.cabig.ctms.acegi.grid.authorization;

import gov.nih.nci.cagrid.gridgrouper.grouper.GrouperI;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface GridGrouperClientFactory {
	
	GrouperI newGridGrouperClient(String url);

}
