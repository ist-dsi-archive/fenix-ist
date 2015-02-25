/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.ui.struts.action.academicAdministration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import org.fenixedu.academic.domain.curriculum.CurriculumValidationEvaluationPhase;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationType;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.exceptions.EnrollmentDomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.RootCurriculumGroup;
import org.fenixedu.academic.dto.degreeAdministrativeOffice.gradeSubmission.MarkSheetEnrolmentEvaluationBean;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.dto.student.StudentsSearchBean;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentEnrollmentBean;
import org.fenixedu.academic.predicate.IllegalDataAccessException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.student.enrolment.bolonha.EnrolBolonhaStudent;
import org.fenixedu.academic.service.services.student.enrolment.bolonha.EnrolBolonhaStudentInCurriculumValidationContext;
import org.fenixedu.academic.ui.struts.FenixActionForm;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.EditCandidacyInformationDA.ChooseRegistrationOrPhd;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.bolonha.CurriculumValidationServicesHelper;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenix.domain.student.evaluation.PreBolognaEvaluationManagement;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = FenixISTAcademicApp.class, path = "manage-prebologna",
        titleKey = "label.academicAdministration.managePreBologna")
@Mapping(path = "/curriculumValidation", module = "academicAdministration", formBeanClass = FenixActionForm.class)
@Forwards({
        @Forward(name = "show-curriculum-validation-options",
                path = "/academicAdministration/curriculumValidation/curriculumValidationOperations.jsp"),
        @Forward(name = "show-degree-modules-to-enrol",
                path = "/academicAdministration/curriculumValidation/showDegreeModulesToEnrol.jsp"),
        @Forward(name = "show-set-evaluations-form", path = "/academicAdministration/curriculumValidation/setEvaluationsForm.jsp"),
        @Forward(name = "show-edit-evaluation-form", path = "/academicAdministration/curriculumValidation/editEvaluationForm.jsp"),
        @Forward(name = "show-set-end-stage-date-form", path = "/academicAdministration/curriculumValidation/setStageDate.jsp"),
        @Forward(name = "registrationConclusion",
                path = "/academicAdministration/curriculumValidation/registrationConclusion.jsp"),
        @Forward(name = "viewStudentDetails",
                path = "/academicAdministration/curriculumValidation/viewPreBolognaStudentDetails.jsp"),
        @Forward(name = "viewRegistrationDetails",
                path = "/academicAdministration/curriculumValidation/viewPreBolognaRegistrationDetails.jsp"),
        @Forward(name = "search", path = "/academicAdministration/curriculumValidation/searchStudents.jsp") })
public class AcademicAdminOfficeCurriculumValidationDA extends FenixDispatchAction {

    private static final Logger logger = LoggerFactory.getLogger(AcademicAdminOfficeCurriculumValidationDA.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        if (studentCurricularPlan != null) {
            request.setAttribute("studentCurriculumValidationAllowed",
                    PreBolognaEvaluationManagement.getEvaluationForCurriculumValidationAllowed(studentCurricularPlan));
        }

        return super.execute(mapping, actionForm, request, response);
    }

    @EntryPoint
    public ActionForward prepareSearch(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {

        StudentsSearchBean studentsSearchBean = getRenderedObject();

        if (studentsSearchBean == null) { // 1st time
            studentsSearchBean = new StudentsSearchBean();
        } else {

            final Set<Student> students = studentsSearchBean.search();
            Set<Student> result = new HashSet<Student>();

            for (Student student : students) {
                for (Registration registration : student.getAllRegistrations()) {
                    if (!registration.isBolonha()) {
                        result.add(student);
                        break;
                    }
                }
            }

            if (result.size() == 1) {
                Student student = result.iterator().next();
                request.setAttribute("student", student);
                request.setAttribute("choosePhdOrRegistration", new ChooseRegistrationOrPhd(student));
                return mapping.findForward("viewStudentDetails");
            }
            request.setAttribute("students", result);
        }

        request.setAttribute("studentsSearchBean", studentsSearchBean);
        return mapping.findForward("search");
    }

    protected Registration getAndSetRegistration(final HttpServletRequest request) {
        final String registrationID =
                getFromRequest(request, "registrationID") != null ? getFromRequest(request, "registrationID").toString() : getFromRequest(
                        request, "registrationId").toString();
        final Registration registration = FenixFramework.getDomainObject(registrationID);
        request.setAttribute("registration", registration);
        return registration;
    }

    public ActionForward visualizeRegistration(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        getAndSetRegistration(request);
        return mapping.findForward("viewRegistrationDetails");
    }

    public ActionForward visualizeStudent(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        getAndSetStudent(request);
        return mapping.findForward("viewStudentDetails");
    }

    private Student getAndSetStudent(final HttpServletRequest request) {
        final String studentID = getFromRequest(request, "studentID").toString();
        final Student student = FenixFramework.getDomainObject(studentID);
        request.setAttribute("student", student);
        request.setAttribute("choosePhdOrRegistration", new ChooseRegistrationOrPhd(student));
        return student;
    }

    public ActionForward prepareCurriculumValidation(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        request.setAttribute("studentCurriculumValidationAllowed",
                studentCurricularPlan.getEvaluationForCurriculumValidationAllowed());
        return mapping.findForward("show-curriculum-validation-options");
    }

    public ActionForward prepareStudentEnrolment(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        ExecutionSemester executionSemester = readExecutionSemester(request);

        addDebtsWarningMessages(studentCurricularPlan.getRegistration().getStudent(), executionSemester, request);

        request.setAttribute(
                "bolonhaStudentEnrollmentBean",
                readBolonhaStudentEnrollmentBean(request, studentCurricularPlan, executionSemester, null,
                        CurricularRuleLevel.ENROLMENT_NO_RULES, true));

        return mapping.findForward("show-degree-modules-to-enrol");
    }

    public ActionForward prepareStudentEnrolmentPostback(ActionMapping mapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = prepareStudentEnrolment(mapping, actionForm, request, response);

        RenderUtils.invalidateViewState("student.enrolment.bean");
        RenderUtils.invalidateViewState("student.enrolment.bean.execution.semester");

        return forward;
    }

    public ActionForward enrolInDegreeModules(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        final BolonhaStudentEnrollmentBean bolonhaStudentEnrollmentBean = getBolonhaStudentEnrollmentBeanFromViewState();
        try {
            final RuleResult ruleResults =
                    EnrolBolonhaStudentInCurriculumValidationContext.run(

                    bolonhaStudentEnrollmentBean.getStudentCurricularPlan(), bolonhaStudentEnrollmentBean.getExecutionPeriod(),
                            bolonhaStudentEnrollmentBean.getDegreeModulesToEvaluate(),
                            bolonhaStudentEnrollmentBean.getCurriculumModulesToRemove(),
                            bolonhaStudentEnrollmentBean.getCurricularRuleLevel());

            if (!bolonhaStudentEnrollmentBean.getDegreeModulesToEvaluate().isEmpty()
                    || !bolonhaStudentEnrollmentBean.getCurriculumModulesToRemove().isEmpty()) {
                addActionMessage("success", request, "label.save.success");
            }

            if (ruleResults.isWarning()) {
                addRuleResultMessagesToActionMessages("warning", request, ruleResults);
            }

        } catch (EnrollmentDomainException ex) {
            addRuleResultMessagesToActionMessages("error", request, ex.getFalseResult());

            return prepareStudentEnrolment(mapping, form, request, response);

        } catch (DomainException ex) {
            addActionMessage("error", request, ex.getKey(), ex.getArgs());

            return prepareStudentEnrolment(mapping, form, request, response);
        }

        RenderUtils.invalidateViewState();

        return prepareStudentEnrolment(mapping, form, request, response);
    }

    public ActionForward prepareSetEvaluations(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        ExecutionSemester executionSemester = readExecutionSemester(request);

        RenderUtils.invalidateViewState("student.enrolment.bean");
        RenderUtils.invalidateViewState("student.enrolment.bean.execution.semester");

        request.setAttribute(
                "bolonhaStudentEnrollmentBean",
                readBolonhaStudentEnrollmentBean(request, studentCurricularPlan, executionSemester, null,
                        CurricularRuleLevel.ENROLMENT_NO_RULES, true));

        java.util.List<java.util.List<MarkSheetEnrolmentEvaluationBean>> enrolmentEvaluationBeanList =
                new java.util.ArrayList<java.util.List<MarkSheetEnrolmentEvaluationBean>>();
        java.util.List<java.util.List<MarkSheetEnrolmentEvaluationBean>> finalEnrolmentEvaluationBeanList =
                new java.util.ArrayList<java.util.List<MarkSheetEnrolmentEvaluationBean>>();

        createTreeCurriculumModules(studentCurricularPlan, executionSemester, enrolmentEvaluationBeanList,
                finalEnrolmentEvaluationBeanList, null, false);

        request.setAttribute("finalEntriesList", finalEnrolmentEvaluationBeanList);
        request.setAttribute("entriesList", enrolmentEvaluationBeanList);

        return mapping.findForward("show-set-evaluations-form");
    }

    public ActionForward setEvaluations(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        List<List<MarkSheetEnrolmentEvaluationBean>> evaluationsList = getCurriculumEntries();

        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        ExecutionSemester selectedSemester = readExecutionSemester(request);

        try {
            studentCurricularPlan.setEvaluationsForCurriculumValidation(evaluationsList);

            for (int i = 0; i < evaluationsList.size(); i++) {
                RenderUtils.invalidateViewState("set.evaluations.form." + i);
            }

        } catch (DomainException e) {
            if ("error.grade.invalid.grade".equals(e.getMessage())) {
                addActionMessage("grade-messages", request, "error.invalid.grade");
            } else {
                throw e;
            }
        }

        return prepareSetEvaluations(mapping, actionForm, request, response);
    }

    public ActionForward prepareEditEvaluation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        Enrolment enrolment = getEnrolmentForEdition(request);
        ExecutionSemester executionSemester = readExecutionSemester(request);
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);

        java.util.List<Enrolment> enrolmentToUse = new java.util.ArrayList<Enrolment>();
        enrolmentToUse.add(enrolment);

        java.util.List<java.util.List<MarkSheetEnrolmentEvaluationBean>> enrolmentEvaluationBeanList =
                new java.util.ArrayList<java.util.List<MarkSheetEnrolmentEvaluationBean>>();
        createTreeCurriculumModules(studentCurricularPlan, executionSemester, enrolmentEvaluationBeanList, null, enrolmentToUse,
                true);

        request.setAttribute("entriesList", enrolmentEvaluationBeanList);

        request.setAttribute("allEvaluationsBound", enrolment.getEvaluationsSet());

        return mapping.findForward("show-edit-evaluation-form");
    }

    public ActionForward editEvaluations(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            List<List<MarkSheetEnrolmentEvaluationBean>> evaluationsList = getCurriculumEntries();

            StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);

            ExecutionSemester executionSemester = readExecutionSemester(request);
            studentCurricularPlan.setEvaluationsForCurriculumValidation(evaluationsList);

            return prepareSetEvaluations(mapping, actionForm, request, response);
        } catch (DomainException e) {
            if ("error.grade.invalid.grade".equals(e.getMessage())) {
                addActionMessage("grade-messages", request, "error.invalid.grade");

                return prepareEditEvaluation(mapping, actionForm, request, response);
            }

            throw e;
        }
    }

    public ActionForward prepareEditEndStageDate(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        ExecutionSemester executionSemester = readExecutionSemester(request);

        request.setAttribute(
                "bolonhaStudentEnrollmentBean",
                readBolonhaStudentEnrollmentBean(request, studentCurricularPlan, executionSemester, null,
                        CurricularRuleLevel.ENROLMENT_NO_RULES, true));

        return mapping.findForward("show-set-end-stage-date-form");
    }

    public ActionForward editEndStageDate(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        BolonhaStudentEnrollmentBean studentEnrolmentBean = readStudentEnrolmentBean(request);
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);

        studentCurricularPlan.editEndStageDate(studentEnrolmentBean.getEndStageDate());

        return prepareCurriculumValidation(mapping, actionForm, request, response);
    }

    public ActionForward removeEnrolmentEvaluation(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        EnrolmentEvaluation evaluationToVoid = readEnrolmentEvaluation(request);

        try {
            PreBolognaEvaluationManagement.deleteEnrolmentEvaluationCurriculumValidationContext(evaluationToVoid);
        } catch (DomainException de) {
            addActionMessage("error", request, de.getKey(), de.getArgs());
        }

        return prepareEditEvaluation(mapping, actionForm, request, response);
    }

    public ActionForward markAsTemporaryEnrolled(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) {
        Enrolment enrolment = readEnrolment(request);

        PreBolognaEvaluationManagement.markAsTemporaryEnrolled(enrolment);

        return prepareSetEvaluations(mapping, actionForm, request, response);
    }

    public ActionForward unEnrol(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Enrolment enrolment = readEnrolment(request);

        try {
            final RuleResult ruleResults =
                    EnrolBolonhaStudent.run(readStudentCurricularPlan(request), enrolment.getExecutionPeriod(),
                            new ArrayList<IDegreeModuleToEvaluate>(), Arrays.asList(new CurriculumModule[] { enrolment }),
                            CurricularRuleLevel.ENROLMENT_NO_RULES);

            if (ruleResults.isWarning()) {
                addRuleResultMessagesToActionMessages("warning", request, ruleResults);
            }

        } catch (EnrollmentDomainException ex) {
            addRuleResultMessagesToActionMessages("error", request, ex.getFalseResult());
        } catch (DomainException ex) {
            addActionMessage("error", request, ex.getKey(), ex.getArgs());
        }

        return prepareSetEvaluations(mapping, actionForm, request, response);
    }

    private Registration readRegistration(HttpServletRequest request) {
        return readStudentCurricularPlan(request).getRegistration();
    }

    public ActionForward prepareRegistrationConclusionProcess(ActionMapping mapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response) {
        RenderUtils.invalidateViewState();

        final Registration registration = readRegistration(request);

        request.setAttribute("registrationConclusionBean", buildRegistrationConclusionBean(registration));
        return mapping.findForward("registrationConclusion");
    }

    private RegistrationConclusionBean getRegistrationConclusionBeanFromViewState() {
        return (RegistrationConclusionBean) getObjectFromViewState("registrationConclusionBean");
    }

    public ActionForward doRegistrationConclusion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        final RegistrationConclusionBean registrationConclusionBean = getRegistrationConclusionBeanFromViewState();

        try {
            new CurriculumValidationServicesHelper().concludeRegistration(registrationConclusionBean);
            return prepareCurriculumValidation(mapping, form, request, response);
        } catch (final IllegalDataAccessException e) {
            addActionMessage("illegal.access", request, "error.not.authorized.to.registration.conclusion.process");
            request.setAttribute("registrationConclusionBean", registrationConclusionBean);
            return mapping.findForward("registrationConclusion");

        } catch (final DomainException e) {
            addActionMessage(request, e.getKey(), e.getArgs());
            request.setAttribute("registrationConclusionBean", registrationConclusionBean);
            return mapping.findForward("registrationConclusion");
        }
    }

    private RegistrationConclusionBean buildRegistrationConclusionBean(final Registration registration) {
        return new RegistrationConclusionBean(registration);
    }

    private Enrolment readEnrolment(HttpServletRequest request) {
        return getDomainObject(request, "enrolmentId");
    }

    private EnrolmentEvaluation readEnrolmentEvaluation(HttpServletRequest request) {
        return getDomainObject(request, "enrolmentEvaluationId");
    }

    private Enrolment getEnrolmentForEdition(HttpServletRequest request) {
        Enrolment enrolment = getDomainObject(request, "enrolmentId");

        request.setAttribute("enrolment", enrolment);
        return enrolment;
    }

    private List<List<MarkSheetEnrolmentEvaluationBean>> getCurriculumEntries() {
        return (List<List<MarkSheetEnrolmentEvaluationBean>>) getObjectFromViewState("set.evaluations.form");
    }

    private static final String[][] TYPE_AND_PHASE = new String[][] {
            { EnrolmentEvaluationType.NORMAL.name(), CurriculumValidationEvaluationPhase.FIRST_SEASON.name() },
            { EnrolmentEvaluationType.NORMAL.name(), CurriculumValidationEvaluationPhase.SECOND_SEASON.name() },
            { EnrolmentEvaluationType.NORMAL.name(), null }, { EnrolmentEvaluationType.IMPROVEMENT.name(), null },
            { EnrolmentEvaluationType.SPECIAL_SEASON.name(), null } };

    private void createTreeCurriculumModules(StudentCurricularPlan studentCurricularPlan, ExecutionSemester executionSemester,
            java.util.List<java.util.List<MarkSheetEnrolmentEvaluationBean>> enrolmentEvaluationBeanList,
            java.util.List<java.util.List<MarkSheetEnrolmentEvaluationBean>> finalEnrolmentEvaluationBeanList,
            java.util.List<Enrolment> enrolments, boolean forEdition) {
        RootCurriculumGroup module = studentCurricularPlan.getRoot();
        enrolments =
                enrolments != null ? enrolments : new java.util.ArrayList<Enrolment>(module.getEnrolmentsBy(executionSemester));

        for (Enrolment enrolment : enrolments) {
            java.util.List<MarkSheetEnrolmentEvaluationBean> markSheetList =
                    new java.util.ArrayList<MarkSheetEnrolmentEvaluationBean>();
            markSheetList.add(new MarkSheetEnrolmentEvaluationBean(enrolment, executionSemester, EnrolmentEvaluationType.NORMAL,
                    CurriculumValidationEvaluationPhase.FIRST_SEASON));
            markSheetList.add(new MarkSheetEnrolmentEvaluationBean(enrolment, executionSemester, EnrolmentEvaluationType.NORMAL,
                    CurriculumValidationEvaluationPhase.SECOND_SEASON));
            markSheetList.add(new MarkSheetEnrolmentEvaluationBean(enrolment, executionSemester,
                    EnrolmentEvaluationType.SPECIAL_SEASON, null));
            markSheetList.add(new MarkSheetEnrolmentEvaluationBean(enrolment, executionSemester,
                    EnrolmentEvaluationType.IMPROVEMENT, null));

            for (String[] element : TYPE_AND_PHASE) {
                EnrolmentEvaluationType type = EnrolmentEvaluationType.valueOf(element[0]);
                CurriculumValidationEvaluationPhase phase =
                        element[1] != null ? CurriculumValidationEvaluationPhase.valueOf(element[1]) : null;
                EnrolmentEvaluation evaluation = enrolment.getLatestEnrolmentEvaluationByTypeAndPhase(type, phase);
                if (evaluation != null) {
                    logger.debug(String.format("%s[%s][%s] State: %s", evaluation.getEnrolment().getName().getContent(), type,
                            phase, evaluation.getEnrolmentEvaluationState().toString()));
                }
            }

            if (enrolment.hasAnyNonTemporaryEvaluations() && !forEdition) {
                finalEnrolmentEvaluationBeanList.add(markSheetList);
            } else {
                enrolmentEvaluationBeanList.add(markSheetList);
            }
        }
    }

    private BolonhaStudentEnrollmentBean getBolonhaStudentEnrollmentBeanFromViewState() {
        return getRenderedObject("bolonhaStudentEnrolments");
    }

    protected void addDebtsWarningMessages(final Student student, final ExecutionSemester executionSemester,
            final HttpServletRequest request) {
        if (student.isAnyGratuityOrAdministrativeOfficeFeeAndInsuranceInDebt()) {
            addActionMessage("warning", request, "label.student.events.in.debt.warning");
        }
    }

    private StudentCurricularPlan readStudentCurricularPlan(HttpServletRequest request) {
        if (request.getAttribute("studentCurricularPlan") != null) {
            return (StudentCurricularPlan) request.getAttribute("studentCurricularPlan");
        }

        StudentCurricularPlan studentCurricularPlan = getDomainObject(request, "studentCurricularPlanId");
        request.setAttribute("studentCurricularPlan", studentCurricularPlan);
        return studentCurricularPlan;

    }

    private ExecutionSemester readExecutionSemester(HttpServletRequest request) {
        ExecutionSemester executionSemester = (ExecutionSemester) request.getAttribute("executionSemester");

        if (executionSemester == null) {
            executionSemester = getDomainObject(request, "executionSemesterId");
        }

        if (executionSemester == null) {
            BolonhaStudentEnrollmentBean bean = readStudentEnrolmentBean(request);
            if (bean != null) {
                executionSemester = bean.getExecutionPeriod();
            }
        }

        if (executionSemester == null) {
            List<ExecutionSemester> possibleExecutionPeriods = readPossibleExecutionSemestersForStudentCurricularPlan(request);
            executionSemester = possibleExecutionPeriods.get(possibleExecutionPeriods.size() - 1);
        }

        request.setAttribute("executionSemester", executionSemester);

        return executionSemester;
    }

    private BolonhaStudentEnrollmentBean readBolonhaStudentEnrollmentBean(HttpServletRequest request,
            StudentCurricularPlan studentCurricularPlan, ExecutionSemester executionSemester, final int[] curricularYears,
            CurricularRuleLevel enrolmentNoRules, boolean create) {
        BolonhaStudentEnrollmentBean studentEnrolmentBean =
                (BolonhaStudentEnrollmentBean) request.getAttribute("bolonhaStudentEnrollmentBean");

        if (studentEnrolmentBean == null) {
            studentEnrolmentBean = (BolonhaStudentEnrollmentBean) getObjectFromViewState("student.enrolment.bean");
        }

        if (studentEnrolmentBean == null || create) {
            studentEnrolmentBean =
                    new BolonhaStudentEnrollmentBean(studentCurricularPlan, executionSemester, curricularYears,
                            CurricularRuleLevel.ENROLMENT_NO_RULES);
        }

        request.setAttribute("bolonhaStudentEnrollmentBean", studentEnrolmentBean);
        return studentEnrolmentBean;
    }

    private BolonhaStudentEnrollmentBean readStudentEnrolmentBean(HttpServletRequest request) {
        BolonhaStudentEnrollmentBean studentEnrolmentBean =
                (BolonhaStudentEnrollmentBean) request.getAttribute("bolonhaStudentEnrollmentBean");

        if (studentEnrolmentBean == null) {
            studentEnrolmentBean = (BolonhaStudentEnrollmentBean) getObjectFromViewState("student.enrolment.bean");
        }

        return studentEnrolmentBean;
    }

    private List<ExecutionSemester> readPossibleExecutionSemestersForStudentCurricularPlan(HttpServletRequest request) {
        StudentCurricularPlan studentCurricularPlan = readStudentCurricularPlan(request);
        final List<ExecutionSemester> executionPeriodsInTimePeriod =
                ExecutionSemester.readExecutionPeriodsInTimePeriod(studentCurricularPlan.getStartDate(),
                        getCurrentExecutionSemesterEndDate());

        Collections.sort(executionPeriodsInTimePeriod, new ReverseComparator(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR));
        return executionPeriodsInTimePeriod;
    }

    private Date getCurrentExecutionSemesterEndDate() {
        return ExecutionYear.readCurrentExecutionYear().getLastExecutionPeriod().getEndDate();
    }
}
