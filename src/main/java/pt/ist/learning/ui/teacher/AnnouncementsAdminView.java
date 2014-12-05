package pt.ist.learning.ui.teacher;

import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;

public class AnnouncementsAdminView extends JstlView {

    private static AnnouncementsAdminView instance;

    @Override
    protected void exposeHelpers(HttpServletRequest request) throws Exception {
        setServletContext(request.getServletContext());
        super.exposeHelpers(request);
        request.setAttribute("teacher$actual$page", "/cms/announcements.jsp");
    }

    @Override
    public String getUrl() {
        return "/teacher/executionCourse/executionCourseFrame.jsp";
    }

    public static AnnouncementsAdminView getInstance() {
        if(instance==null) {
            instance = new AnnouncementsAdminView();
        }
        return instance;
    }
}
