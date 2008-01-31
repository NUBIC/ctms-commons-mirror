package gov.nih.nci.cabig.ctms.tools.configuration;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "configuration")
public class DefaultConfigurationEntry extends ConfigurationEntry { }
