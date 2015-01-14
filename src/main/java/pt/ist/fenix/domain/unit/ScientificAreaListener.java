package pt.ist.fenix.domain.unit;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.*;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenix.domain.unit.components.*;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

/**
 * Created by borgez on 24-11-2014.
 */
public class ScientificAreaListener {
    private static final String BUNDLE = "resources.FenixEduLearningResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    private static final LocalizedString ANNOUNCEMENTS_TITLE = getLocalizedString(BUNDLE, "label.announcements");
    private static final LocalizedString EVENTS_TITLE = getLocalizedString(BUNDLE, "label.events");
    private static final LocalizedString HOMEPAGE_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.homepage");
    private static final LocalizedString VIEW_POST_TITLE = getLocalizedString(BUNDLE, "label.viewPost");
    private static final LocalizedString TITLE_EMPLOYEES = getLocalizedString(BUNDLE, "department.staff");
    private static final LocalizedString TITLE_TEACHERS = getLocalizedString(BUNDLE, "department.faculty");
    private static final LocalizedString TITLE_SUBUNITS = getLocalizedString(BUNDLE, "researchUnit.subunits");
    private static final LocalizedString TITLE_ORGANIZATION = getLocalizedString(BUNDLE, "researchUnit.organization");
    private static final LocalizedString TITLE_PUBLICATIONS = getLocalizedString(BUNDLE, "department.publications");
    private static final LocalizedString TITLE_COURES = getLocalizedString(BUNDLE, "department.courses");

    public static UnitSite create(Unit scientificArea) {
        final UnitSite newSite = new UnitSite(scientificArea);
        final Menu menu = new Menu(newSite);
        menu.setName(MENU_TITLE);
        createDefaultContents(newSite, menu, Authenticate.getUser());
        return newSite;
    }

    public static void createDefaultContents(Site site, Menu menu, User user) {
        UnitsListener.createDefaultContents(site, menu, user);

        Page.create(site, menu, null, TITLE_EMPLOYEES, true, "employeesByArea", user, forType(UnitEmployees.class));
        Page.create(site, menu, null, TITLE_TEACHERS, true, "departmentFaculty", user, forType(UnitTeachersComponent.class));
        Page.create(site, menu, null, TITLE_SUBUNITS, true, "subunits", user, forType(SubUnits.class));
        Page.create(site, menu, null, TITLE_ORGANIZATION, true, "unitOrganization", user, forType(Organization.class));
        Page.create(site, menu, null, TITLE_PUBLICATIONS, true, "researcherSection", user, forType(UnitReserachersComponent.class));
        Page.create(site, menu, null, TITLE_COURES, true, "departmentCourses", user, forType(UnitCourses.class));

    }
}
