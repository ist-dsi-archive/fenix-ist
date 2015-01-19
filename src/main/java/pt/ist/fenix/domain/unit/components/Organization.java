package pt.ist.fenix.domain.unit.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum.MANAGEMENT_FUNCTION;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.YearMonthDay;

import pt.ist.fenixedu.contracts.domain.organizationalStructure.Function;
import pt.ist.fenixedu.contracts.domain.organizationalStructure.PersonFunction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ComponentType(name = "Unit Organization", description = "Provides the organizational structure for this unit")
public class Organization extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        globalContext.put("unitBean", new UnitFunctionsBean(unit(page)));
    }

    public static class UnitFunctionsBean {

        private final Unit unit;

        public UnitFunctionsBean(Unit unit) {
            this.unit = unit;
        }

        public SortedMap<Function, SortedSet<PersonFunction>> getPersonFunctionsByFunction() {
            return getPersonFunctionsByFunction(getUnit());
        }

        public Stream<UnitFunctionsBean> getSubunitBeans() {
            Predicate<Unit> hasPersons = subunit -> !getPersonFunctionsByFunction(subunit).isEmpty();
            return getUnit().getActiveSubUnits(currentDay()).stream().filter(hasPersons)
                    .sorted(Unit.COMPARATOR_BY_SUBPARTY_AND_NAME_AND_ID).map(UnitFunctionsBean::new);
        }

        private SortedMap<Function, SortedSet<PersonFunction>> getPersonFunctionsByFunction(Unit unit) {
            return Function.getOrderedActiveFunctions(unit).stream()
                    .filter(function -> MANAGEMENT_FUNCTION.equals(function.getType()))
                    .flatMap(function -> PersonFunction.getActivePersonFunctions(function).stream())
                    .collect(groupingBy(PersonFunction::getFunction, functionsFactory, toCollection(personFunctionFactory)));
        }

        public Unit getUnit() {
            return unit;
        }

        public YearMonthDay currentDay() {
            return new YearMonthDay();
        }

        private static Supplier<TreeMap<Function, SortedSet<PersonFunction>>> functionsFactory = () -> Maps
                .newTreeMap(Function.COMPARATOR_BY_ORDER);

        private static Supplier<SortedSet<PersonFunction>> personFunctionFactory = () -> Sets
                .newTreeSet(PersonFunction.COMPARATOR_BY_PERSON_NAME);
    }
}
