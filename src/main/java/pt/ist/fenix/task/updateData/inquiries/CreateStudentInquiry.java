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
import org.fenixedu.academic.domain.inquiries.CurricularCourseInquiryTemplate;
import org.fenixedu.academic.domain.inquiries.InquiryBlock;
import org.fenixedu.academic.domain.inquiries.StudentTeacherInquiryTemplate;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.joda.time.DateTime;

public class CreateStudentInquiry extends CustomTask {

    @Override
    public void runTask() throws Exception {
        ExecutionSemester currentExecutionSemester = ExecutionSemester.readActualExecutionSemester();
        DateTime begin = new DateTime(2012, 5, 16, 0, 0, 0, 0);
        DateTime end = new DateTime(2012, 5, 20, 0, 0, 0, 0);

        // Curricular inquiry
        CurricularCourseInquiryTemplate newCourseInquiryTemplate = new CurricularCourseInquiryTemplate(begin, end);
        newCourseInquiryTemplate.setExecutionPeriod(currentExecutionSemester);

        CurricularCourseInquiryTemplate previousCourseInquiryTemplate =
                CurricularCourseInquiryTemplate.getTemplateByExecutionPeriod(currentExecutionSemester
                        .getPreviousExecutionPeriod());
        for (InquiryBlock inquiryBlock : previousCourseInquiryTemplate.getInquiryBlocksSet()) {
            newCourseInquiryTemplate.addInquiryBlocks(inquiryBlock);
        }

        // Teachers inquiry      
        StudentTeacherInquiryTemplate newStudentTeacherInquiryTemplate = new StudentTeacherInquiryTemplate(begin, end);
        newStudentTeacherInquiryTemplate.setExecutionPeriod(currentExecutionSemester);

        StudentTeacherInquiryTemplate previousStudentTeacherInquiryTemplate =
                StudentTeacherInquiryTemplate.getTemplateByExecutionPeriod(currentExecutionSemester.getPreviousExecutionPeriod());
        for (InquiryBlock inquiryBlock : previousStudentTeacherInquiryTemplate.getInquiryBlocksSet()) {
            newStudentTeacherInquiryTemplate.addInquiryBlocks(inquiryBlock);
        }
    }
}
