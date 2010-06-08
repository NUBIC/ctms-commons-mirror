package gov.nih.nci.cabig.ctms.suite.authorization.domain;

import gov.nih.nci.cabig.ctms.suite.authorization.StudyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class TestStudyMapping implements StudyMapping<TestStudy> {
    public String getSharedIdentity(TestStudy study) {
        return study.getIdent();
    }

    public boolean isInstance(Object o) {
        return o instanceof TestStudy;
    }

    public List<TestStudy> getApplicationInstances(List<String> identities) {
        List<TestStudy> sites = new ArrayList<TestStudy>(identities.size());
        for (String identity : identities) {
            sites.add(new TestStudy(identity));
        }
        return sites;
    }
}
