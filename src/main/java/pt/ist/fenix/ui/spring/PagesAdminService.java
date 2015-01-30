/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.ui.spring;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.*;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Service
public class PagesAdminService {

    private final Predicate<MenuItem> isStaticPage =
            menuItem -> menuItem.getPage() != null && menuItem.getPage().getComponentsSet().stream()
                    .filter(StaticPost.class::isInstance).map(component -> ((StaticPost) component).getPost())
                    .filter(post -> post != null).findFirst().isPresent();

    protected static Stream<Page> dynamicPages(Site site) {
        return site.getPagesSet().stream().filter(PagesAdminService::isDynamicPage)
                .filter(page -> !site.getInitialPage().equals(page)).sorted(comparing(Page::getName));
    }

    protected static boolean isDynamicPage(Page page) {
        return !page.getComponentsSet().stream().filter(StaticPost.class::isInstance).findAny().isPresent();
    }

    static List<Group> permissionGroups(Site site) {
        if (site instanceof ExecutionCourseSite) {
            return ((ExecutionCourseSite) site).getContextualPermissionGroups();
        }
        if (site instanceof HomepageSite) {
            return ((HomepageSite) site).getContextualPermissionGroups();
        }
        return ImmutableList.of(AnyoneGroup.get(), LoggedGroup.get());
    }

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
        Page page = Page.create(site, menu, parent, Post.sanitize(name), true, "view", Authenticate.getUser());
        Category category = site.getOrCreateCategoryForSlug("content", new LocalizedString().with(I18N.getLocale(), "Content"));
        Post post = Post.create(site, page, Post.sanitize(name), Post.sanitize(body), category, true, Authenticate.getUser());
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
    protected MenuItem edit(MenuItem menuItem, LocalizedString name, LocalizedString body, Group canViewGroup, Boolean visible) {
        name = Post.sanitize(name);
        body = Post.sanitize(body);
        if (!menuItem.getName().equals(name)) {
            menuItem.setName(name);
        }
        Post post = postForPage(menuItem.getPage());

        if (visible != null) {
            menuItem.getPage().setPublished(visible);
        }

        if (!menuItem.getPage().getName().equals(name)) {
            menuItem.getPage().setName(name);
        }

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
        root.addProperty("visible", item.getPage().isPublished());

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
        Post post = postForPage(page);
        JsonArray filesJson = new JsonArray();
        for (GroupBasedFile postFile : post.getAttachments().getFiles()) {
            JsonObject json = describeFile(page, postFile);
            json.addProperty("visible", true);
            filesJson.add(json);
        }
        if (filesJson.size() > 0) {
            filesJson.get(filesJson.size() - 1).getAsJsonObject().addProperty("last", true);
        }
        for (GroupBasedFile postFile : post.getPostFiles().getFiles()) {
            JsonObject json = describeFile(page, postFile);
            json.addProperty("visible", false);
            filesJson.add(json);
        }
        return filesJson;
    }

    protected JsonObject describeFile(Page page, GroupBasedFile file) {
        JsonObject postFileJson = new JsonObject();
        postFileJson.addProperty("name", file.getDisplayName());
        postFileJson.addProperty("filename", file.getFilename());
        postFileJson.addProperty("externalId", file.getExternalId());
        postFileJson.addProperty("creationDate", file.getCreationDate().toString());
        postFileJson.addProperty("contentType", file.getContentType());
        postFileJson.addProperty("size", file.getSize());
        postFileJson.addProperty("downloadUrl", FileDownloadServlet.getDownloadUrl(file));
        postFileJson.addProperty("group", canViewGroupIndex(page, file.getAccessGroup()));
        return postFileJson;
    }

    @Atomic
    protected GroupBasedFile addPostFile(MultipartFile attachment, MenuItem menuItem) throws IOException {
        GroupBasedFile f = new GroupBasedFile(attachment.getOriginalFilename(), attachment.getOriginalFilename(),
                        attachment.getBytes(), AnyoneGroup.get());
        postForPage(menuItem.getPage()).getPostFiles().putFile(f);
        return f;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void delete(MenuItem menuItem, GroupBasedFile file) {
        Post post = postForPage(menuItem.getPage());
        Post.Attachments attachments = post.getAttachments();
        int attachmentPosition = attachments.getFiles().indexOf(file);
        if(attachmentPosition != -1) {
            attachments.removeFile(attachmentPosition);
            file.delete();
        } else if (post.getPostFiles().getFiles().indexOf(file) != -1) {
            post.getPostFiles().removeFile(file);
            file.delete();
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void updateAttachment(MenuItem menuItem, GroupBasedFile attachment, int newPosition, int groupPosition,
            String displayName, boolean visible) {
        if (displayName != null) {
            attachment.setDisplayName(displayName);
        }
        attachment.setAccessGroup(permissionGroups(menuItem.getMenu().getSite()).get(groupPosition));

        Post post = postForPage(menuItem.getPage());

        if (visible) {
            Post.Attachments attachments = post.getAttachments();
            if (attachment.getPostFile() != null) {
                int currentPosition = attachments.getFiles().indexOf(attachment);
                if (currentPosition != newPosition) {
                    attachments.move(currentPosition, newPosition);
                }
            } else {
                attachment.setPost(null);
                PostFile postFile = new PostFile();
                postFile.setIndex(attachments.getFiles().size() + 1);
                postFile.setPost(post);
                postFile.setFiles(attachment);
            }
        } else {
            if (attachment.getPostFile() != null) {
                attachment.getPostFile().delete();
            }
            attachment.setPost(post);
        }

    }

    protected void copyStaticPage(MenuItem oldMenuItem, ExecutionCourseSite newSite, Menu newMenu, MenuItem newParent) {
        if(oldMenuItem.getPage() != null) {
            Page oldPage = oldMenuItem.getPage();
            staticPost(oldPage).ifPresent(oldPost -> {
                Page newPage = new Page(newSite);
                newPage.setName(oldPage.getName());
                newPage.setTemplate(newSite.getTheme().templateForType(oldPage.getTemplate().getType()));
                newPage.setCreatedBy(Authenticate.getUser());
                newPage.setPublished(false);

                for(Component component : oldPage.getComponentsSet()) {
                    if(component instanceof StaticPost) {
                        StaticPost staticPostComponent = (StaticPost) component;
                        Post newPost = clonePost(staticPostComponent.getPost(), newSite);
                        newPost.setActive(true);
                        StaticPost newComponent = new StaticPost(newPost);
                        newPage.addComponents(newComponent);
                    }
                }

                MenuItem newMenuItem = MenuItem.create(newMenu, newPage, oldMenuItem.getName(), newParent);
                newMenuItem.setPosition(oldMenuItem.getPosition());
                newMenuItem.setUrl(oldMenuItem.getUrl());
                newMenuItem.setFolder(oldMenuItem.getFolder());

                oldMenuItem.getChildrenSet().stream().forEach(child->copyStaticPage(child, newSite, newMenu, newMenuItem));
            });
        }
    }

    private Post clonePost(Post oldPost, Site newSite) {
        Post newPost = new Post(newSite);
        newPost.setName(oldPost.getName());
        newPost.setBody(oldPost.getBody());
        newPost.setCreationDate(new DateTime());
        newPost.setCreatedBy(Authenticate.getUser());
        newPost.setActive(oldPost.getActive());

        for(Category oldCategory : oldPost.getCategoriesSet()) {
            Category newCategory = newSite.getOrCreateCategoryForSlug(oldCategory.getSlug(), oldCategory.getName());
            newPost.addCategories(newCategory);
        }

        for (int i = 0; i < oldPost.getAttachments().getFiles().size(); ++i) {
            GroupBasedFile file = oldPost.getAttachments().getFiles().get(i);
            GroupBasedFile attachmentCopy =
                    new GroupBasedFile(file.getDisplayName(), file.getFilename(), file.getContent(), AnyoneGroup.get());
            newPost.getAttachments().putFile(attachmentCopy, i);
        }

        for (GroupBasedFile file : oldPost.getPostFiles().getFiles()) {
            GroupBasedFile postFileCopy =
                    new GroupBasedFile(file.getDisplayName(), file.getFilename(), file.getContent(), AnyoneGroup.get());
            newPost.getPostFiles().putFile(postFileCopy);
        }
        return newPost;
    }

    private Optional<Post> staticPost(Page page) {
        return page.getComponentsSet().stream().filter(StaticPost.class::isInstance).map(StaticPost.class::cast)
                .map(StaticPost::getPost).findFirst();
    }

    public JsonElement data(Site site, MenuItem item) {
        return postForPage(item.getPage()).getBody() != null ? postForPage(item.getPage()).getBody().json() : new JsonObject();
    }

}
