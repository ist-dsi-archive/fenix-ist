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
package pt.ist.fenix.task;

import java.util.Collections;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitCostCenterCode;
import org.fenixedu.academic.domain.util.email.Message;
import org.fenixedu.academic.domain.util.email.SystemSender;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.joda.time.YearMonthDay;

import pt.ist.fenixedu.contracts.domain.Employee;

@Task(englishTitle = "BirthDaysCIIST")
public class BirthDaysCIIST extends CronTask {

    private static final Integer howManyDaysBeforeToWarn = 1;
    private static final Integer[] listOfEmployeesNumberToWarn = { 4506, 4439 };

    @Override
    public void runTask() {
        Unit ciist = UnitCostCenterCode.find(8400).getUnit();
        YearMonthDay now = new YearMonthDay();
        for (Employee employee : Employee.getAllCurrentActiveWorkingEmployees(ciist)) {
            YearMonthDay birthDay = employee.getPerson().getDateOfBirthYearMonthDay();
            if (birthDay != null) {
                if ((now.plusDays(howManyDaysBeforeToWarn).getDayOfMonth() == birthDay.getDayOfMonth())
                        && (now.plusDays(howManyDaysBeforeToWarn).getMonthOfYear() == birthDay.getMonthOfYear())) {
                    warnAboutBirthDay(employee.getPerson(), birthDay);
                }
            }
        }
    }

    private void warnAboutBirthDay(Person personNearBirthDay, YearMonthDay birthDay) {
        for (Integer employeeToWarnNumber : listOfEmployeesNumberToWarn) {
            Employee employeeToWarn = Employee.readByNumber(employeeToWarnNumber);
            if (employeeToWarn != null) {
                Person personToWarn = employeeToWarn.getPerson();
                if (personToWarn != null) {
                    sendMessage(personToWarn.getDefaultEmailAddressValue(), "Aviso de Aniversario CIIST",
                            personNearBirthDay.getFirstAndLastName() + " faz anos dia " + birthDay.toString() + ".");
                }
            }
        }
    }

    private static void sendMessage(String email, String subject, String body) {
        SystemSender systemSender = Bennu.getInstance().getSystemSender();
        if (email != null) {
            new Message(systemSender, null, Collections.EMPTY_LIST, subject, body, email);
        }
    }
}
