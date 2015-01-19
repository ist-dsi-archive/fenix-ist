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
package pt.ist.fenix.ui.struts.action.departmentAdmOffice;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.fenixedu.academic.ui.struts.action.teacher.SummariesManagementDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

@Mapping(module = "departmentAdmOffice", path = "/summariesManagement", formBean = "summariesManagementForm",
        functionality = TeacherSearchForSummariesManagement.class)
@Forwards({
        @Forward(name = "prepareInsertSummary", path = "/departmentAdmOffice/teacher/executionCourse/createSummary.jsp"),
        @Forward(name = "prepareShowSummaries", path = "/departmentAdmOffice/teacher/executionCourse/showSummaries.jsp"),
        @Forward(name = "showSummariesCalendar", path = "/departmentAdmOffice/teacher/executionCourse/showSummariesCalendar.jsp"),
        @Forward(name = "prepareInsertComplexSummary",
                path = "/departmentAdmOffice/teacher/executionCourse/createComplexSummary.jsp") })
public class SummariesManagementDAForDepartmentAdmOffice extends SummariesManagementDA {

    @Override
    protected ActionForward processForward(HttpServletRequest request, ActionForward forward) {
        return forward;
    }
}