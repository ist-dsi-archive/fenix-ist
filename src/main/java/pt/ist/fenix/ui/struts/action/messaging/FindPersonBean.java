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
package pt.ist.fenix.ui.struts.action.messaging;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public class FindPersonBean implements Serializable {
    public static enum SearchRoleType implements IPresentableEnum {
        STUDENT, TEACHER, GRANT_OWNER, EMPLOYEE, ALUMNI;

        @Override
        public String getLocalizedName() {
            return BundleUtil.getString(Bundle.ENUMERATION, name());
        }
    }

    private static final long serialVersionUID = -7868952167229025567L;

    private SearchRoleType roleType;
    private DegreeType degreeType;
    private Degree degree;
    private Department department;
    private String name;
    private Boolean viewPhoto;

    public FindPersonBean() {

    }

    public SearchRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(SearchRoleType roleType) {
        this.roleType = roleType;
    }

    public DegreeType getDegreeType() {
        if (!SearchRoleType.STUDENT.equals(roleType)) {
            return null;
        }
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getViewPhoto() {
        return viewPhoto;
    }

    public void setViewPhoto(Boolean viewPhoto) {
        this.viewPhoto = viewPhoto;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public List<Degree> getPossibleDegrees() {
        return Collections.unmodifiableList(Degree.readAllByDegreeType(degreeType));
    }

    public String getDepartmentExternalId() {
        if (department != null && SearchRoleType.TEACHER.equals(roleType)) {
            return department.getExternalId();
        }
        return null;
    }

    public String getDegreeExternalId() {
        if (degree != null && SearchRoleType.STUDENT.equals(roleType)) {
            return degree.getExternalId();
        }
        return null;
    }

}
