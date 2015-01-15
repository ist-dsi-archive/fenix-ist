package pt.ist.fenix.ui.spring;

import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.cms.ui.AdminSites;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping("/pages/{siteId}/admin")
public class PagesAdminController {

    private static final String JSON_VALUE = "application/json; charset=utf-8";

    @Autowired
    PagesAdminService service;

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = JSON_VALUE)
    public @ResponseBody String data(@PathVariable String siteId) {
        return service.serialize(site(siteId)).toString();
    }

    @RequestMapping(value = "/data/{menuItem}", method = RequestMethod.GET, produces = JSON_VALUE)
    public @ResponseBody String data(@PathVariable String siteId, @PathVariable MenuItem menuItem) {
        return service.data(site(siteId), menuItem).toString();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = JSON_VALUE)
    public @ResponseBody String create(@PathVariable String siteId, @RequestBody String bodyJson) {
        PagesAdminBean bean = new PagesAdminBean(bodyJson);
        Site site = site(siteId);
        Optional<MenuItem> menuItem = service.create(site, bean.getParent(), bean.getTitle(), bean.getBody());
        return service.serialize(menuItem.get(), true).toString();
    }

    @RequestMapping(value = "/{menuItemId}", method = RequestMethod.DELETE)
    public @ResponseBody String delete(@PathVariable String siteId, @PathVariable String menuItemId) {
        service.delete(getDomainObject(menuItemId));
        return data(siteId);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = JSON_VALUE)
    public @ResponseBody String edit(@RequestBody String bodyJson) {
        PagesAdminBean bean = new PagesAdminBean(bodyJson);
        MenuItem menuItem =
                service.edit(bean.getMenuItem(), bean.getTitle(), bean.getBody(), bean.getCanViewGroup(), bean.isVisible());
        return service.serialize(menuItem, true).toString();
    }

    @RequestMapping(value = "{menuItem}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(Model model, @PathVariable Site site,
            @PathVariable(value = "menuItem") String menuItemId, @RequestParam("attachment") MultipartFile[] attachments)
            throws IOException {
        MenuItem menuItem = FenixFramework.getDomainObject(menuItemId);

        AdminSites.canEdit(menuItem.getMenu().getSite());

        JsonArray array = new JsonArray();

        Arrays.asList(attachments).stream().map((attachment) -> {
            GroupBasedFile f = null;
            try {
                f = service.addPostFile(attachment.getName(), attachment, menuItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("displayname", f.getDisplayName());
            obj.addProperty("filename", f.getFilename());
            obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));
            return obj;
        }).forEach(x -> array.add(x));

        return array.toString();
    }

    @RequestMapping(value = "/move", method = RequestMethod.PUT, consumes = JSON_VALUE)
    public @ResponseBody String move(@RequestBody String bodyJson) {
        JsonObject json = new JsonParser().parse(bodyJson).getAsJsonObject();
        MenuItem item = getDomainObject(json.get("menuItemId").getAsString());
        MenuItem parent = getDomainObject(json.get("parent").getAsString());
        MenuItem insertAfter =
                getDomainObject(json.get("insertAfter").isJsonNull() ? null : json.get("insertAfter").getAsString());
        service.moveTo(item, parent, insertAfter);
        return service.serialize(item, false).toString();
    }

    @RequestMapping(value = "/attachment/{menuItemId}", method = RequestMethod.POST)
    public @ResponseBody String addAttachments(@PathVariable("menuItemId") String menuItemId,
            @RequestParam("file") MultipartFile file) throws IOException {
        service.addAttachment(file.getOriginalFilename(), file, getDomainObject(menuItemId));
        return getAttachments(menuItemId);
    }

    @RequestMapping(value = "/attachment/{menuItemId}/{fileId}", method = RequestMethod.DELETE, produces = JSON_VALUE)
    public @ResponseBody String deleteAttachments(@PathVariable String menuItemId, @PathVariable String fileId) {
        MenuItem menuItem = getDomainObject(menuItemId);
        GroupBasedFile postFile = getDomainObject(fileId);
        service.delete(menuItem, postFile);
        return getAttachments(menuItemId);
    }

    @RequestMapping(value = "/attachments", method = RequestMethod.GET)
    public @ResponseBody String getAttachments(@RequestParam(required = true) String menuItemId) {
        MenuItem menuItem = getDomainObject(menuItemId);
        return service.serializeAttachments(menuItem.getPage()).toString();
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.PUT)
    public @ResponseBody String updateAttachment(@RequestBody String bodyJson) {
        JsonObject updateMessage = new JsonParser().parse(bodyJson).getAsJsonObject();
        MenuItem menuItem = getDomainObject(updateMessage.get("menuItemId").getAsString());
        GroupBasedFile attachment = getDomainObject(updateMessage.get("fileId").getAsString());
        service.updateAttachment(menuItem, attachment, updateMessage.get("position").getAsInt(), updateMessage.get("group")
                .getAsInt(), updateMessage.get("name").getAsString(), updateMessage.get("visible").getAsBoolean());
        return getAttachments(menuItem.getExternalId());
    }

    @ModelAttribute("site")
    private Site site(@PathVariable String siteId) {
        Site site = getDomainObject(siteId);
        if (!FenixFramework.isDomainObjectValid(site)) {
            throw BennuCoreDomainException.resourceNotFound(siteId);
        }
        if (site instanceof ExecutionCourseSite) {
            if (((ExecutionCourseSite) site).getExecutionCourse().getProfessorshipForCurrentUser() == null) {
                throw CmsDomainException.forbiden();
            }
        } else if (site instanceof HomepageSite) {
            if (!Objects.equals(AccessControl.getPerson(), ((HomepageSite) site).getOwner())) {
                throw CmsDomainException.forbiden();
            }
        } else if (!site.getCanAdminGroup().isMember(Authenticate.getUser())) {
            throw CmsDomainException.forbiden();
        }
        return site;
    }
}
