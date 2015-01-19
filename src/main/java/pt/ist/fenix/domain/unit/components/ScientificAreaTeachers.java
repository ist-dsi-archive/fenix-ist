package pt.ist.fenix.domain.unit.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.organizationalStructure.ScientificAreaUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenix.domain.unit.UnitSite;

@ComponentType(description = "Shows the teachers of the site's Scientific Area", name = "Scientific Area Teachers")
public class ScientificAreaTeachers extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Unit unit = unit(page);
        Map<TeacherCategory, SortedSet<Teacher>> teachers =
                unit.getDepartmentUnit()
                        .getDepartment()
                        .getAllTeachers(ExecutionYear.readCurrentExecutionYear())
                        .stream()
                        .filter(teacher -> teacher.getCategory() != null)
                        .filter(teacher -> teacher.getDepartment().getDepartmentUnit() == unit)
                        .collect(
                                groupingBy(Teacher::getCategory, TreeMap::new, toCollection(() -> new TreeSet<Teacher>(
                                        Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER))));

        globalContext.put("scientificAreaTeachers", teachers);
        globalContext.put("categories", teachers.keySet());
    }

    public static boolean supportsSite(Site site) {
        return site instanceof UnitSite && ((UnitSite) site).getUnit() instanceof ScientificAreaUnit;
    }
}
