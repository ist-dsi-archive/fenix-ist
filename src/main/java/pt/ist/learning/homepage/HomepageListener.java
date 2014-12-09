package pt.ist.learning.homepage;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.CMSTheme;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.MenuComponent;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.learning.homepage.components.PresentationComponent;
import pt.ist.learning.homepage.components.ResearcherComponent;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

/**
 * Created by borgez on 24-11-2014.
 */
public class HomepageListener {
    private static final String BUNDLE = "resources.FenixEduCMSResources";
    private static final String INTERESTS_KEY = "homepage.researcher.interests";
    private static final String PATENTS_KEY = "homepage.researcher.patents";
    private static final String PUBLICATIONS_KEY = "homepage.researcher.publications";
    private static final String ACTIVITIES_KEY = "homepage.researcher.activities";
    private static final String PRIZES_KEY = "homepage.researcher.prizes";

    private static final LocalizedString PRESENTATION_TITLE = getLocalizedString(BUNDLE, "homepage.presentation.title");
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduCMSResources", "label.menu");
    private static final LocalizedString INTERESTS_TITLE = getLocalizedString(BUNDLE, INTERESTS_KEY);
    private static final LocalizedString PATENTS_TITLE = getLocalizedString(BUNDLE, PATENTS_KEY);
    private static final LocalizedString PUBLICATIONS_TITLE = getLocalizedString(BUNDLE, PUBLICATIONS_KEY);
    private static final LocalizedString ACTIVITIES_TITLE = getLocalizedString(BUNDLE, ACTIVITIES_KEY);
    private static final LocalizedString PRIZES_TITLE = getLocalizedString(BUNDLE, PRIZES_KEY);

    public static HomepageSite create(Person person) {
        HomepageSite newSite = new HomepageSite(person);
        Menu menu = new Menu(newSite, MENU_TITLE);
        User user = Authenticate.getUser();

        newSite.setTheme(CMSTheme.forType("fenixedu-homepages-theme"));

        Component menuComponent = new MenuComponent(menu);
        Component presentationComponent = Component.forType(PresentationComponent.class);
        Component interestsComponent = new ResearcherComponent(INTERESTS_KEY, BUNDLE, "interests");
        Component prizesComponent = new ResearcherComponent(PRIZES_KEY, BUNDLE, "prizes");
        Component activitiesComponent = new ResearcherComponent(ACTIVITIES_KEY, BUNDLE, "activities");
        Component patentsComponent = new ResearcherComponent(PATENTS_KEY, BUNDLE, "patents");
        Component publicationsComponent = new ResearcherComponent(PUBLICATIONS_KEY, BUNDLE, "publications");

        Page initialPage = Page.create(newSite, menu, null, PRESENTATION_TITLE, true, "presentation", user, presentationComponent, menuComponent);
        Page.create(newSite, menu, null, INTERESTS_TITLE, false, "researcherSection", user, interestsComponent, menuComponent);
        Page.create(newSite, menu, null, PRIZES_TITLE, false, "researcherSection", user, prizesComponent, menuComponent);
        Page.create(newSite, menu, null, ACTIVITIES_TITLE, false, "researcherSection", user, activitiesComponent, menuComponent);
        Page.create(newSite, menu, null, PATENTS_TITLE, false, "researcherSection", user, patentsComponent, menuComponent);
        Page.create(newSite, menu, null, PUBLICATIONS_TITLE, false, "researcherSection", user, publicationsComponent, menuComponent);

        newSite.setInitialPage(initialPage);

        return newSite;
    }
}
