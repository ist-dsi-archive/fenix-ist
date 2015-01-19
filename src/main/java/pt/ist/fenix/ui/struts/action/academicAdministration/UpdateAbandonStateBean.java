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
package pt.ist.fenix.ui.struts.action.academicAdministration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOfficeType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule.CurriculumModulePredicateByType;
import org.fenixedu.academic.domain.util.email.Message;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.domain.util.email.SystemSender;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.joda.time.YearMonthDay;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.Atomic;
import pt.utl.ist.fenix.tools.predicates.AndPredicate;
import pt.utl.ist.fenix.tools.predicates.Predicate;

public class UpdateAbandonStateBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String RESOURCE_BUNDLE = "FENIX_IST_RESOURCES";

    private ExecutionSemester whenToAbandon;
    private StringBuilder log;

    public UpdateAbandonStateBean() {
    }

    public void updateStates() {
        int totalByDegree = 0;
        int total = 0;
        setLog(new StringBuilder());
        for (final DegreeCurricularPlan degreeCurricularPlan : DegreeCurricularPlan.readBolonhaDegreeCurricularPlans()) {
            if (degreeCurricularPlan.getDegreeType().getAdministrativeOfficeType() != AdministrativeOfficeType.DEGREE) {
                continue;
            }
            if (degreeCurricularPlan.hasExecutionDegreeFor(getWhenToAbandon().getExecutionYear())
                    || degreeCurricularPlan.hasExecutionDegreeFor(getWhenToAbandon().getExecutionYear()
                            .getPreviousExecutionYear())) {
                totalByDegree = 0;

                for (final StudentCurricularPlan studentCurricularPlan : degreeCurricularPlan.getActiveStudentCurricularPlans()) {
                    try {
                        if (processStudent(studentCurricularPlan)) {
                            totalByDegree++;
                        }
                    } catch (Exception e) {
                        getLog().append(
                                RenderUtils.getFormatedResourceString(RESOURCE_BUNDLE,
                                        "label.academicAdministration.setAbandonState.report.error")).append("\t")
                                .append(studentCurricularPlan.getRegistration().getStudent().getNumber()).append("\t")
                                .append(e.getMessage()).append("\n");
                    }
                }
                getLog().append(
                        RenderUtils.getFormatedResourceString(RESOURCE_BUNDLE,
                                "label.academicAdministration.setAbandonState.report.degreeTotal")).append(" ")
                        .append(degreeCurricularPlan.getName()).append("\t").append(totalByDegree).append("\n\n");
                total += totalByDegree;
            }
        }
        getLog().append("\n")
                .append(RenderUtils.getFormatedResourceString(RESOURCE_BUNDLE,
                        "label.academicAdministration.setAbandonState.report.totalStudents")).append("\t").append(total);
    }

    @Atomic
    private boolean processStudent(StudentCurricularPlan studentCurricularPlan) {
        final ExecutionSemester startChecking = getWhenToAbandon().getPreviousExecutionPeriod();
        final Registration registration = studentCurricularPlan.getRegistration();
        RegistrationState lastRegistrationState =
                registration.getLastRegistrationState(getWhenToAbandon().getExecutionYear().getPreviousExecutionYear());

        if (hasValidRegistrationAgreement(registration) && registration.isDegreeAdministrativeOffice()
                && lastRegistrationState != null && lastRegistrationState.isActive()
                && !lastRegistrationState.getStateType().equals(RegistrationStateType.MOBILITY) && !registration.hasConcluded()) {

            if (registration.getStartExecutionYear().isBefore(getWhenToAbandon().getExecutionYear())
                    && !hasAnyEnrolmentInPeriodOrPrevious(registration, startChecking)) {

                if (registration.hasStateType(getWhenToAbandon().getPreviousExecutionPeriod(),
                        RegistrationStateType.EXTERNAL_ABANDON)
                        || registration.hasStateType(getWhenToAbandon(), RegistrationStateType.EXTERNAL_ABANDON)) {
                    return false;
                }
                final YearMonthDay now = new YearMonthDay();
                final RegistrationState state =
                        RegistrationState.createRegistrationState(registration, null, (now.isBefore(getWhenToAbandon()
                                .getBeginDateYearMonthDay()) ? getWhenToAbandon().getBeginDateYearMonthDay() : now)
                                .toDateTimeAtMidnight(), RegistrationStateType.EXTERNAL_ABANDON);

                state.setRemarks(RenderUtils.getFormatedResourceString(RESOURCE_BUNDLE,
                        "message.academicAdministration.abandonState.observations"));
                sendEmail(registration);
                getLog().append(registration.getStudent().getNumber()).append("\t")
                        .append(registration.getLastDegreeCurricularPlan().getName()).append("\n");
                return true;
            }
        }
        return false;
    }

    private boolean hasValidRegistrationAgreement(final Registration registration) {
        return registration.getRegistrationProtocol() == null
                || (!registration.getRegistrationProtocol().isMilitaryAgreement() && !registration.getRegistrationProtocol()
                        .isMobilityAgreement());
    }

    private boolean hasAnyEnrolmentInPeriodOrPrevious(final Registration registration, final ExecutionSemester executionSemester) {
        return evaluateAndCheckEnrolments(registration, executionSemester);
    }

    private boolean evaluateAndCheckEnrolments(final Registration registration, final ExecutionSemester semester) {
        if (registration.getStartExecutionYear().isBeforeOrEquals(semester.getExecutionYear())) {
            if (hasAnyCurriculumLines(registration, semester)) {
                return true;
            }
            if (registration.getStartExecutionYear().isBeforeOrEquals(semester.getPreviousExecutionPeriod().getExecutionYear())) {
                return hasAnyCurriculumLines(registration, semester.getPreviousExecutionPeriod());
            }
        }
        return true;
    }

    private boolean hasAnyCurriculumLines(final Registration registration, final ExecutionSemester semester) {
        final AndPredicate<CurriculumModule> andPredicate = new AndPredicate<CurriculumModule>();
        andPredicate.add(new CurriculumModulePredicateByType(CurriculumLine.class));
        andPredicate.add(new CurriculumLinePredicateByExecutionSemester(semester));

        for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            if (studentCurricularPlan.hasAnyCurriculumModules(andPredicate)) {
                return true;
            }
        }
        return false;
    }

    private class CurriculumLinePredicateByExecutionSemester extends Predicate<CurriculumModule> {

        private final ExecutionSemester semester;

        private CurriculumLinePredicateByExecutionSemester(final ExecutionSemester semester) {
            this.semester = semester;
        }

        @Override
        public boolean eval(final CurriculumModule module) {
            if (!module.isCurriculumLine()) {
                return false;
            }
            if (module.isEnrolment()) {
                return ((Enrolment) module).isValid(semester);
            }
            return ((CurriculumLine) module).getExecutionPeriod().equals(semester);
        }
    }

    private void sendEmail(final Registration registration) {
        final Person person = registration.getPerson();

        SystemSender systemSender = Bennu.getInstance().getSystemSender();
        List<Recipient> recipientList = new ArrayList<Recipient>();
        recipientList.add(new Recipient(UserGroup.of(person.getUser())));

        String body = buildMessage(registration);
        String subject =
                RenderUtils
                        .getFormatedResourceString(RESOURCE_BUNDLE, "message.academicAdministration.abandonState.mail.subject");
        new Message(systemSender, systemSender.getConcreteReplyTos(), recipientList, null, null, subject, body,
                new HashSet<String>());
    }

    private String buildMessage(final Registration registration) {
        return RenderUtils.getFormatedResourceString(RESOURCE_BUNDLE, "message.academicAdministration.abandonState.mail.body",
                registration.getLastStudentCurricularPlan().getName(), getWhenToAbandon().getQualifiedName(), getWhenToAbandon()
                        .getPreviousExecutionPeriod().getPreviousExecutionPeriod().getQualifiedName(), getWhenToAbandon()
                        .getPreviousExecutionPeriod().getQualifiedName());
    }

    public ExecutionSemester getWhenToAbandon() {
        return whenToAbandon;
    }

    public void setWhenToAbandon(ExecutionSemester whenToAbandon) {
        this.whenToAbandon = whenToAbandon;
    }

    public StringBuilder getLog() {
        return log;
    }

    public void setLog(StringBuilder log) {
        this.log = log;
    }
}
