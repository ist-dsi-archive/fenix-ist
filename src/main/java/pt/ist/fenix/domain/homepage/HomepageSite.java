package pt.ist.fenix.domain.homepage;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

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
    @Atomic
    public void delete() {
        setOwner(null);
        super.delete();
    }
}
