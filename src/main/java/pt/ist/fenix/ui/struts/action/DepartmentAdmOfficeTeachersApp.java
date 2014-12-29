package pt.ist.fenix.ui.struts.action;

import org.fenixedu.bennu.struts.portal.StrutsApplication;

@StrutsApplication(bundle = "DepartmentAdmOfficeResources", path = "teachers", titleKey = "link.group.teacher.title",
        hint = "Department Admin Office",
        accessGroup = "role(DEPARTMENT_CREDITS_MANAGER) & role(DEPARTMENT_ADMINISTRATIVE_OFFICE)")
public class DepartmentAdmOfficeTeachersApp {
}
