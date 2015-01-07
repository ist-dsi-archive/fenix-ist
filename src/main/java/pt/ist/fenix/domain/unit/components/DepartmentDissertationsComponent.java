package pt.ist.fenix.domain.unit.components;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import static org.fenixedu.academic.domain.Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID;
import static org.fenixedu.academic.domain.ExecutionYear.COMPARATOR_BY_YEAR;
import static org.fenixedu.learning.domain.DissertationsUtils.allThesesByYear;
import static org.fenixedu.learning.domain.DissertationsUtils.getThesisStateMapping;

@ComponentType(name = "departmentDissertations", description = "Dissertations information for a Department")
public class DepartmentDissertationsComponent extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        if (unit(page) instanceof DepartmentUnit) {
            Collection<Degree> degrees = unit(page).getDepartment().getDegreesSet();
            SortedMap<ExecutionYear, List<Thesis>> allThesesByYear = allThesesByYear(degrees);

            globalContext.put("unit", unit(page).getDepartment().getDepartmentUnit());
            globalContext.put("thesesByYear", allThesesByYear);
            globalContext.put("years", allThesesByYear.keySet().stream().sorted(COMPARATOR_BY_YEAR));
            globalContext.put("degrees", degrees.stream().sorted(COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID));
            globalContext.put("states", getThesisStateMapping());
        }
    }

}
