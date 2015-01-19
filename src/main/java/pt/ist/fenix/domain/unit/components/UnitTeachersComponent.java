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