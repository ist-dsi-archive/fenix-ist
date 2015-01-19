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
package pt.ist.fenix.domain.homepage.components;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;

import pt.ist.fenix.domain.homepage.HomepageSite;

/**
 * Created by borgez on 02-12-2014.
 */
public abstract class HomepageSiteComponent implements CMSComponent {

    protected Person owner(Page page) {
        return site(page).getOwner();
    }

    protected HomepageSite site(Page page) {
        if (page.getSite() instanceof HomepageSite) {
            return (HomepageSite) page.getSite();
        }
        throw new ResourceNotFoundException();
    }

    public static boolean supportsSite(Site site) {
        return site instanceof HomepageSite;
    }
}
