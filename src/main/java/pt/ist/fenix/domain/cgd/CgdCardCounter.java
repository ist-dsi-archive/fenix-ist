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
package pt.ist.fenix.domain.cgd;

import java.time.Year;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;

public class CgdCardCounter extends CgdCardCounter_Base {

    private CgdCardCounter(final int year) {
        setBennu(Bennu.getInstance());
        setYear(year);
        setCount(0);
    }

    @Atomic
    public static String getNextSerialNumber(final User user) {
        final int year = Year.now().getValue();
        final CgdCardCounter counter = getCounterForYear(year);
        return counter.nextSerialNumber(user);
    }

    private String nextSerialNumber(final User user) {
        return user.getCgdCardSet().stream().filter(c -> c.getCgdCardCounter() == this).findAny()
                .orElse(createNewSerialNumber(user)).getSerialNumberForCard();
    }

    private CgdCard createNewSerialNumber(User user) {
        final int count = getCount() + 1;
        setCount(count);
        return new CgdCard(this, user, count);
    }

    private static CgdCardCounter getCounterForYear(final int year) {
        for (final CgdCardCounter counter : Bennu.getInstance().getCgdCardCounterSet()) {
            if (counter.getYear() == year) {
                return counter;
            }
        }
        return new CgdCardCounter(year);
    }

}
