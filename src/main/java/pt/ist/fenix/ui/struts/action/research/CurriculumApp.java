package pt.ist.fenix.ui.struts.action.research;

import org.fenixedu.bennu.struts.portal.StrutsApplication;

@StrutsApplication(bundle = "ResearcherResources", path = "curriculum", titleKey = "link.viewCurriculum",
        accessGroup = "role(RESEARCHER)", hint = "Researcher")
public class CurriculumApp {
}