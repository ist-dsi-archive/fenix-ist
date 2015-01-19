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
package pt.ist.fenix.dto;

import java.io.Serializable;

import org.fenixedu.academic.domain.ExecutionYear;

public class ExecutionYearIntervalBean extends ExecutionYearBean implements Serializable {

    private ExecutionYear finalExecutionYear;

    public ExecutionYearIntervalBean() {
        setFirstExecutionYear(ExecutionYear.readFirstExecutionYear());
        setFinalExecutionYear(ExecutionYear.readLastExecutionYear());
    }

    public ExecutionYearIntervalBean(ExecutionYear firstExecutionYear, ExecutionYear finalExecutionYear) {
        setFirstExecutionYear(firstExecutionYear);
        setFinalExecutionYear(finalExecutionYear);
    }

    public void setFirstExecutionYear(ExecutionYear executionYear) {
        this.setExecutionYear(executionYear);
    }

    public ExecutionYear getFirstExecutionYear() {
        return this.getExecutionYear();
    }

    public void setFinalExecutionYear(ExecutionYear executionYear) {
        this.finalExecutionYear = executionYear;
    }

    public ExecutionYear getFinalExecutionYear() {
        return this.finalExecutionYear;
    }

    public ExecutionYear getFirstNonNullExecutionYear() {
        return getFirstExecutionYear() != null ? getFirstExecutionYear() : ExecutionYear.readFirstExecutionYear();
    }

    public ExecutionYear getFinalNonNullExecutionYear() {
        return getFinalExecutionYear() != null ? getFinalExecutionYear() : ExecutionYear.readLastExecutionYear();
    }
}
