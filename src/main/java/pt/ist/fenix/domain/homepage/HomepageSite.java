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
