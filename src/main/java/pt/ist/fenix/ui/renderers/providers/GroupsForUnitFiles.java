package pt.ist.fenix.ui.renderers.providers;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenix.domain.accessControl.MembersLinkGroup;
import pt.ist.fenix.domain.accessControl.PersistentGroupMembers;
import pt.ist.fenix.ui.struts.action.research.researchUnit.UnitFileBean;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class GroupsForUnitFiles implements DataProvider {

    @Override
    public Converter getConverter() {
        return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
        Set<Group> groups = new HashSet<>();
        Unit unit = ((UnitFileBean) source).getUnit();
        groups.addAll(unit.getGroups());
        for (final PersistentGroupMembers persistentMembers : unit.getPersistentGroupsSet()) {
            groups.add(MembersLinkGroup.get(persistentMembers));
        }
        groups.addAll(((UnitFileBean) source).getGroups());
        return groups;
    }
}
