package pt.ist.fenix.ui.struts.action.pedagogicalCouncil;

import java.io.Serializable;

import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.joda.time.YearMonthDay;

public class SendStudentEmailBean implements Serializable {
    private static final long serialVersionUID = -74024929237627862L;

    private DegreeType degreeType;

    private ExecutionYear executionYear;

    private Degree degree;

    private CurricularYear curricularYear;

    private YearMonthDay startDate;

    private YearMonthDay endDate;

    public SendStudentEmailBean() {
        setCurricularYear(null);
        setDegree(null);
    }

    public CurricularYear getCurricularYear() {
        return (curricularYear);
    }

    public void setCurricularYear(CurricularYear curricularYear) {
        this.curricularYear = curricularYear;
    }

    public Degree getDegree() {
        return (degree);
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    public YearMonthDay getEndDate() {
        return endDate;
    }

    public void setEndDate(YearMonthDay endDate) {
        this.endDate = endDate;
    }

    public YearMonthDay getStartDate() {
        return startDate;
    }

    public void setStartDate(YearMonthDay startDate) {
        this.startDate = startDate;
    }

    public ExecutionYear getExecutionYear() {
        return (executionYear);
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }
}