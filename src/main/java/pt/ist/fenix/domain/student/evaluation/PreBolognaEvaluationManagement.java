package pt.ist.fenix.domain.student.evaluation;

import java.lang.reflect.Method;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.log.EnrolmentEvaluationLog;

import pt.ist.fenixframework.Atomic;

public class PreBolognaEvaluationManagement {

    public static boolean getEvaluationForCurriculumValidationAllowed(StudentCurricularPlan studentCurricularPlan) {
        ExecutionSemester FENIX_START_DATE_SEMESTER = ExecutionSemester.readBySemesterAndExecutionYear(1, "2006/2007");
        return studentCurricularPlan.getStartExecutionPeriod().isBefore(FENIX_START_DATE_SEMESTER)
                && !studentCurricularPlan.isBolonhaDegree();
    }

    @Atomic
    public static void deleteEnrolmentEvaluationCurriculumValidationContext(EnrolmentEvaluation enrolmentEvaluation)
            throws Exception {
        if (!enrolmentEvaluation.getEnrolment().getStudentCurricularPlan().getEvaluationForCurriculumValidationAllowed()) {
            throw new DomainException("error.curriculum.validation.enrolment.evaluatiom.removal.not.allowed");
        }

        Enrolment enrolment = enrolmentEvaluation.getEnrolment();
        EnrolmentEvaluationLog.logEnrolmentEvaluationDeletion(enrolmentEvaluation);
        if (enrolmentEvaluation.getImprovementOfApprovedEnrolmentEvent() != null
                && enrolmentEvaluation.getImprovementOfApprovedEnrolmentEvent().isPayed()) {
            throw new DomainException("error.enrolmentEvaluation.has.been.payed");
        }

        enrolmentEvaluation.setPersonResponsibleForGrade(null);
        enrolmentEvaluation.setPerson(null);
        enrolmentEvaluation.setEnrolment(null);
        enrolmentEvaluation.setMarkSheet(null);
        enrolmentEvaluation.setRectification(null);
        enrolmentEvaluation.setRectified(null);
        if (enrolmentEvaluation.getImprovementOfApprovedEnrolmentEvent() != null) {
            enrolmentEvaluation.getImprovementOfApprovedEnrolmentEvent().removeImprovementEnrolmentEvaluations(
                    enrolmentEvaluation);
        }
        enrolmentEvaluation.setExecutionPeriod(null);
        enrolmentEvaluation.setRootDomainObject(null);

        //TODO refactor this hack to a general functionality that allows to change past data
        Method deletedMethod =
                enrolmentEvaluation.getClass().getSuperclass().getSuperclass().getDeclaredMethod("deleteDomainObject");
        deletedMethod.setAccessible(true);
        deletedMethod.invoke(enrolmentEvaluation);

        changeStateIfAprovedAndEvaluationsIsEmpty(enrolment);
    }

    private static void changeStateIfAprovedAndEvaluationsIsEmpty(Enrolment enrolment) {
        if (!enrolment.getStudentCurricularPlan().getEvaluationForCurriculumValidationAllowed()) {
            throw new DomainException("error.curriculum.validation.enrolment.evaluatiom.removal.not.allowed");
        }

        if (enrolment.getEnrollmentState().equals(EnrollmentState.APROVED) && enrolment.getEvaluationsSet().isEmpty()) {
            enrolment.setEnrollmentState(EnrollmentState.ENROLLED);
        }
    }

    @Atomic
    public static void markAsTemporaryEnrolled(Enrolment enrolment) {
        if (!enrolment.getStudentCurricularPlan().getEvaluationForCurriculumValidationAllowed()) {
            throw new DomainException("error.curriculum.validation.enrolment.evaluatiom.removal.not.allowed");
        }

        if (enrolment.getEvaluationsSet().isEmpty()) {
            enrolment.setEnrollmentState(EnrollmentState.ENROLLED);
        }
    }
}
