package pt.ist.fenix.dto;

import java.io.Serializable;

import org.fenixedu.academic.domain.ExecutionYear;

public class ExecutionYearBean implements Serializable {

    private ExecutionYear executionYear;

    public ExecutionYearBean() {
        setExecutionYear(null);
    }

    public ExecutionYearBean(ExecutionYear executionYear) {
        setExecutionYear(executionYear);
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public ExecutionYear getExecutionYear() {
        return this.executionYear;
    }
}
