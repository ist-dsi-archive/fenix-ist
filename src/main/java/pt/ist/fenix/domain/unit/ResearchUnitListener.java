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
public class ResearchUnitListener {
    private static final String BUNDLE = "resources.FenixEduLearningResources";
    private static final LocalizedString MENU_TITLE = getLocalizedString("resources.FenixEduLearningResources", "label.menu");
    private static final LocalizedString MEMBERS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "label.researchers");
    private static final LocalizedString SUBUNITS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.subunits");
    private static final LocalizedString ORGANIZATION_TITLE = BundleUtil.getLocalizedString(BUNDLE, "researchUnit.organization");
    private static final LocalizedString PUBLICATIONS_TITLE = BundleUtil.getLocalizedString(BUNDLE, "department.publications");

    public static UnitSite create(Unit researchUnit) {
        final UnitSite newSite = new UnitSite(researchUnit);
        final Menu menu = new Menu(newSite);
        menu.setName(MENU_TITLE);
        createDefaultContents(newSite, menu, Authenticate.getUser());
        return newSite;
    }

    public static void createDefaultContents(Site site, Menu menu, User user) {
        UnitsListener.createDefaultContents(site, menu, user);
        Page.create(site, menu, null, MEMBERS_TITLE, true, "members", user, forType(UnitComponent.class));
        Page.create(site, menu, null, SUBUNITS_TITLE, true, "subunits", user, forType(SubUnits.class));
        Page.create(site, menu, null, ORGANIZATION_TITLE, true, "unitOrganization", user, forType(Organization.class));
        Page.create(site, menu, null, PUBLICATIONS_TITLE, true, "researcherSection", user, forType(UnitReserachersComponent.class));
    }
}
