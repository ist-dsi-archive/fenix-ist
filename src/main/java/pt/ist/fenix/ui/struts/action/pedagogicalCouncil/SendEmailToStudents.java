package pt.ist.fenix.ui.struts.action.pedagogicalCouncil;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.accessControl.StudentGroup;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.organizationalStructure.PedagogicalCouncilUnit;
import org.fenixedu.academic.domain.util.email.EmailBean;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenix.ui.struts.action.PedagogicalCommunicationApp;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = PedagogicalCommunicationApp.class, path = "send-email-to-students",
        titleKey = "link.sendEmailToStudents", bundle = "PedagogicalCouncilResources")
@Mapping(module = "pedagogicalCouncil", path = "/sendEmailToStudents")
@Forwards({ @Forward(name = "showDegrees", path = "/pedagogicalCouncil/sendEmailToStudents.jsp"),
        @Forward(name = "sendEmail", path = "/messaging/emails.do?method=newEmail") })
public class SendEmailToStudents extends FenixDispatchAction {

    @EntryPoint
    public ActionForward prepare(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();

        SendStudentEmailBean bean = new SendStudentEmailBean();
        bean.setDegreeType(DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE); // default
        bean.setExecutionYear(currentExecutionYear); // default

        return selectDegreeType(mapping, actionForm, request, response, bean);
    }

    public ActionForward selectDegreeType(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, SendStudentEmailBean sendStudentEmailBean) throws Exception {
        final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
        request.setAttribute("currentExecutionYear", currentExecutionYear);
        if (sendStudentEmailBean == null) {
            final Degree degree = FenixFramework.getDomainObject(request.getParameter("degreeOID"));

            sendStudentEmailBean = new SendStudentEmailBean();
            sendStudentEmailBean.setDegree(degree);
            sendStudentEmailBean.setDegreeType(degree.getDegreeType());
            sendStudentEmailBean.setExecutionYear(currentExecutionYear); // default
        } else {
            if (sendStudentEmailBean.getExecutionYear() == null) {
                sendStudentEmailBean.setExecutionYear(currentExecutionYear); // default
            }
            if (sendStudentEmailBean.getDegreeType() == null) {
                sendStudentEmailBean.setDegreeType(DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE); // default
            }
        }

        request.setAttribute("electionPeriodBean", sendStudentEmailBean);
        request.setAttribute("currentExecutionYear", currentExecutionYear);
        request.setAttribute("degrees", Degree.readAllByDegreeType(sendStudentEmailBean.getDegreeType()));
        return mapping.findForward("showDegrees");
    }

    public ActionForward selectDegreeTypePostBack(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SendStudentEmailBean periodBean = getRenderedObject();
        RenderUtils.invalidateViewState();

        return selectDegreeType(mapping, actionForm, request, response, periodBean);

    }

    public ActionForward sendMail(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ExecutionYear executionYear = ExecutionYear.readExecutionYearByName(request.getParameter("executionYear"));
        CurricularYear curricularYear = CurricularYear.readByYear(Integer.valueOf(request.getParameter("curricularYear")));
        Degree degree = FenixFramework.getDomainObject(request.getParameter("degreeId"));

        StudentGroup studentsByDegreeAndCurricularYear = StudentGroup.get(degree, curricularYear, executionYear);

        String message =
                MessageResources.getMessageResources(Bundle.PEDAGOGICAL).getMessage("label.mail.student.year.degree",
                        curricularYear.getYear().toString(), degree.getSigla());

        Recipient recipient = Recipient.newInstance(message, studentsByDegreeAndCurricularYear);
        EmailBean bean = new EmailBean();
        bean.setRecipients(Collections.singletonList(recipient));
        bean.setSender(PedagogicalCouncilUnit.getPedagogicalCouncilUnit().getUnitBasedSenderSet().iterator().next());

        request.setAttribute("emailBean", bean);

        return mapping.findForward("sendEmail");
    }
}