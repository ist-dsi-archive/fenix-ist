package pt.ist.fenix.domain.unit;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.cms.domain.component.Component.forType;

import org.fenixedu.academic.domain.Department;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.degree.components.ThesisComponent;

import pt.ist.fenix.domain.unit.components.CompetenceCourseComponent;
import pt.ist.fenix.domain.unit.components.DepartmentDegrees;
import pt.ist.fenix.domain.unit.components.DepartmentDissertationsComponent;
import pt.ist.fenix.domain.unit.components.Organization;
import pt.ist.fenix.domain.unit.components.SubUnits;
import pt.ist.fenix.domain.unit.components.UnitCourses;
import pt.ist.fenix.domain.unit.components.UnitEmployees;
import pt.ist.fenix.domain.unit.components.UnitReserachersComponent;
import pt.ist.fenix.domain.unit.components.UnitTeachersComponent;

/**
 * Created by borgez on 24-11-2014.
 */
public class DepartmentListener {
    private static final String BUNDLE = "resources.FenixEduLearningResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    private static final LocalizedString TITLE_HOMEPAGE = getLocalizedString(BUNDLE, "researchUnit.homepage");
    private static final LocalizedString TITLE_EVENTS = getLocalizedString(BUNDLE, "label.events");
    private static final LocalizedString TITLE_ANNOUNCEMENTS = getLocalizedString(BUNDLE, "label.announcements");
    private static final LocalizedString TITLE_ORGANIZATION = getLocalizedString(BUNDLE, "researchUnit.organization");
    private static final LocalizedString TITLE_SUBUNITS = getLocalizedString(BUNDLE, "researchUnit.subunits");
    private static final LocalizedString TITLE_VIEW_POST = getLocalizedString(BUNDLE, "label.viewPost");
    private static final LocalizedString TITLE_THESES = getLocalizedString(BUNDLE, "department.theses");
    private static final LocalizedString TITLE_THESIS = getLocalizedString(BUNDLE, "department.thesis");
    private static final LocalizedString TITLE_PUBLICATIONS = getLocalizedString(BUNDLE, "department.publications");
    private static final LocalizedString TITLE_DEGREES = getLocalizedString(BUNDLE, "department.degrees");
    private static final LocalizedString TITLE_COURES = getLocalizedString(BUNDLE, "department.courses");
    private static final LocalizedString TITLE_COURSE = getLocalizedString(BUNDLE, "department.course");
    private static final LocalizedString TITLE_EMPLOYEES = getLocalizedString(BUNDLE, "department.staff");
    private static final LocalizedString TITLE_TEACHERS = getLocalizedString(BUNDLE, "department.faculty");
    private static final LocalizedString TITLE_DEPARTMENT = getLocalizedString(BUNDLE, "department");

    public static UnitSite create(Department department) {
        final UnitSite newSite = new UnitSite(department.getDepartmentUnit());
        final Menu menu = new Menu(newSite);
        menu.setName(MENU_TITLE);
        createDefaultContents(newSite, menu, Authenticate.getUser());
        return newSite;
    }

    public static void createDefaultContents(Site site, Menu menu, User user) {
        UnitsListener.createDefaultContents(site, menu, user);
        Page.create(site, menu, null, TITLE_SUBUNITS, true, "subunits", user, forType(SubUnits.class));
        Page.create(site, menu, null, TITLE_ORGANIZATION, true, "unitOrganization", user, forType(Organization.class));
        Page.create(site, menu, null, TITLE_TEACHERS, true, "departmentFaculty", user, forType(UnitTeachersComponent.class));
        Page.create(site, menu, null, TITLE_EMPLOYEES, true, "employeesByArea", user, forType(UnitEmployees.class));
        Page.create(site, menu, null, TITLE_DEGREES, true, "departmentDegrees", user, forType(DepartmentDegrees.class));
        Page.create(site, menu, null, TITLE_COURES, true, "departmentCourses", user, forType(UnitCourses.class));
        Page.create(site, menu, null, TITLE_THESES, true, "dissertations", user, forType(DepartmentDissertationsComponent.class));
        Page.create(site, menu, null, TITLE_PUBLICATIONS, true, "researcherSection", user,
                forType(UnitReserachersComponent.class));
        Page.create(site, null, null, TITLE_THESIS, true, "dissertation", user, forType(ThesisComponent.class));
        Page.create(site, null, null, TITLE_COURSE, true, "competenceCourse", user, forType(CompetenceCourseComponent.class));
    }
}
