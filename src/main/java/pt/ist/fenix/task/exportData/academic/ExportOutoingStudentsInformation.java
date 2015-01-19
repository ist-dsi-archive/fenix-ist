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
package pt.ist.fenix.task.exportData.academic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

@Task(englishTitle = "ExportOutoingStudentsInformation", readOnly = true)
public class ExportOutoingStudentsInformation extends CustomTask {

    @Override
    public void runTask() throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet("Estudantes Mobilidade");
        spreadsheet
                .setHeaders("Nº aluno, Curso, Ano lectivo mobilidade, País de mobilidade, Instituição de mobilidade, Nº créditos"
                        .split(","));
        for (final Student student : Bennu.getInstance().getStudentsSet()) {
            for (final Registration registration : student.getRegistrationsSet()) {
                if (registration.isBolonha()) {
                    for (final StudentCurricularPlan scp : registration.getStudentCurricularPlansSet()) {
                        Map<ExecutionSemester, MobilityCreditsEnrolments> mobilityCreditsMap =
                                new HashMap<ExecutionSemester, MobilityCreditsEnrolments>();
                        for (final Credits credit : scp.getCreditsSet()) {
                            if (credit.isAllEnrolmentsAreExternal()) {
                                IEnrolment iEnrolment = credit.getIEnrolments().iterator().next();
                                if (iEnrolment.getAcademicUnit() == null
                                        || (iEnrolment.getAcademicUnit() != null
                                                && iEnrolment.getAcademicUnit().getCountry() != null && !iEnrolment
                                                .getAcademicUnit().getCountry().getCode().equalsIgnoreCase("pt"))) {
                                    ExecutionSemester executionSemester = credit.getExecutionPeriod();
                                    MobilityCreditsEnrolments mobilityCreditsEnrolments =
                                            mobilityCreditsMap.get(executionSemester);
                                    if (mobilityCreditsEnrolments == null) {
                                        mobilityCreditsEnrolments = new MobilityCreditsEnrolments();
                                        mobilityCreditsMap.put(executionSemester, mobilityCreditsEnrolments);
                                    }
                                    mobilityCreditsEnrolments.addCredits(credit.getGivenCredits());
                                    mobilityCreditsEnrolments.setIEnrolment(iEnrolment);
                                }
                            }
                        }
                        writeIEnrolments(scp, mobilityCreditsMap, spreadsheet);
                    }
                }
            }
        }
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        spreadsheet.exportToXLSSheet(byteArrayOS);
        output("Estudantes_Mobilidade.xls", byteArrayOS.toByteArray());
    }

    private void writeIEnrolments(final StudentCurricularPlan scp,
            Map<ExecutionSemester, MobilityCreditsEnrolments> mobilityCreditsMap, final Spreadsheet spreadsheet) {
        for (ExecutionSemester executionSemester : mobilityCreditsMap.keySet()) {
            Row row = spreadsheet.addRow();
            row.setCell(scp.getRegistration().getNumber());
            row.setCell(scp.getDegree().getNameI18N().getContent());
            row.setCell(executionSemester.getExecutionYear().getName());
            MobilityCreditsEnrolments mobilityCreditsEnrolments = mobilityCreditsMap.get(executionSemester);
            Unit academicUnit = mobilityCreditsEnrolments.getiEnrolment().getAcademicUnit();
            if (academicUnit != null) {
                row.setCell(academicUnit.getCountry().getName());
                row.setCell(academicUnit.getName());
            } else {
                row.setCell("");
                row.setCell(mobilityCreditsEnrolments.getiEnrolment().getDescription());
            }
            row.setCell(mobilityCreditsEnrolments.getCredits());
        }
    }

    private class MobilityCreditsEnrolments implements Serializable {

        private static final long serialVersionUID = 1L;

        private double credits;
        private IEnrolment iEnrolment;

        public MobilityCreditsEnrolments() {
            credits = 0;
        }

        public double getCredits() {
            return credits;
        }

        public void addCredits(double credits) {
            this.credits += credits;
        }

        public void setIEnrolment(IEnrolment iEnrolment) {
            this.iEnrolment = iEnrolment;
        }

        public IEnrolment getiEnrolment() {
            return iEnrolment;
        }
    }
}
