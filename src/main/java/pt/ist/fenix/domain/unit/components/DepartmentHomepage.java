package pt.ist.fenix.domain.unit.components;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.rendering.TemplateContext;

public class DepartmentHomepage extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        globalContext.put("department", unit(page).getDepartment());
    }
}
