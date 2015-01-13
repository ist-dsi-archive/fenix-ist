package pt.ist.fenix.ui.struts.action;

import org.fenixedu.academic.ui.struts.action.commons.FacesEntryPoint;
import org.fenixedu.academic.ui.struts.action.messaging.MessagingApplication.MessagingSearchApp;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

public class MessagingApplication {
    @StrutsFunctionality(app = MessagingSearchApp.class, path = "organizational-structure", titleKey = "label.orgUnit")
    @Mapping(path = "/organizationalStructure/structurePage", module = "messaging")
    public static class OrganizationalStructurePage extends FacesEntryPoint {
    }
}
