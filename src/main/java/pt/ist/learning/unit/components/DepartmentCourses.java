package pt.ist.learning.unit.components;

import com.google.common.collect.ImmutableMap;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.organizationalStructure.CompetenceCourseGroupUnit;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.domain.ExecutionYear.readCurrentExecutionYear;

@ComponentType(name = "departmentCourses", description = "Courses of a Department")
public class DepartmentCourses extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        if(unit(page) instanceof DepartmentUnit) {
            DepartmentUnit departmentUnit = ofNullable((DepartmentUnit) unit(page)).orElseGet(() -> getPersonDepartmentUnit());
            globalContext.put("scientificAreaUnits", getScientificAreaUnits(departmentUnit));
            globalContext.put("department", departmentUnit.getDepartment());
            globalContext.put("departmentUnit", departmentUnit);
        }
    }

    public List<Map> getScientificAreaUnits(DepartmentUnit unit) {
        return unit.getScientificAreaUnits().stream().map(this::wrap).collect(toList());
    }

    public Map wrap(ScientificAreaUnit scientificAreaUnit) {
        List<Map> competenceCoursesWraps = scientificAreaUnit.getCompetenceCourseGroupUnits().stream().map(this::wrap).collect(toList());
        return ImmutableMap.of("name", scientificAreaUnit.getNameI18n().toLocalizedString(),
                "competenceCourseGroupUnits", competenceCoursesWraps,
                "hasCompetenceCourses", competenceCoursesWraps.stream().anyMatch(wrap -> (boolean) wrap.get("hasCompetenceCourses")));
    }

    public Map wrap(CompetenceCourseGroupUnit competenceCourseGroupUnit) {
        List<CompetenceCourse> competenceCourses = competenceCourseGroupUnit.getCompetenceCoursesByExecutionYear(readCurrentExecutionYear());
        return ImmutableMap.of("name", competenceCourseGroupUnit.getNameI18n().toLocalizedString(),
                "competenceCourses", approvedCompetenceCourses(competenceCourses).map(this::wrap).collect(toList()),
                "hasCompetenceCourses", approvedCompetenceCourses(competenceCourses).count() > 0);
    }

    private Stream<CompetenceCourse> approvedCompetenceCourses(Collection<CompetenceCourse> competenceCourses) {
        return competenceCourses.stream().filter(CompetenceCourse::isApproved);
    }

    public Map wrap(CompetenceCourse competenceCourse) {
        return ImmutableMap.of("name", competenceCourse.getNameI18N().toLocalizedString(),
                "acronym", competenceCourse.getAcronym(), "url", format("course/%s", competenceCourse.getExternalId()),
                "approved", competenceCourse.isApproved());
    }

    public DepartmentUnit getPersonDepartmentUnit() {
        //TODO: employees
        /*final User user = Authenticate.getUser();
        final Person person = user == null ? null : Person.userToPerson.apply(user);
        final Employee employee = person == null ? null : person.getEmployee();
        final Department department = employee == null ? null : employee.getCurrentDepartmentWorkingPlace();
        return department == null ? null : department.getDepartmentUnit();
        */
        return null;
    }
}
