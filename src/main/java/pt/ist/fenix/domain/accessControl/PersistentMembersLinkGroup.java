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
package pt.ist.fenix.domain.accessControl;

import java.util.Optional;

import org.fenixedu.bennu.core.groups.Group;

public class PersistentMembersLinkGroup extends PersistentMembersLinkGroup_Base {
    protected PersistentMembersLinkGroup(PersistentGroupMembers persistentGroupMembers) {
        super();
        setPersistentGroupMembers(persistentGroupMembers);
    }

    @Override
    public Group toGroup() {
        return MembersLinkGroup.get(getPersistentGroupMembers());
    }

    @Override
    protected void gc() {
        setPersistentGroupMembers(null);
        super.gc();
    }

    public static PersistentMembersLinkGroup getInstance(PersistentGroupMembers persistentGroupMembers) {
        return singleton(() -> Optional.ofNullable(persistentGroupMembers.getMembersLinkGroup()),
                () -> new PersistentMembersLinkGroup(persistentGroupMembers));
    }
}
