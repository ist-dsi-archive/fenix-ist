package pt.ist.fenix.domain.unit.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.function.Predicate;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenix.domain.unit.UnitSite;

@ComponentType(name = "Department Degrees", description = "Degrees information for a Department")
public class DepartmentDegrees extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        Department department = unit(page).getDepartment();
        Predicate<Degree> isActive = Degree::isActive;
        global.put("activeTypes",
                department.getDegreesSet().stream().filter(isActive).map(Degree::getDegreeType).collect(toList()));
        global.put("inactiveTypes", department.getDegreesSet().stream().filter(isActive.negate()).map(Degree::getDegreeType)
                .collect(toList()));
        global.put("degreesByType", department.getDegreesSet().stream().collect(groupingBy(Degree::getDegreeType)));
    }

    public static boolean supportsSite(Site site) {
        return site instanceof UnitSite && ((UnitSite) site).getUnit() instanceof DepartmentUnit;
    }

}