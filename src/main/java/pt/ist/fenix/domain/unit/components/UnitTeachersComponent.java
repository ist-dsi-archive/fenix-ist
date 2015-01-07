package pt.ist.fenix.domain.unit.components;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "departmentTeachers", description = "Teachers information for a Department")
public class UnitTeachersComponent extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {}

    //TODO: professionalCategory
    /*
    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Unit unit = unit(page);
        globalContext.put("teachersByCategory", teachersByCategory(unit));
        globalContext.put("teachersByArea", teachersByArea(unit));
        globalContext.put("teachersWithoutArea", teachersWithoutArea(unit));
        globalContext.put("hasTeachersWithoutArea", teachersWithoutArea(unit).findAny().isPresent());
    }

    private SortedMap<ProfessionalCategory, TreeSet<Teacher>> teachersByCategory(Unit unit) {
        return unitTeachers(unit).filter(teacher -> teacher.getCategory() != null)
                .collect(groupingBy(Teacher::getCategory, TreeMap::new, toCollection(sortedTeacherFactory)));
    }

    private SortedMap<Unit, TreeSet<Teacher>> teachersByArea(Unit unit) {
         return unitTeachers(unit).filter(hasScientificArea)
                .collect(groupingBy(Teacher::getCurrentSectionOrScientificArea, mapFactory, toCollection(sortedTeacherFactory)));
    }

    private Stream<Teacher> teachersWithoutArea(Unit unit) {
        return unitTeachers(unit).filter(hasScientificArea.negate()).sorted(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
    }

    private Stream<Teacher> unitTeachers(Unit unit) {
        return unit.getDepartment().getAllCurrentTeachers().stream().sorted(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
    }

    private static Supplier<TreeSet<Teacher>> sortedTeacherFactory = () ->
            Sets.newTreeSet(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);

    private static Supplier<TreeMap<Unit, TreeSet<Teacher>>> mapFactory = () -> Maps.newTreeMap(Unit.COMPARATOR_BY_NAME_AND_ID);

    Predicate<Teacher> hasScientificArea = teacher -> teacher.getCurrentSectionOrScientificArea() != null;
    */
}