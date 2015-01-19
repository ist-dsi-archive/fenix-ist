/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
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
