package pt.ist.learning.unit;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@SuppressWarnings("deprecation")
@ComponentType(description = "Shows the teachers of the site's Scientific Area", name = "Scientific Area Teachers")
public class ScientificAreaTeachers extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Unit unit = unit(page);
        //TODO: employees
        /*
        Map<ProfessionalCategory, SortedSet<Teacher>> teachers =
                unit.getDepartmentUnit()
                        .getDepartment()
                        .getAllTeachers(new YearMonthDay(), new YearMonthDay().plusDays(1))
                        .stream()
                        .filter(teacher -> teacher.getCurrentSectionOrScientificArea() == unit && teacher.getCategory() != null)
                        .collect(
                                groupingBy(Teacher::getCategory, TreeMap::new, toCollection(() -> new TreeSet<Teacher>(
                                        Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER))));

        globalContext.put("scientificAreaTeachers", teachers);
        globalContext.put("categories", teachers.keySet());
        */
    }
}
