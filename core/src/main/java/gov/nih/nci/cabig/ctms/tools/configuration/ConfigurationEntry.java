package gov.nih.nci.cabig.ctms.tools.configuration;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * @author Rhett Sutphin
 */
// This has to be a MappedSuperclass because hibernate doesn't have any
// inheritance mappings that say "store everything from every class in
// separate tables."
@MappedSuperclass
public abstract class ConfigurationEntry {
    private String key;
    private String value;
    private Integer version;

    @Id // assigned identifier
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
