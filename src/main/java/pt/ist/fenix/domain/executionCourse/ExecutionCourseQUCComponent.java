package pt.ist.fenix.domain.executionCourse;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.inquiries.InquiryResult;
import org.fenixedu.academic.domain.inquiries.TeacherInquiryTemplate;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.learning.domain.executionCourse.components.BaseExecutionCourseComponent;
import org.joda.time.DateTime;

@ComponentType(description = "Shows QUC Results on Public Pages", name = "QUC Results")
public class ExecutionCourseQUCComponent extends BaseExecutionCourseComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();

        ExecutionSemester executionPeriod = executionCourse.getExecutionPeriod();
        ExecutionSemester oldQucExecutionSemester = ExecutionSemester.readBySemesterAndExecutionYear(2, "2009/2010");
        if (executionPeriod.isAfter(oldQucExecutionSemester)) {
            TeacherInquiryTemplate teacherInquiryTemplate = TeacherInquiryTemplate.getTemplateByExecutionPeriod(executionPeriod);
            if (teacherInquiryTemplate == null
                    || teacherInquiryTemplate.getResponsePeriodBegin().plusDays(7).isAfter(DateTime.now())
                    || executionCourse.getInquiryResultsSet().isEmpty()) {
                return;
            }

            Map<Professorship, Set<ShiftType>> professorships = new HashMap<Professorship, Set<ShiftType>>();
            for (Professorship professorship : executionCourse.getProfessorshipsSet()) {
                Collection<InquiryResult> professorshipResults = professorship.getInquiryResultsSet();
                if (!professorshipResults.isEmpty()) {
                    professorships.put(professorship, getShiftTypes(professorshipResults));
                }
            }

            globalContext.put("executionCourse", executionCourse);
            globalContext.put("professorships", professorships);
        }
    }

    private Set<ShiftType> getShiftTypes(Collection<InquiryResult> professorshipResults) {
        Set<ShiftType> shiftTypes = new HashSet<ShiftType>();
        for (InquiryResult inquiryResult : professorshipResults) {
            shiftTypes.add(inquiryResult.getShiftType());
        }
        return shiftTypes;
    }
}
