package pt.ist.fenix.domain.unit.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherCategory;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ComponentType(name = "Unit Teachers", description = "Teachers information for a Department")
public class UnitTeachersComponent extends UnitSiteComponent {

    private static Supplier<TreeSet<Teacher>> sortedTeacherFactory = () -> Sets
            .newTreeSet(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
    private static Supplier<TreeMap<Unit, TreeSet<Teacher>>> mapFactory = () -> Maps.newTreeMap(Unit.COMPARATOR_BY_NAME_AND_ID);
    Predicate<Teacher> hasScientificArea = teacher -> teacher.getDepartment() != null
            && teacher.getDepartment().getDepartmentUnit() != null;

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Unit unit = unit(page);
        globalContext.put("teachersByCategory", teachersByCategory(unit));
        globalContext.put("teachersByArea", teachersByArea(unit));
        globalContext.put("teachersWithoutArea", teachersWithoutArea(unit));
        globalContext.put("hasTeachersWithoutArea", teachersWithoutArea(unit).findAny().isPresent());
    }

    private SortedMap<TeacherCategory, TreeSet<Teacher>> teachersByCategory(Unit unit) {
        return unitTeachers(unit).filter(teacher -> teacher.getCategory() != null).collect(
                groupingBy(Teacher::getCategory, TreeMap::new, toCollection(sortedTeacherFactory)));
    }

    private SortedMap<Unit, TreeSet<Teacher>> teachersByArea(Unit unit) {
        return unitTeachers(unit).filter(hasScientificArea)
                .collect(
                        groupingBy(teacher -> teacher.getDepartment().getDepartmentUnit(), mapFactory,
                                toCollection(sortedTeacherFactory)));
    }

    private Stream<Teacher> teachersWithoutArea(Unit unit) {
        return unitTeachers(unit).filter(hasScientificArea.negate()).sorted(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
    }

    private Stream<Teacher> unitTeachers(Unit unit) {
        return unit.getDepartmentUnit().getDepartment().getAllCurrentTeachers().stream()
                .sorted(Teacher.TEACHER_COMPARATOR_BY_CATEGORY_AND_NUMBER);
    }
}