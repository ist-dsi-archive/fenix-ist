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
