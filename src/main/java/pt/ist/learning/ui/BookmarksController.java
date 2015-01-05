package pt.ist.learning.ui;

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
