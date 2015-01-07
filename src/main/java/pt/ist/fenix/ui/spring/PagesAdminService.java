package pt.ist.fenix.ui.spring;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Service
public class PagesAdminService {

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected void delete(MenuItem menuItem) {
        //recursive call to remove associated childrens
        menuItem.getChildrenSorted().forEach(this::delete);
        //deleting a page allready deletes all the associated menu items and components
        menuItem.getPage().delete();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected Optional<MenuItem> create(Site site, MenuItem parent, LocalizedString name, LocalizedString body, Integer position) {
        Menu menu = site.getMenusSet().stream().findFirst().orElse(null);
        Page page = Page.create(site, menu, parent, name, true, "view", Authenticate.getUser());
        Category category = site.getOrCreateCategoryForSlug("content", new LocalizedString().with(I18N.getLocale(), "Content"));
        Post post = Post.create(site, page, name, body, category, true, Authenticate.getUser());
        page.addComponents(new StaticPost(post));
        MenuItem menuItem = page.getMenuItemsSet().stream().findFirst().get();
        if(parent!=null) {
            parent.add(menuItem);
        } else {
            menu.add(menuItem);
        }
        return Optional.of(menuItem);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected MenuItem edit(MenuItem menuItem, MenuItem menuItemParent, LocalizedString name, LocalizedString body, Integer position, Group canViewGroup) {
        Menu menu = menuItem.getMenu();

        if (!menuItem.getName().equals(name)) {
            menuItem.setName(name);
        }

        if (!menuItem.getPage().getName().equals(name)) {
            menuItem.getPage().setName(name);
        }

        Post post = postForPage(menuItem.getPage());
        if(post.getBody() == null && body != null || post.getBody() != null && !post.getBody().equals(body)) {
            post.setBody(body);
        }
        if (!post.getName().equals(name)) {
            post.setName(name);
        }

        if(menuItem.getParent() != menuItemParent || menuItem.getPosition() != position) {
            if(menuItemParent == null) {
                menu.putAt(menuItem, position);
            } else {
                menuItemParent.putAt(menuItem, position);
            }
        }

        if(canViewGroup!= null && !menuItem.getPage().getCanViewGroup().equals(canViewGroup)) {
            menuItem.getPage().setCanViewGroup(canViewGroup);
        }

        return menuItem;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected GroupBasedFile addAttachment(String name, MultipartFile attachment, MenuItem menuItem) throws IOException {
        Post post = postForPage(menuItem.getPage());
        GroupBasedFile file = new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), AnyoneGroup.get());
        post.getAttachments().putFile(file, 0);
        return file;
    }

    private Post postForPage(Page page) {
        return page.getComponentsSet().stream().filter(component->component instanceof StaticPost)
                .map(component -> ((StaticPost) component).getPost()).filter(post->post!=null).findFirst().get();
    }

    protected JsonObject serialize(Site cmsSite) {
        JsonObject data = new JsonObject();
        if(!cmsSite.getMenusSet().isEmpty()) {
            Menu menu = cmsSite.getMenusSet().stream().findFirst().get();
            JsonObject root = new JsonObject();
            root.add("title", new JsonPrimitive(cmsSite.getName().getContent()));
            root.add("root", new JsonPrimitive(true));
            root.add("isFolder", new JsonPrimitive(true));
            root.add("expanded", new JsonPrimitive(true));
            root.add("key", new JsonPrimitive("null"));

            JsonArray groupsJson = new JsonArray();
            if(cmsSite instanceof ExecutionCourseSite) {
                for (Group group : ((ExecutionCourseSite) cmsSite).getContextualPermissionGroups()) {
                    groupsJson.add(serializeGroup(group));
                }
            }

            JsonArray child = new JsonArray();
            menu.getToplevelItemsSorted().filter(isStaticPage).map(this::serialize).forEach(json -> child.add(json));
            root.add("children", child);
            data.add("root", root);
            data.add("groups", groupsJson);
        }
        return data;
    }

    protected JsonObject serialize(MenuItem item) {
        JsonObject root = new JsonObject();

        root.add("title", item.getName().json());
        if(item.getParent()!=null) {
            root.add("menuItemParentId", new JsonPrimitive(item.getParent().getExternalId()));
        }
        root.add("key", new JsonPrimitive(item.getExternalId()));
        String pageAddress = Optional.ofNullable(item.getUrl()).orElse(item.getPage().getAddress());
        root.add("pageAddress", new JsonPrimitive(pageAddress));
        root.add("position", new JsonPrimitive(item.getPosition()));
        root.add("isFolder", new JsonPrimitive(Optional.ofNullable(item.getFolder()).orElse(false)));
        JsonElement body = postForPage(item.getPage()).getBody() != null ? postForPage(item.getPage()).getBody().json() : new JsonObject();
        root.add("body", body);

        root.add("files", serializeAttachments(item.getPage()));

        if (item.getChildrenSet().size() > 0) {
            root.add("folder", new JsonPrimitive(true));
            JsonArray children = new JsonArray();
            item.getChildrenSorted().stream().filter(isStaticPage).forEach(subitem -> children.add(serialize(subitem)));
            root.add("children", children);
        }
        if(item.getPage().getSite() instanceof ExecutionCourseSite) {
            root.addProperty("canViewGroupIndex", canViewGroupIndex(item));
        }

        return root;
    }

    private Integer canViewGroupIndex(MenuItem item) {
        ExecutionCourseSite cmsSite = (ExecutionCourseSite) item.getPage().getSite();
        for(int i=0; i< cmsSite.getContextualPermissionGroups().size(); ++i) {
            if(cmsSite.getContextualPermissionGroups().get(i).equals(item.getPage().getCanViewGroup())) {
                return i;
            }
        }
        return 0;
    }


    private JsonObject serializeGroup(Group group) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", group.getPresentationName());
        jsonObject.addProperty("expression", group.getExpression());
        return jsonObject;
    }


    protected JsonElement serializeAttachments(Page page) {
        Post.Attachments postFiles = postForPage(page).getAttachments();
        JsonArray filesJson = new JsonArray();
        for(GroupBasedFile postFile : postFiles.getFiles()){
            JsonObject postFileJson = new JsonObject();
            postFileJson.addProperty("name", postFile.getDisplayName());
            postFileJson.addProperty("externalId", postFile.getExternalId());
            postFileJson.addProperty("creationDate", postFile.getCreationDate().toString());
            postFileJson.addProperty("contentType", postFile.getContentType());
            postFileJson.addProperty("size", postFile.getSize());
            postFileJson.addProperty("downloadUrl", FileDownloadServlet.getDownloadUrl(postFile));
            filesJson.add(postFileJson);
        }
        return filesJson;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected GroupBasedFile addPostFile(String name, MultipartFile multipartFile, MenuItem menuItem) throws IOException {
        Post post = postForPage(menuItem.getPage());
        GroupBasedFile file =
                new GroupBasedFile(name, multipartFile.getOriginalFilename(), multipartFile.getBytes(), AnyoneGroup.get());
        post.getPostFiles().putFile(file);
        return file;
    }

    private Predicate<MenuItem> isStaticPage = menuItem -> menuItem.getPage()!=null &&
                menuItem.getPage().getComponentsSet().stream().filter(StaticPost.class::isInstance)
                    .map(component -> ((StaticPost) component).getPost()).filter(post->post!=null).findFirst().isPresent();

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void delete(MenuItem menuItem, GroupBasedFile attachment) {
        Post.Attachments attachments = postForPage(menuItem.getPage()).getAttachments();
        int attachmentPosition = attachments.getFiles().indexOf(attachment);
        attachments.removeFile(attachmentPosition);
        attachment.delete();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void updateAttachment(MenuItem menuItem, GroupBasedFile attachment, int newPosition) {
        Post.Attachments attachments = postForPage(menuItem.getPage()).getAttachments();
        int currentPosition = attachments.getFiles().indexOf(attachment);
        attachments.move(currentPosition, newPosition);
    }

    public Stream<Page> dynamicPages(Site site) {
        return site.getPagesSet().stream().filter(this::isDynamicPage).filter(page->!site.getInitialPage().equals(page))
                .sorted(comparing(Page::getName));
    }

    private boolean isDynamicPage(Page page) {
        return !page.getComponentsSet().stream().filter(StaticPost.class::isInstance).findAny().isPresent();
    }

}
