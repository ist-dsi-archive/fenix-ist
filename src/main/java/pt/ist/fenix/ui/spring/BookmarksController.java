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

import java.util.Comparator;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.Category;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(accessGroup = "logged", app = NewsController.class, title = "bookmarks.title")
@RequestMapping("learning/bookmarks")
public class BookmarksController {

    @RequestMapping
    public String bookmarks(Model model) {
        model.addAttribute(
                "bookmarks",
                Authenticate.getUser().getBookmarksSet().stream()
                        .sorted(Comparator.comparing(cat -> cat.getSite().getName().getContent())).collect(Collectors.toList()));
        return "fenix-learning/bookmarks";
    }

    @RequestMapping("/add/{category}")
    public RedirectView addBookmark(@PathVariable Category category) {
        FenixFramework.atomic(() -> {
            Authenticate.getUser().addBookmarks(category);
        });
        return new RedirectView("/learning/bookmarks", true);
    }

    @RequestMapping("/remove/{category}")
    public RedirectView removeBookmark(@PathVariable Category category) {
        FenixFramework.atomic(() -> {
            Authenticate.getUser().removeBookmarks(category);
        });
        return new RedirectView("/learning/bookmarks", true);
    }

}
