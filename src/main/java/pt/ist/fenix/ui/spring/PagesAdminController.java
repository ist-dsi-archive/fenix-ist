package pt.ist.fenix.ui.spring;

import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.io.IOException;
import java.util.Optional;

import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping(method = RequestMethod.POST, consumes = JSON_VALUE)
    public @ResponseBody String create(@PathVariable String siteId, @RequestBody String bodyJson) {
        PagesAdminBean bean = new PagesAdminBean(bodyJson);
        Site site = site(siteId);
        Optional<MenuItem> menuItem = service.create(site, bean.getParent(), bean.getTitle(), bean.getBody(), bean.getPosition());
        return service.serialize(menuItem.get()).toString();
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
                service.edit(bean.getMenuItem(), bean.getParent(), bean.getTitle(), bean.getBody(), bean.getPosition(),
                        bean.getCanViewGroup());
        return service.serialize(menuItem).toString();
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
        service.updateAttachment(menuItem, attachment, updateMessage.get("position").getAsInt());
        return getAttachments(menuItem.getExternalId());
    }

    private static Site site(String siteId) {
        return getDomainObject(siteId);
    }

}
