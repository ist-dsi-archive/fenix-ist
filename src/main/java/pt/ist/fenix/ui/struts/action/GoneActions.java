package pt.ist.fenix.ui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.struts.annotations.Mapping;

public class GoneActions extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.sendError(410, "Gone for good");
        return null;
    }

    @Mapping(path = "/executionCourse", module = "publico")
    public static class OldExecutionCourseDA extends GoneActions {
    }

    @Mapping(path = "/announcementManagement", module = "publico")
    public static class OldAnnouncementManagementDA extends GoneActions {
    }

    @Mapping(path = "/showDegreeTheses", module = "publico")
    public static class OldShowDegreeThesisDA extends GoneActions {
    }

    @Mapping(path = "/showDegreeSite", module = "publico")
    public static class OldShowDegreeSiteDA extends GoneActions {
    }

    @Mapping(path = "/department/theses", module = "publico")
    public static class OldDepartmentThesisDA extends GoneActions {
    }

    @Mapping(path = "/chooseContextDANew", module = "publico")
    public static class OldChooseContextDA extends GoneActions {
    }

    @Mapping(path = "/viewClassTimeTableNew", module = "publico")
    public static class OldViewClassTimeTableDA extends GoneActions {
    }

    @Mapping(path = "/chooseExamsMapContextDANew", module = "publico")
    public static class OldChooseExamsMapContextDA extends GoneActions {
    }
}
