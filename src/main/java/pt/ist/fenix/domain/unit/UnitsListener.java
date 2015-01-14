package pt.ist.fenix.domain.unit;

import org.fenixedu.academic.domain.Department;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenix.domain.unit.UnitSite;
import pt.ist.fenix.domain.unit.components.UnitHomepageComponent;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

/**
 * Created by borgez on 24-11-2014.
 */
public class UnitsListener {
    private static final String BUNDLE = "resources.FenixEduLearningResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    private static final LocalizedString TITLE_HOMEPAGE = getLocalizedString(BUNDLE, "researchUnit.homepage");
    private static final LocalizedString TITLE_EVENTS = getLocalizedString(BUNDLE, "label.events");
    private static final LocalizedString TITLE_ANNOUNCEMENTS = getLocalizedString(BUNDLE, "label.announcements");
    private static final LocalizedString TITLE_VIEW_POST = getLocalizedString(BUNDLE, "label.viewPost");

    public static UnitSite create(Department department) {
        final UnitSite newSite = new UnitSite(department.getDepartmentUnit());
        final Menu menu = new Menu(newSite);
        menu.setName(MENU_TITLE);
        createDefaultContents(newSite, menu, Authenticate.getUser());
        return newSite;
    }

    public static void createDefaultContents(Site site, Menu menu, User user) {
        site.setTheme(CMSTheme.forType("fenixedu-units-theme"));
        Component announcementsComponent =
                new ListCategoryPosts(site.getOrCreateCategoryForSlug("announcement", TITLE_ANNOUNCEMENTS));
        Component eventsComponent = new ListCategoryPosts(site.getOrCreateCategoryForSlug("event", TITLE_EVENTS));

        Page initialPage = Page.create(site, menu, null, TITLE_HOMEPAGE, true, "unitHomepage", user,
                forType(UnitHomepageComponent.class));
        Page.create(site, menu, null, TITLE_EVENTS, true, "category", user, eventsComponent);
        Page.create(site, menu, null, TITLE_ANNOUNCEMENTS, true, "category", user, announcementsComponent);
        Page.create(site, null, null, TITLE_VIEW_POST, true, "view", user, forType(ViewPost.class));

        site.setInitialPage(initialPage);
    }
}
