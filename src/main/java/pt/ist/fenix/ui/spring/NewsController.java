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

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.Site;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringApplication(group = "logged", path = "news", title = "news.title")
@SpringFunctionality(accessGroup = "logged", app = NewsController.class, title = "news.title")
@RequestMapping("/cms/news")
public class NewsController {

    @RequestMapping
    public String news(Model model, @RequestParam(defaultValue = "5", required = false) int posts) {
        model.addAttribute("posts", posts);
        Set<Category> bookmarks = Authenticate.getUser().getBookmarksSet();
        if (!bookmarks.isEmpty()) {
            model.addAttribute("allPosts", bookmarks.stream().flatMap(cat -> cat.getPostsSet().stream()).filter(Post::isVisible)
                    .filter(Post::isAccessible).sorted(Post.CREATION_DATE_COMPARATOR).limit(posts).collect(Collectors.toList()));
        } else if (Bennu.getInstance().getDefaultSite() != null) {
            model.addAttribute("allPosts", Bennu.getInstance().getDefaultSite().getPostSet().stream().filter(Post::isVisible)
                    .filter(Post::isAccessible).sorted(Post.CREATION_DATE_COMPARATOR).limit(posts).collect(Collectors.toList()));
        }
        return "fenix-learning/news";
    }

    @RequestMapping("/{siteSlug}/{postSlug}")
    public String showPost(Model model, @PathVariable String siteSlug, @PathVariable String postSlug) {
        Site site = Site.fromSlug(siteSlug);
        Post post = site.postForSlug(postSlug);
        model.addAttribute("post", post);
        return "fenix-learning/news-post";
    }

}
