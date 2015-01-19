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
package pt.ist.fenix.ui.spring;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.ImmutableSet;

@SpringFunctionality(accessGroup = "logged", app = NewsController.class, title = "channels.title")
@RequestMapping("learning/channels")
public class ChannelsController {

    @RequestMapping
    public String bookmarks(Model model, @RequestParam(required = false) ExecutionSemester semester, @RequestParam(
            required = false) ExecutionDegree degree) {
        model.addAttribute("executions", Bennu.getInstance().getExecutionPeriodsSet().stream().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
        if (semester != null) {
            model.addAttribute("selectedSemester", semester);
            model.addAttribute("degrees", semester.getExecutionYear().getExecutionDegreesSortedByDegreeName());
        }
        if (semester != null && degree != null) {
            model.addAttribute("selectedDegree", degree);
            model.addAttribute(
                    "courses",
                    semester.getAssociatedExecutionCoursesSet().stream()
                            .filter(course -> isExecutionCourseForExecutionDegree(course, degree))
                            .sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR).collect(Collectors.toList()));
        }
        model.addAttribute("bookmarks", Authenticate.getUser().getBookmarksSet());
        model.addAttribute("slugs", ImmutableSet.of("announcement", "summary"));
        return "fenix-learning/channels";
    }

    private boolean isExecutionCourseForExecutionDegree(final ExecutionCourse executionCourse,
            final ExecutionDegree executionDegree) {
        for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
            final DegreeCurricularPlan degreeCurricularPlan = curricularCourse.getDegreeCurricularPlan();
            for (final ExecutionDegree otherExecutionDegree : degreeCurricularPlan.getExecutionDegreesSet()) {
                if (executionDegree == otherExecutionDegree) {
                    return true;
                }
            }
        }
        return false;
    }
}
