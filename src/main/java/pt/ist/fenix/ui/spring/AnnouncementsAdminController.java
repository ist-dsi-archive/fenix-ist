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

import static java.lang.String.format;
import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.bennu.core.security.Authenticate.getUser;
import static org.fenixedu.cms.domain.Post.CREATION_DATE_COMPARATOR;
import static pt.ist.fenixframework.FenixFramework.atomic;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.spring.controller.teacher.ExecutionCourseController;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.ui.AdminSites;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

import com.google.common.math.IntMath;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
@RequestMapping("/teacher/{executionCourse}/announcements")
public class AnnouncementsAdminController extends ExecutionCourseController {
    private static final LocalizedString ANNOUNCEMENT = getLocalizedString("resources.FenixEduLearningResources",
            "label.announcements");

    private static final int PER_PAGE = 5;

    @RequestMapping(method = RequestMethod.GET)
    public AnnouncementsAdminView all(Model model, @PathVariable ExecutionCourse executionCourse, @RequestParam(required = false,
            defaultValue = "1") int page) {
        Professorship professorship = executionCourse.getProfessorship(AccessControl.getPerson());
        AccessControl.check(person -> professorship != null && professorship.getPermissions().getAnnouncements());
        List<Post> announcements = getAnnouncements(executionCourse.getSite());
        model.addAttribute("executionCourse", executionCourse);
        int pages = IntMath.divide(announcements.size(), PER_PAGE, RoundingMode.CEILING);
        if (page < 1) {
            page = 1;
        }
        if (pages > 0 && page > pages) {
            page = pages;
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", pages);
        model.addAttribute("announcements",
                announcements.stream().skip((page - 1) * PER_PAGE).limit(PER_PAGE).collect(Collectors.toList()));
        model.addAttribute("professorship", professorship);
        return new AnnouncementsAdminView();
    }

    @RequestMapping(value = "{postSlug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable ExecutionCourse executionCourse, @PathVariable String postSlug) {
        Post post = executionCourse.getSite().postForSlug(postSlug);
        atomic(() -> post.delete());
        return viewAll(executionCourse);
    }

    @RequestMapping(value = "{postSlug}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(Model model, @PathVariable ExecutionCourse executionCourse, @PathVariable(
            value = "postSlug") String slugPost, @RequestParam("attachment") MultipartFile[] attachments) throws IOException {
        Site s = executionCourse.getSite();

        AdminSites.canEdit(s);

        Post p = s.postForSlug(slugPost);
        JsonArray array = new JsonArray();

        Arrays.asList(attachments).stream().map((attachment) -> {
            GroupBasedFile f = null;
            try {
                f = addFile(attachment, p);
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

    @Atomic
    private GroupBasedFile addFile(MultipartFile attachment, Post p) throws IOException {
        GroupBasedFile f =
                new GroupBasedFile(attachment.getOriginalFilename(), attachment.getOriginalFilename(), attachment.getBytes(),
                        AnyoneGroup.get());
        p.getPostFiles().putFile(f);
        return f;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView create(@PathVariable ExecutionCourse executionCourse, @RequestParam LocalizedString name,
            @RequestParam LocalizedString body, @RequestParam(required = false, defaultValue = "false") boolean active)
            throws Exception {
        Site site = executionCourse.getSite();
        atomic(() -> Post.create(site, null, Post.sanitize(name), Post.sanitize(body), announcementsCategory(site), active,
                getUser()));
        return viewAll(executionCourse);
    }

    @RequestMapping(value = "{postSlug}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable ExecutionCourse executionCourse, @PathVariable String postSlug,
            @RequestParam LocalizedString name, @RequestParam LocalizedString body, @RequestParam(required = false,
                    defaultValue = "false") boolean active) {
        Post post = executionCourse.getSite().postForSlug(postSlug);
        atomic(() -> {
            post.setName(Post.sanitize(name));
            post.setBody(Post.sanitize(body));
            post.setActive(active);
        });
        return viewAll(executionCourse);
    }

    private RedirectView viewAll(ExecutionCourse executionCourse) {
        return new RedirectView(format("/teacher/%s/announcements", executionCourse.getExternalId()), true);
    }

    private List<Post> getAnnouncements(Site site) {
        return announcementsCategory(site).getPostsSet().stream().sorted(CREATION_DATE_COMPARATOR).collect(Collectors.toList());
    }

    private Category announcementsCategory(Site site) {
        return site.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENT);
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    public class AnnouncementsAdminView extends JstlView {

        @Override
        protected void exposeHelpers(HttpServletRequest request) throws Exception {
            setServletContext(request.getServletContext());
            super.exposeHelpers(request);
            request.setAttribute("teacher$actual$page", "/WEB-INF/fenix-learning/announcements.jsp");
        }

        @Override
        public String getUrl() {
            return "/teacher/executionCourse/executionCourseFrame.jsp";
        }

    }
}
