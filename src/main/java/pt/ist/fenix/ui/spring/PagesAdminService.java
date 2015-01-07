package pt.ist.fenix.ui.spring;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
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

import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.ImmutableList;
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
    protected Optional<MenuItem> create(Site site, MenuItem parent, LocalizedString name, LocalizedString body) {
        Menu menu = site.getMenusSet().stream().findFirst().orElse(null);
        Page page = Page.create(site, menu, parent, name, true, "view", Authenticate.getUser());
        Category category = site.getOrCreateCategoryForSlug("content", new LocalizedString().with(I18N.getLocale(), "Content"));
        Post post = Post.create(site, page, name, body, category, true, Authenticate.getUser());
        page.addComponents(new StaticPost(post));
        MenuItem menuItem = page.getMenuItemsSet().stream().findFirst().get();
        if (parent != null) {
            parent.add(menuItem);
        } else {
            menu.add(menuItem);
        }
        return Optional.of(menuItem);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected MenuItem edit(MenuItem menuItem, LocalizedString name, LocalizedString body, Group canViewGroup) {

        if (!menuItem.getName().equals(name)) {
            menuItem.setName(name);
        }

        if (!menuItem.getPage().getName().equals(name)) {
            menuItem.getPage().setName(name);
        }

        Post post = postForPage(menuItem.getPage());
        if (post.getBody() == null && body != null || post.getBody() != null && !post.getBody().equals(body)) {
            post.setBody(body);
        }
        if (!post.getName().equals(name)) {
            post.setName(name);
        }

        if (canViewGroup != null && !post.getCanViewGroup().equals(canViewGroup)) {
            post.setCanViewGroup(canViewGroup);
        }

        return menuItem;
    }

    @Atomic(mode = TxMode.WRITE)
    protected void moveTo(MenuItem item, MenuItem parent, MenuItem insertAfter) {
        Menu menu = item.getMenu();

        if (insertAfter == null && parent == null) {
            insertAfter = getLastBuiltinContent(menu);
        }

        if (parent == null) {
            MenuItem.fixOrder(menu.getToplevelItemsSorted().collect(Collectors.toList()));
            int newPosition = insertAfter == null ? 0 : insertAfter.getPosition() + 1;
            menu.putAt(item, newPosition);
        } else {
            MenuItem.fixOrder(parent.getChildrenSorted());
            int newPosition = insertAfter == null ? 0 : insertAfter.getPosition() + 1;
            parent.putAt(item, newPosition);
        }
    }

    private MenuItem getLastBuiltinContent(Menu menu) {
        return menu.getToplevelItemsSorted().sorted(Comparator.reverseOrder()).filter(isStaticPage.negate()).findFirst()
                .orElse(null);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected GroupBasedFile addAttachment(String name, MultipartFile attachment, MenuItem menuItem) throws IOException {
        Post post = postForPage(menuItem.getPage());
        GroupBasedFile file =
                new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), AnyoneGroup.get());
        post.getAttachments().putFile(file, 0);
        return file;
    }

    private Post postForPage(Page page) {
        return page.getComponentsSet().stream().filter(component -> component instanceof StaticPost)
                .map(component -> ((StaticPost) component).getPost()).filter(post -> post != null).findFirst().get();
    }

    protected JsonObject serialize(Site site) {
        JsonObject data = new JsonObject();
        if (!site.getMenusSet().isEmpty()) {
            Menu menu = site.getMenusSet().stream().findFirst().get();
            JsonObject root = new JsonObject();
            root.add("title", site.getName().json());
            root.add("root", new JsonPrimitive(true));
            root.add("isFolder", new JsonPrimitive(true));
            root.add("expanded", new JsonPrimitive(true));
            root.add("key", new JsonPrimitive("null"));

            JsonArray groupsJson = new JsonArray();
            for (Group group : permissionGroups(site)) {
                groupsJson.add(serializeGroup(group));
            }

            JsonArray child = new JsonArray();
            menu.getToplevelItemsSorted().filter(isStaticPage).map(item -> serialize(item, false))
                    .forEach(json -> child.add(json));
            root.add("children", child);
            data.add("root", root);
            data.add("groups", groupsJson);
        }
        return data;
    }

    protected JsonObject serialize(MenuItem item, boolean withBody) {
        JsonObject root = new JsonObject();

        root.add("title", item.getName().json());
        if (item.getParent() != null) {
            root.add("menuItemParentId", new JsonPrimitive(item.getParent().getExternalId()));
        }
        root.add("key", new JsonPrimitive(item.getExternalId()));
        String pageAddress = Optional.ofNullable(item.getUrl()).orElse(item.getPage().getAddress());
        root.add("pageAddress", new JsonPrimitive(pageAddress));
        root.add("position", new JsonPrimitive(item.getPosition()));
        root.add("isFolder", new JsonPrimitive(Optional.ofNullable(item.getFolder()).orElse(false)));

        if (withBody) {
            root.add("body", data(item.getMenu().getSite(), item));
        }

        root.add("files", serializeAttachments(item.getPage()));

        if (item.getChildrenSet().size() > 0) {
            root.add("folder", new JsonPrimitive(true));
            JsonArray children = new JsonArray();
            item.getChildrenSorted().stream().filter(isStaticPage).forEach(subitem -> children.add(serialize(subitem, false)));
            root.add("children", children);
        }
        root.addProperty("canViewGroupIndex", canViewGroupIndex(item.getPage(), postForPage(item.getPage()).getCanViewGroup()));

        return root;
    }

    private Integer canViewGroupIndex(Page page, Group group) {
        List<Group> permissionGroups = permissionGroups(page.getSite());
        for (int i = 0; i < permissionGroups.size(); ++i) {
            if (permissionGroups.get(i).equals(group)) {
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
        for (GroupBasedFile postFile : postFiles.getFiles()) {
            JsonObject postFileJson = new JsonObject();
            postFileJson.addProperty("name", postFile.getDisplayName());
            postFileJson.addProperty("filename", postFile.getFilename());
            postFileJson.addProperty("externalId", postFile.getExternalId());
            postFileJson.addProperty("creationDate", postFile.getCreationDate().toString());
            postFileJson.addProperty("contentType", postFile.getContentType());
            postFileJson.addProperty("size", postFile.getSize());
            postFileJson.addProperty("downloadUrl", FileDownloadServlet.getDownloadUrl(postFile));
            postFileJson.addProperty("group", canViewGroupIndex(page, postFile.getAccessGroup()));
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

    private final Predicate<MenuItem> isStaticPage = menuItem -> menuItem.getPage() != null
            && menuItem.getPage().getComponentsSet().stream().filter(StaticPost.class::isInstance)
                    .map(component -> ((StaticPost) component).getPost()).filter(post -> post != null).findFirst().isPresent();

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void delete(MenuItem menuItem, GroupBasedFile attachment) {
        Post.Attachments attachments = postForPage(menuItem.getPage()).getAttachments();
        int attachmentPosition = attachments.getFiles().indexOf(attachment);
        attachments.removeFile(attachmentPosition);
        attachment.delete();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void updateAttachment(MenuItem menuItem, GroupBasedFile attachment, int newPosition, int groupPosition,
            String displayName) {
        Post.Attachments attachments = postForPage(menuItem.getPage()).getAttachments();
        int currentPosition = attachments.getFiles().indexOf(attachment);
        attachment.setAccessGroup(permissionGroups(menuItem.getMenu().getSite()).get(groupPosition));
        if (displayName != null) {
            attachment.setDisplayName(displayName);
        }
        if (currentPosition != newPosition) {
            attachments.move(currentPosition, newPosition);
        }
    }

    public Stream<Page> dynamicPages(Site site) {
        return site.getPagesSet().stream().filter(this::isDynamicPage).filter(page -> !site.getInitialPage().equals(page))
                .sorted(comparing(Page::getName));
    }

    private boolean isDynamicPage(Page page) {
        return !page.getComponentsSet().stream().filter(StaticPost.class::isInstance).findAny().isPresent();
    }

    public JsonElement data(Site site, MenuItem item) {
        return postForPage(item.getPage()).getBody() != null ? postForPage(item.getPage()).getBody().json() : new JsonObject();
    }

    private List<Group> permissionGroups(Site site) {
        if (site instanceof ExecutionCourseSite) {
            return ((ExecutionCourseSite) site).getContextualPermissionGroups();
        }
        if (site instanceof HomepageSite) {
            return ((HomepageSite) site).getContextualPermissionGroups();
        }
        return ImmutableList.of(AnyoneGroup.get(), LoggedGroup.get());
    }

}
