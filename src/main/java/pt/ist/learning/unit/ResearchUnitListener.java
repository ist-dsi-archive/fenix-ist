package pt.ist.learning.unit;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.ListCategoryPosts;
import org.fenixedu.cms.domain.component.MenuComponent;
import org.fenixedu.cms.domain.component.ViewPost;
import org.fenixedu.commons.i18n.LocalizedString;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

/**
 * Created by borgez on 24-11-2014.
 */
public class ResearchUnitListener {
    private static final String BUNDLE = "resources.FenixEduCMSResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduCMSResources", "label.menu");
    private static final LocalizedString ANNOUNCEMENTS_TITLE = getLocalizedString(BUNDLE, "label.announcements");
    private static final LocalizedString EVENTS_TITLE = getLocalizedString(BUNDLE, "label.events");
    private static final LocalizedString MEMBERS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "label.researchers");
    private static final LocalizedString SUBUNITS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.subunits");
    private static final LocalizedString ORGANIZATION_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.organization");
    private static final LocalizedString HOMEPAGE_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.homepage");
    private static final LocalizedString PUBLICATIONS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "department.publications");
    private static final LocalizedString VIEW_POST_TITLE = getLocalizedString(BUNDLE, "label.viewPost");

    public static UnitSite create(Unit researchUnit) {
        final UnitSite newSite = new UnitSite(researchUnit);
        final Menu menu = new Menu(newSite, MENU_TITLE);
        final User user = Authenticate.getUser();

        newSite.setTheme(CMSTheme.forType("fenixedu-units-theme"));

        Component menuComponent = new MenuComponent(menu);
        Component announcementsComponent = new ListCategoryPosts(newSite.categoryForSlug("announcement", ANNOUNCEMENTS_TITLE));
        Component eventsComponent = new ListCategoryPosts(newSite.categoryForSlug("event", EVENTS_TITLE));

        Page initialPage = Page.create(newSite, menu, null, HOMEPAGE_TITLE, true, "unitHomepageWithBannerIntro", user, forType(UnitHomepageComponent.class), menuComponent);
        Page.create(newSite, null, null, VIEW_POST_TITLE, true, "view", user, forType(ViewPost.class), menuComponent);
        Page.create(newSite, menu, null, MEMBERS_TITLE, true, "members", user, forType(UnitComponent.class), menuComponent);
        Page.create(newSite, menu, null, SUBUNITS_TITLE, true, "subunits", user, forType(SubUnits.class), menuComponent);
        Page.create(newSite, menu, null, ORGANIZATION_TITLE, true, "unitOrganization", user, forType(Organization.class), menuComponent);
        Page.create(newSite, menu, null, PUBLICATIONS_TITLE, true, "researcherSection", user, forType(UnitReserachersComponent.class), menuComponent);
        Page.create(newSite, menu, null, ANNOUNCEMENTS_TITLE, true, "category", user, announcementsComponent, menuComponent);
        Page.create(newSite, menu, null, EVENTS_TITLE, true, "category", user, eventsComponent, menuComponent);

        newSite.setInitialPage(initialPage);

        return newSite;
    }
}
