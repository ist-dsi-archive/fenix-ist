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
package pt.ist.fenix.domain.homepage;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

import java.util.List;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.ImmutableList;

public class HomepageSite extends HomepageSite_Base {
    public HomepageSite(Person person) {
        super();
        setBennu(Bennu.getInstance());
        setName(getLocalizedString("resources.FenixEduLearningResources", "homepage.title", person.getName()));
        setDescription(getLocalizedString("resources.FenixEduLearningResources", "homepage.title", person.getName()));
        setSlug(person.getUser().getUsername());
        setOwner(person);
    }

    @Override
    public LocalizedString getName() {
        return new LocalizedString(I18N.getLocale(), getOwner().getProfile().getDisplayName());
    }

    @Override
    public LocalizedString getDescription() {
        return getName();
    }

    @Override
    @Atomic
    public void delete() {
        setOwner(null);
        super.delete();
    }

    public List<Group> getContextualPermissionGroups() {
        return ImmutableList.of(AnyoneGroup.get(), LoggedGroup.get(), UserGroup.of(getOwner().getUser()));
    }
}
