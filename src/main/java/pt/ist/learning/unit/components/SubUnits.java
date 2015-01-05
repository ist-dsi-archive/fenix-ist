package pt.ist.learning.unit.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "Subunits", description = "Subunits of a research unit that have a site")
public class SubUnits extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        globalContext.put("subunits", subUnitsWithSite(unit(page)));
    }

    private List<Unit> subUnitsWithSite(Unit unit) {
        return unit.getSubUnits().stream().filter(u -> u.getSite() != null).sorted(Unit.COMPARATOR_BY_NAME_AND_ID)
                .collect(toList());
    }
}
