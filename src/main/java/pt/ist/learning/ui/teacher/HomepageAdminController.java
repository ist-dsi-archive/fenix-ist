package pt.ist.learning.ui.teacher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.struts.action.person.PersonApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pt.ist.learning.homepage.HomepageSite;

import java.util.stream.Stream;

import static pt.ist.fenixframework.FenixFramework.atomic;
import static pt.ist.fenixframework.FenixFramework.getDomainObject;

@SpringFunctionality(app = PersonApplication.PersonalAreaApp.class, title = "application.homepage.admin")
@RequestMapping("/personal/homepage/admin")
public class HomepageAdminController {

    @Autowired
    PagesAdminService service;

    @RequestMapping(method = RequestMethod.GET)
    public String all(Model model) {
        model.addAttribute("homepage", getHomepage());
        return "person/homepageManagement";
    }

    @RequestMapping(value="dynamicPages", method = RequestMethod.GET)
    public @ResponseBody String getDynamicPages() {
        return toJsonArray(service.dynamicPages(getHomepage())).toString();
    }

    @RequestMapping(value="dynamicPages/{menuItemId}", method = RequestMethod.PUT)
    public @ResponseBody String editDynamicPage(@PathVariable String menuItemId) {
        Page page = getMenuItem(menuItemId).getPage();
        atomic(() -> page.setPublished(page.isPublished()));
        return getDynamicPages();
    }

    @RequestMapping(value="homepageOptions", method = RequestMethod.GET)
    public @ResponseBody String getHomepageOptions() {
        System.out.println("geting homepage options..");

        return null;
    }

    @RequestMapping(value="homepageOptions", method = RequestMethod.PUT)
    public RedirectView editHomepageOptions() {
        System.out.println("editing homepage options..");
        return new RedirectView("/personal/homepage/admin");
    }

    private MenuItem getMenuItem(String menuItemId) {
        MenuItem menuItem = getDomainObject(menuItemId);
        AccessControl.check((obj) -> menuItem.getPage().getSite() == getHomepage());
        return menuItem;
    }

    private HomepageSite getHomepage() {
        AccessControl.check(AccessControl.getPerson(), (person) -> person != null && person.getCmsSite() != null);
        return AccessControl.getPerson().getCmsSite();
    }

    private JsonObject toJson(HomepageSite homepageSite) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("showActiveStudentCurricularPlans", homepageSite.getShowActiveStudentCurricularPlans());
        jsonObject.addProperty("showAlternativeHomepage", homepageSite.getShowAlternativeHomepage());
        jsonObject.addProperty("showAlumniDegrees", homepageSite.getShowAlumniDegrees());
        jsonObject.addProperty("showCategory", homepageSite.getShowCategory());
        jsonObject.addProperty("showCurrentAttendingExecutionCourses", homepageSite.getShowCurrentAttendingExecutionCourses());
        jsonObject.addProperty("showCurrentExecutionCourses", homepageSite.getShowCurrentExecutionCourses());
        jsonObject.addProperty("showEmail", homepageSite.getShowEmail());
        jsonObject.addProperty("showMobileTelephone", homepageSite.getShowMobileTelephone());
        jsonObject.addProperty("showPhoto", homepageSite.getShowPhoto());
        jsonObject.addProperty("showResearchUnitHomepage", homepageSite.getShowResearchUnitHomepage());
        jsonObject.addProperty("showPersonalTelephone", homepageSite.getShowPersonalTelephone());
        jsonObject.addProperty("showUnit", homepageSite.getShowUnit());
        jsonObject.addProperty("showWorkTelephone", homepageSite.getShowWorkTelephone());
        jsonObject.addProperty("researchUnitHomepage", homepageSite.getResearchUnitHomepage());
        jsonObject.add("researchUnitName", homepageSite.getResearchUnitName().json());
        return jsonObject;
    }

    private JsonArray toJsonArray(Stream<Page> pageStream) {
        JsonArray pagesJson = new JsonArray();
        pageStream.map(this::toJsonObject).forEach(pagesJson::add);
        return pagesJson;
    }

    private JsonObject toJsonObject(Page dynamicPage) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", dynamicPage.getName().getContent());
        jsonObject.addProperty("published", dynamicPage.getPublished());
        jsonObject.addProperty("address", dynamicPage.getAddress());
        return jsonObject;
    }
}
