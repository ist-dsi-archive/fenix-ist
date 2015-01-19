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
package pt.ist.fenix.service.services.manager;

import java.util.List;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Unit;

import pt.ist.fenix.domain.accessControl.PersistentGroupMembers;
import pt.ist.fenix.domain.accessControl.PersistentGroupMembersType;
import pt.ist.fenixframework.Atomic;

public class CreatePersistentGroup {

    @Atomic
    public static void run(Unit unit, String name, List<Person> people, PersistentGroupMembersType type) {

        PersistentGroupMembers members = new PersistentGroupMembers(name, type);
        members.setUnit(unit);
        for (Person person : people) {
            members.addPersons(person);
        }
    }
}