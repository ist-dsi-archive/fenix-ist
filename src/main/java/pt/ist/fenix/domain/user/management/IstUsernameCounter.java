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
package pt.ist.fenix.domain.user.management;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class IstUsernameCounter extends IstUsernameCounter_Base {

    public IstUsernameCounter() {
        super();
        setBennu(Bennu.getInstance());
        setLastValue(4_00_000);
    }

    public long getNext() {
        long next = getLastValue() + 1;
        setLastValue(next);
        return next;
    }

    @Atomic
    public static IstUsernameCounter ensureSingleton() {
        if (Bennu.getInstance().getIstUsernameCounter() == null) {
            new IstUsernameCounter();
        }
        return Bennu.getInstance().getIstUsernameCounter();
    }

}
