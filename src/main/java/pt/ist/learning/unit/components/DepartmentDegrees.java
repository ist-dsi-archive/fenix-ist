package pt.ist.learning.unit.components;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@ComponentType(name = "departmentDegrees", description = "Degrees information for a Department")
public class DepartmentDegrees extends UnitSiteComponent {

    @Override public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        Department department = unit(page).getDepartment();
        Predicate<Degree> isActive = Degree::isActive;
        global.put("activeTypes", department.getDegreesSet().stream().filter(isActive).map(Degree::getDegreeType).collect(toList()));
        global.put("inactiveTypes",  department.getDegreesSet().stream().filter(isActive.negate()).map(Degree::getDegreeType).collect(toList()));
        global.put("degreesByType", department.getDegreesSet().stream().collect(groupingBy(Degree::getDegreeType)));
    }


}