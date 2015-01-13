package pt.ist.fenix.domain.unit.components;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "Unit", description = "Provides the unit associated with the site")
public class UnitComponent extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        componentContext.put("unit", unit(page));
        globalContext.put("unit", unit(page));
    }

}
