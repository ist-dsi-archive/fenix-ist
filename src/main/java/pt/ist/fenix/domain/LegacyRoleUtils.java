package pt.ist.fenix.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.accessControl.ActiveStudentsGroup;
import org.fenixedu.academic.domain.accessControl.ActiveTeachersGroup;
import org.fenixedu.academic.domain.accessControl.AlumniGroup;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixedu.contracts.domain.accessControl.ActiveEmployees;
import pt.ist.fenixedu.contracts.domain.accessControl.ActiveGrantOwner;
import pt.ist.fenixedu.contracts.domain.accessControl.ActiveResearchers;

public class LegacyRoleUtils {
    public static List<String> mainRoles(User user) {
        List<String> roles = new ArrayList<>();
        if (new ActiveTeachersGroup().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "TEACHER"));
        }
        if (new ActiveStudentsGroup().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "STUDENT"));
        }
        if (new ActiveGrantOwner().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "GRANT_OWNER"));
        }
        if (new ActiveEmployees().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "EMPLOYEE"));
        }
        if (new ActiveResearchers().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "RESEARCHER"));
        }
        if (AlumniGroup.get().isMember(user)) {
            roles.add(BundleUtil.getString(Bundle.ENUMERATION, "ALUMNI"));
        }
        return roles;
    }

    public static String mainRolesStr(User user) {
        return mainRoles(user).stream().collect(Collectors.joining(", "));
    }
}
