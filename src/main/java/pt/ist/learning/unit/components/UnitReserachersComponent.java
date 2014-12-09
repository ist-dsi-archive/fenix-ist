package pt.ist.learning.unit.components;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.I18N;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentType(name = "unitResearchers", description = "Researchers for a Unit")
public class UnitReserachersComponent extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        globalContext.put("researchers", researchers(unit(page)));
        globalContext.put("researcher", researchers(unit(page)).map(Person::getUsername).collect(Collectors.joining(",")));
        globalContext.put("sotisUrl", "https://sotis.tecnico.ulisboa.pt"); //FIXME get real configuration property when available
        globalContext.put("language", I18N.getLocale().toLanguageTag());
        globalContext.put("dataKey", "list");
    }

    Stream<Person> researchers(Unit unit) {
        return unit.getAllSubUnits().stream().flatMap(subunit -> (Stream<Person>) subunit.getChildParties(Person.class).stream());
    }
}
