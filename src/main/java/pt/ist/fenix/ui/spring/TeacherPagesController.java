package pt.ist.fenix.ui.spring;

import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.spring.StrutsFunctionalityController;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.JstlView;

@Controller
@RequestMapping("/teacher/{executionCourseId}/pages")
public class TeacherPagesController extends StrutsFunctionalityController {

    @Autowired
    PagesAdminService service;

    @RequestMapping(method = RequestMethod.GET)
    public TeacherPagesView all(Model model, @PathVariable String executionCourseId) {
        ExecutionCourse executionCourse = getDomainObject(executionCourseId);
        Professorship professorship = executionCourse.getProfessorship(AccessControl.getPerson());
        AccessControl.check(person -> professorship != null && professorship.getPermissions().getSections());
        model.addAttribute("executionCourse", executionCourse);
        model.addAttribute("professorship", professorship);
        model.addAttribute("site", executionCourse.getSite());
        return new TeacherPagesView();
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    public class TeacherPagesView extends JstlView {

        @Override
        protected void exposeHelpers(HttpServletRequest request) throws Exception {
            setServletContext(request.getServletContext());
            super.exposeHelpers(request);
            request.setAttribute("teacher$actual$page", "/WEB-INF/fenix-learning/teacherPages.jsp");
        }

        @Override
        public String getUrl() {
            return "/teacher/executionCourse/executionCourseFrame.jsp";
        }

    }
}
