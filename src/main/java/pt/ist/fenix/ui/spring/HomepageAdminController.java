package pt.ist.fenix.ui.spring;

import static pt.ist.fenixframework.FenixFramework.atomic;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenix.domain.homepage.HomepageSite;

@SpringApplication(group = "logged", path = "personal-homepage", title = "application.homepage.admin")
@SpringFunctionality(app = HomepageAdminController.class, title = "application.homepage.admin")
@RequestMapping("/personal-homepage")
public class HomepageAdminController {

    @Autowired
    PagesAdminService service;

    @RequestMapping(method = RequestMethod.GET)
    public String options(Model model) {
        model.addAttribute("homepage", getHomepage());
        model.addAttribute("person", loggedPerson());
        model.addAttribute("dynamicPages", service.dynamicPages(getHomepage()).collect(Collectors.toList()));
        return "fenix-learning/homepageOptions";
    }

    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public String content(Model model) {
        model.addAttribute("homepage", getHomepage());
        return "fenix-learning/homepageContent";
    }

    @RequestMapping(value = "/options", method = RequestMethod.POST)
    public RedirectView editHomepageOptions(@RequestParam(required = false, defaultValue = "false") Boolean showPhoto,
            @RequestParam(required = false, defaultValue = "false") Boolean showCategory, @RequestParam(required = false,
                    defaultValue = "false") Boolean showResearchUnitHomepage, @RequestParam(required = false,
                    defaultValue = "false") Boolean showActiveStudentCurricularPlans, @RequestParam(required = false,
                    defaultValue = "false") Boolean published, @RequestParam(required = false) String researchUnitHomepage,
            @RequestParam(required = false) LocalizedString researchUnitName) {
        atomic(() -> {
            HomepageSite homepage = getHomepage();
            homepage.setShowPhoto(showPhoto);
            homepage.setShowCategory(showCategory);
            homepage.setShowResearchUnitHomepage(showResearchUnitHomepage);
            homepage.setShowActiveStudentCurricularPlans(showActiveStudentCurricularPlans);
            homepage.setResearchUnitHomepage(researchUnitHomepage);
            homepage.setResearchUnitName(researchUnitName);
            homepage.setPublished(published);
        });
        return new RedirectView("personal-homepage", true);
    }

    @RequestMapping(value = "/activePages", method = RequestMethod.POST)
    public RedirectView editActivePages(HttpServletRequest request) {
        atomic(() -> service.dynamicPages(getHomepage()).forEach(
                page -> page.setPublished(request.getParameterMap().keySet().contains(page.getSlug()))));
        return new RedirectView("personal-homepage", true);
    }

    private HomepageSite getHomepage() {
        AccessControl.check((obj) -> loggedPerson().getHomepage() != null);
        return loggedPerson().getHomepage();
    }

    private Person loggedPerson() {
        AccessControl.check((obj) -> AccessControl.getPerson() != null);
        return AccessControl.getPerson();
    }

}
