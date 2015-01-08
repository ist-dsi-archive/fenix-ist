package pt.ist.fenix.ui.struts.action.externalServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.cms.domain.Category;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/announcementsRSS", module = "external")
public class LegacyRSSAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DomainObject obj = FenixFramework.getDomainObject(request.getParameter("announcementBoardId"));
        if (FenixFramework.isDomainObjectValid(obj) && obj instanceof Category) {
            Category cat = (Category) obj;
            response.setHeader("Location", cat.getRssUrl());
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            return null;
        }
        response.sendError(404);
        return null;
    }
}
