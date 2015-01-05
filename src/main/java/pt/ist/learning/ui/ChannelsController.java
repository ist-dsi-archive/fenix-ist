package pt.ist.learning.ui;

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
