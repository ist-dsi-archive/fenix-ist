package pt.ist.learning.homepage.components;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.exceptions.ResourceNotFoundException;
import pt.ist.learning.homepage.HomepageSite;

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
}
