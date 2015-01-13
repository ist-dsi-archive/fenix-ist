package pt.ist.fenix.task.updateData.inquiries;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.inquiries.CoordinatorInquiryTemplate;
import org.fenixedu.academic.domain.inquiries.InquiryBlock;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.joda.time.DateTime;

public class CreateCoordinatorInquiry extends CustomTask {

    @Override
    public void runTask() throws Exception {
        ExecutionSemester currentExecutionSemester = ExecutionSemester.readActualExecutionSemester();
        DateTime begin = new DateTime(2012, 5, 16, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 5, 20, 0, 0, 0, 0);

        CoordinatorInquiryTemplate newCoordinatorInquiryTemplate = new CoordinatorInquiryTemplate(begin, end, true);
        newCoordinatorInquiryTemplate.setExecutionPeriod(currentExecutionSemester);

        CoordinatorInquiryTemplate previousCoordinatorInquiryTemplate =
                CoordinatorInquiryTemplate.getTemplateByExecutionPeriod(currentExecutionSemester.getPreviousExecutionPeriod());
        for (InquiryBlock inquiryBlock : previousCoordinatorInquiryTemplate.getInquiryBlocksSet()) {
            newCoordinatorInquiryTemplate.addInquiryBlocks(inquiryBlock);
        }
    }
}
