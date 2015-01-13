package pt.ist.fenix.ui.struts.action;

import org.fenixedu.academic.ui.struts.action.commons.FacesEntryPoint;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

@StrutsApplication(path = "organizational-structure", titleKey = "title.manager.organizationalStructureManagement",
        bundle = "ManagerResources", accessGroup = "#managers", hint = "Manager")
public class ManagerOrganizationalStructureApp {
    @StrutsFunctionality(app = ManagerOrganizationalStructureApp.class, path = "manage",
            titleKey = "link.manager.organizationalStructureManagement")
    @Mapping(path = "/organizationalStructureManagament/listAllUnits", module = "manager")
    public static class OrganizationalStructurePage extends FacesEntryPoint {
    }
}
