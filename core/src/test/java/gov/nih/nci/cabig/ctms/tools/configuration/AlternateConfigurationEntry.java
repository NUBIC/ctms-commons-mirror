package gov.nih.nci.cabig.ctms.tools.configuration;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "alt_configuration")
public class AlternateConfigurationEntry extends ConfigurationEntry { }
