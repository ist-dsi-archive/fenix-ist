package pt.ist.fenix.domain.unit.components;

import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenix.domain.unit.UnitSite;

public class DepartmentHomepage extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        globalContext.put("department", unit(page).getDepartment());
    }

    public static boolean supportsSite(Site site) {
        return site instanceof UnitSite && ((UnitSite) site).getUnit() instanceof DepartmentUnit;
    }
}
