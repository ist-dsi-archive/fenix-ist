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

import org.fenixedu.academic.domain.organizationalStructure.AccountabilityType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.predicate.RolePredicates;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class AssociateParentUnit {

    @Atomic
    public static void run(String unitID, String parentUnitID, AccountabilityType accountabilityType)
            throws FenixServiceException {
        check(RolePredicates.MANAGER_PREDICATE);

        Unit parentUnit = getParentUnit(parentUnitID);
        Unit unit = (Unit) FenixFramework.getDomainObject(unitID);

        if (unit == null) {
            throw new FenixServiceException("error.inexistent.unit");
        }
        unit.addParentUnit(parentUnit, accountabilityType);
    }

    private static Unit getParentUnit(String parentUnitID) {
        Unit parentUnit = null;
        if (parentUnitID != null) {
            parentUnit = (Unit) FenixFramework.getDomainObject(parentUnitID);
        }
        return parentUnit;
    }
}