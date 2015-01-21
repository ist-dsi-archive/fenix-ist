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
package pt.ist.fenix.domain.unit.components;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.domain.ExecutionYear.readCurrentExecutionYear;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.CompetenceCourseGroupUnit;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.degree.components.DegreeSiteComponent;

import pt.ist.fenixedu.contracts.domain.Employee;

import com.google.common.collect.ImmutableMap;

@ComponentType(name = "unitCourses", description = "Courses of a Unit")
public class UnitCourses extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        String courseComponentUrl =
                DegreeSiteComponent.pageForComponent(page.getSite(), CompetenceCourseComponent.class).map(Page::getAddress)
                        .orElse("#");
        if (unit(page) instanceof DepartmentUnit) {
            DepartmentUnit departmentUnit = ofNullable((DepartmentUnit) unit(page)).orElseGet(() -> getPersonDepartmentUnit());
            globalContext.put("scientificAreaUnits", getScientificAreaUnits(departmentUnit, courseComponentUrl));
            globalContext.put("department", departmentUnit.getDepartment());
            globalContext.put("departmentUnit", departmentUnit);
        } else {
            globalContext.put("scientificAreaUnits", getScientificAreaUnits(unit(page), courseComponentUrl));
        }
    }

    public List<Map> getScientificAreaUnits(Unit unit, String courseComponentUrl) {
        return unit.getSubUnits().stream().filter(Unit::isScientificAreaUnit).map(ScientificAreaUnit.class::cast)
                .sorted(ScientificAreaUnit.COMPARATOR_BY_NAME_AND_ID).map(subunit -> wrap(subunit, courseComponentUrl))
                .collect(toList());
    }

    public Map wrap(ScientificAreaUnit scientificAreaUnit, String courseComponentUrl) {
        List<Map> competenceCoursesWraps =
                scientificAreaUnit.getCompetenceCourseGroupUnits().stream()
                        .map(competenceCourseGroupUnit -> wrap(competenceCourseGroupUnit, courseComponentUrl)).collect(toList());
        return ImmutableMap.of("name", scientificAreaUnit.getNameI18n().toLocalizedString(), "competenceCourseGroupUnits",
                competenceCoursesWraps, "hasCompetenceCourses",
                competenceCoursesWraps.stream().anyMatch(wrap -> (boolean) wrap.get("hasCompetenceCourses")));
    }

    public Map wrap(CompetenceCourseGroupUnit competenceCourseGroupUnit, String courseComponentUrl) {
        List<CompetenceCourse> competenceCourses =
                competenceCourseGroupUnit.getCompetenceCoursesByExecutionYear(readCurrentExecutionYear());
        return ImmutableMap.of("name", competenceCourseGroupUnit.getNameI18n().toLocalizedString(), "competenceCourses",
                approvedCompetenceCourses(competenceCourses).map(competenceCourse -> wrap(competenceCourse, courseComponentUrl))
                        .collect(toList()), "hasCompetenceCourses", approvedCompetenceCourses(competenceCourses).count() > 0);
    }

    private Stream<CompetenceCourse> approvedCompetenceCourses(Collection<CompetenceCourse> competenceCourses) {
        return competenceCourses.stream().filter(CompetenceCourse::isApproved);
    }

    public Map wrap(CompetenceCourse competenceCourse, String courseComponentUrl) {
        return ImmutableMap.of("name", competenceCourse.getNameI18N().toLocalizedString(), "acronym",
                competenceCourse.getAcronym(), "url", format("%s/%s", courseComponentUrl, competenceCourse.getExternalId()),
                "approved", competenceCourse.isApproved());
    }

    public DepartmentUnit getPersonDepartmentUnit() {
        final User user = Authenticate.getUser();
        final Person person = user == null ? null : user.getPerson();
        final Employee employee = person == null ? null : person.getEmployee();
        final Department department = employee == null ? null : employee.getCurrentDepartmentWorkingPlace();
        return department == null ? null : department.getDepartmentUnit();
    }
}
