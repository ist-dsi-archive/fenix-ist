package pt.ist.learning.unit;

import java.util.Optional;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.DomainObject;

public class UnitSite extends UnitSite_Base {

    public UnitSite(Unit unit) {
        super();
        setUnit(unit);
        setBennu(Bennu.getInstance());
    }

    @Override
    public LocalizedString getName() {
        return Optional.ofNullable(super.getName()).orElse(getUnit().getNameI18n().toLocalizedString());
    }

    @Override
    public LocalizedString getDescription() {
        return Optional.ofNullable(super.getDescription()).orElse(getName());
    }

    @Override
    public DomainObject getObject() {
        return getUnit();
    }

    @Override
    public void delete() {
        this.setUnit(null);
        this.setBennu(null);
        super.delete();
    }
}
