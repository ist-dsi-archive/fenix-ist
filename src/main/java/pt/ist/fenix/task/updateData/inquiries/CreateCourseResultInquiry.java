package pt.ist.fenix.task.updateData.inquiries;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.inquiries.InquiryBlock;
import org.fenixedu.academic.domain.inquiries.ResultsInquiryTemplate;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CreateCourseResultInquiry extends CustomTask {

    @Override
    public void runTask() throws Exception {
        ExecutionSemester currentExecutionSemester = ExecutionSemester.readActualExecutionSemester();

        ResultsInquiryTemplate newResultsInquiryTemplate = new ResultsInquiryTemplate();
        newResultsInquiryTemplate.setExecutionPeriod(currentExecutionSemester);

        ResultsInquiryTemplate previousResultsInquiryTemplate =
                ResultsInquiryTemplate.getTemplateByExecutionPeriod(currentExecutionSemester.getPreviousExecutionPeriod());
        for (InquiryBlock inquiryBlock : previousResultsInquiryTemplate.getInquiryBlocksSet()) {
            newResultsInquiryTemplate.addInquiryBlocks(inquiryBlock);
        }
    }
}
