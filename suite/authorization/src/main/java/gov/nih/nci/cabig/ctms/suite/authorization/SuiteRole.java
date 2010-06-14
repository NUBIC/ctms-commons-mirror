package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.CommonsError;
import gov.nih.nci.cabig.ctms.domain.EnumHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * The unified suite roles.  This enum reflects all information known about each role.
 * <p>
 * See also <a href="https://cabig-kc.nci.nih.gov/CTMS/KC/index.php/Roles_-_The_Suite_v2.2">the
 * original hashing out of the roles across the suite</a>.
 *
 * @author Rhett Sutphin
 */
public enum SuiteRole {
    SYSTEM_ADMINISTRATOR,
    BUSINESS_ADMINISTRATOR,
    PERSON_AND_ORGANIZATION_INFORMATION_MANAGER,
    DATA_IMPORTER,
    USER_ADMINISTRATOR,
    STUDY_QA_MANAGER,
    STUDY_CREATOR,
    SUPPLEMENTAL_STUDY_INFORMATION_MANAGER,
    STUDY_TEAM_ADMINISTRATOR,
    STUDY_SITE_PARTICIPATION_ADMINISTRATOR,
    AE_RULE_AND_REPORT_MANAGER,
    STUDY_CALENDAR_TEMPLATE_BUILDER,
    REGISTRATION_QA_MANAGER,
    SUBJECT_MANAGER,
    STUDY_SUBJECT_CALENDAR_MANAGER,
    REGISTRAR,
    AE_REPORTER,
    AE_EXPEDITED_REPORT_REVIEWER,
    AE_STUDY_DATA_REVIEWER,
    LAB_IMPACT_CALENDAR_NOTIFIER,
    LAB_DATA_USER,
    DATA_READER,
    DATA_ANALYST;

    public static SuiteRole getByCsmName(String csmName) {
        for (SuiteRole role : values()) {
            if (role.getCsmName().equals(csmName)) return role;
        }
        throw new IllegalArgumentException("There is no suite role with the CSM name " + csmName);
    }

    private String description;
    private String displayName;
    private Set<ScopeType> scopes;

    private static Properties roleProperties;

    SuiteRole() {
        this.displayName = createDisplayName();
        this.description = createDescription();
        this.scopes = createScopes();
    }

    /**
     * The human-readable informal description of this role.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * A string usable for referring to this role in, e.g., a user interface.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * The string used to refer to this role in the CSM group and role tables.
     */
    public String getCsmName() {
        return name().toLowerCase();
    }

    /**
     * The axes on which this role is scoped.
     */
    public Set<ScopeType> getScopes() {
        return this.scopes;
    }

    public boolean isStudyScoped() {
        return getScopes().contains(ScopeType.STUDY);
    }

    public boolean isSiteScoped() {
        return getScopes().contains(ScopeType.SITE);
    }

    public boolean isScoped() {
        return isSiteScoped() || isStudyScoped();
    }

    private String createDisplayName() {
        return getRoleProperties().getProperty(
            getCsmName() + ".displayName", EnumHelper.titleCasedName(this));
    }

    private String createDescription() {
        return getRoleProperties().getProperty(getCsmName() + ".description");
    }

    private Set<ScopeType> createScopes() {
        String prop = getRoleProperties().getProperty(getCsmName() + ".scopes");
        if (prop == null) {
            return Collections.emptySet();
        } else {
            Set<ScopeType> creating = new LinkedHashSet<ScopeType>();
            for (String scope : prop.split("\\s+")) {
                creating.add(ScopeType.valueOf(scope.toUpperCase()));
            }
            return Collections.unmodifiableSet(creating);
        }
    }

    /**
     * Loads and returns the role.properties resource.  This resource contains all non-default
     * information about each role.
     */
    private synchronized static Properties getRoleProperties() {
        if (roleProperties == null) {
            roleProperties = new Properties();
            try {
                roleProperties.load(SuiteRole.class.getResourceAsStream("role.properties"));
            } catch (IOException e) {
                throw new CommonsError("Cannot load role info from properties", e);
            }
        }
        return roleProperties;
    }
}
