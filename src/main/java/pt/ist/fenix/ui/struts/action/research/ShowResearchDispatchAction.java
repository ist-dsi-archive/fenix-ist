package pt.ist.fenix.ui.struts.action.research;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.ui.struts.action.base.FenixAction;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

@StrutsFunctionality(app = CurriculumApp.class, path = "research", titleKey = "label.curriculum.research")
@Mapping(module = "researcher", path = "/showResearch")
@Forwards(value = { @Forward(name = "showResearch", path = "/researcher/showResearch.jsp") })
public class ShowResearchDispatchAction extends FenixAction {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.setAttribute("researchers", Authenticate.getUser().getUsername());
        super.execute(mapping, actionForm, request, response);
        return mapping.findForward("showResearch");
    }
}
