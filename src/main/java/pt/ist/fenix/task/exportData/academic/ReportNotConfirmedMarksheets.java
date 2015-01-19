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

import org.fenixedu.academic.domain.MarkSheet;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

public class ReportNotConfirmedMarksheets extends CustomTask {

    @Override
    public void runTask() throws Exception {
        final Spreadsheet spreadsheet = new Spreadsheet("PautasPorConfirmar");
        spreadsheet.setHeader("Plano Curricular\t");
        spreadsheet.setHeader("Unidade Curricular\t");
        spreadsheet.setHeader("Número Responsável\t");
        spreadsheet.setHeader("Responsável\n");

        for (final MarkSheet markSheet : Bennu.getInstance().getMarkSheetsSet()) {
            if (markSheet.isNotConfirmed()) {
                final Row row = spreadsheet.addRow();
                row.setCell(markSheet.getCurricularCourse().getDegreeCurricularPlan().getName());
                row.setCell(markSheet.getCurricularCourse().getName());
                row.setCell(markSheet.getResponsibleTeacher().getPerson().getEmployee().getEmployeeNumber());
                row.setCell(markSheet.getResponsibleTeacher().getPerson().getName());
            }
        }
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        spreadsheet.exportToXLSSheet(byteArrayOS);

        output("pautas_por_confirmar.xls", byteArrayOS.toByteArray());
    }
}
