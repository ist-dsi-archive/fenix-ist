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

import java.util.Set;

import org.fenixedu.academic.domain.accessControl.FenixGroup;
import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.cms.domain.Site;
import org.joda.time.DateTime;

import com.google.common.base.Objects;

@GroupOperator("siteManagers")
public class ManagersOfUnitSiteGroup extends FenixGroup {
    private static final long serialVersionUID = 1045771762725375319L;

    @GroupArgument
    private Site site;

    private ManagersOfUnitSiteGroup() {
        super();
    }

    private ManagersOfUnitSiteGroup(Site site) {
        this();
        this.site = site;
    }

    public static ManagersOfUnitSiteGroup get(Site site) {
        return new ManagersOfUnitSiteGroup(site);
    }

    @Override
    public String[] getPresentationNameKeyArgs() {
        return new String[] { site.getName().getContent() };
    }

    @Override
    public Set<User> getMembers() {
        return site.getCanAdminGroup().getMembers();
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return site.getCanAdminGroup().getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return site.getCanAdminGroup().isMember(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return site.getCanAdminGroup().isMember(user, when);
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentManagersOfUnitSiteGroup.getInstance(site);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ManagersOfUnitSiteGroup) {
            return Objects.equal(site, ((ManagersOfUnitSiteGroup) object).site);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(site);
    }

}