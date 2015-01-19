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

import org.fenixedu.bennu.core.domain.User;

public class CgdCard extends CgdCard_Base {

    public CgdCard(final CgdCardCounter counter, final User user, final int count) {
        setCgdCardCounter(counter);
        setUser(user);
        setSerialNumber(count);
    }

    public String getSerialNumberForCard() {
        return Integer.toString(getCgdCardCounter().getYear() % 100)
                + fillLeftString(Integer.toString(getSerialNumber()), '0', 6);
    }

    protected static String fillLeftString(final String string, final char c, final int fillTo) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = string.length(); i < fillTo; i++) {
            stringBuilder.append(c);
        }
        stringBuilder.append(string);
        return stringBuilder.toString();
    }

}
