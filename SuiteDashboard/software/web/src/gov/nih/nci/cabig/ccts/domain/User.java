package gov.nih.nci.cabig.ccts.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User extends Person {

	protected String loginId;
	protected String emailAddress;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (emailAddress != null ? !emailAddress.equals(user.emailAddress) : user.emailAddress != null) return false;
        if (loginId != null ? !loginId.equals(user.loginId) : user.loginId != null) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = (loginId != null ? loginId.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        return result;
    }

}
