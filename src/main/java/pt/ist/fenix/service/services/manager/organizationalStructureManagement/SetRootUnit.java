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
package pt.ist.fenix.service.services.manager.organizationalStructureManagement;

import static org.fenixedu.academic.predicate.AccessControl.check;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.predicate.RolePredicates;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class SetRootUnit {

    @Atomic
    public static void run(final Unit unit, final Boolean institutionUnit) {
        check(RolePredicates.MANAGER_PREDICATE);

        if (unit.isPlanetUnit()) {
            Bennu.getInstance().setEarthUnit(unit);

        } else if (institutionUnit) {
            Bennu.getInstance().setInstitutionUnit(unit);

        } else if (!institutionUnit) {
            Bennu.getInstance().setExternalInstitutionUnit(unit);
        }
    }
}