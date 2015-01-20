/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static List<String> mainRoleKeys(User user) {
        List<String> roles = new ArrayList<>();
        if (new ActiveTeachersGroup().isMember(user)) {
            roles.add("TEACHER");
        }
        if (new ActiveStudentsGroup().isMember(user)) {
            roles.add("STUDENT");
        }
        if (new ActiveGrantOwner().isMember(user)) {
            roles.add("GRANT_OWNER");
        }
        if (new ActiveEmployees().isMember(user)) {
            roles.add("EMPLOYEE");
        }
        if (new ActiveResearchers().isMember(user)) {
            roles.add("RESEARCHER");
        }
        if (AlumniGroup.get().isMember(user)) {
            roles.add("ALUMNI");
        }
        return roles;
    }

    public static List<String> mainRoles(User user) {
        return mainRolesStream(user).collect(Collectors.toList());
    }

    public static String mainRolesStr(User user) {
        return mainRoles(user).stream().collect(Collectors.joining(", "));
    }

    private static Stream<String> mainRolesStream(User user) {
        return mainRoleKeys(user).stream().map(r -> BundleUtil.getString(Bundle.ENUMERATION, r));
    }
}
