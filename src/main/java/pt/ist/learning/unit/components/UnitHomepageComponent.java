package pt.ist.learning.unit.components;

import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.PostMetadata;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.component.PostsPresentationBean;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.cms.rendering.TemplateContext;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ComponentType(name = "Unit Homepage", description = "Provides the latest events and announcements")
public class UnitHomepageComponent implements CMSComponent {

    private static final Random random = new Random();
    private static final long NUM_POSTS = 3;

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext global) {
        PostMetadata metadata = metadata(page.getSite());
        String staticDir = page.getSite().getStaticDirectory();
        global.put("announcements", postsForCategory(page.getSite().categoryForSlug("announcement")));
        global.put("events", postsForCategory(page.getSite().categoryForSlug("event")));
        global.put("backgroundImage", metadata.getAsString("backgroundImage").orElse(staticDir + "/images/bolonha_bck.gif"));
        global.put("mainImage", metadata.getAsString("mainImage").orElse(staticDir + "/images/bolonha.gif"));
        global.put("link", metadata.getAsString("link").orElse("http://www.bolonha.ist.eu"));
        global.put("showIntroduction", metadata.getAsBoolean("showIntroduction").orElse(true));
        global.put("repeatType", metadata.getAsString("repeatType").orElse("repeat-x"));
        global.put("color", metadata.getAsString("color").orElse("#019AD7"));
        global.put("showBanner", metadata.getAsBoolean("showBanner").orElse(true));
        global.put("weight", metadata.getAsNumber("weight").orElse(100));
        global.put("weightPercentage", metadata.getAsNumber("weightPercentage").orElse(100));
        global.put("showPersonalizedLogo", metadata.getAsBoolean("showPersonalizedLogo").orElse(false));
        global.put("showAnnouncements", metadata.getAsBoolean("showAnnouncements").orElse(true));
        global.put("showEvents", metadata.getAsBoolean("showEvents").orElse(true));
    }

    private PostMetadata metadata(Site site) {
        Optional<Category> bannerCategory = Optional.ofNullable(site.categoryForSlug("banner"));
        if (bannerCategory.isPresent()) {
            //find a random banner post, so that it changes each time the users visits the page
            List<PostMetadata> postsMetadata = metadataForCategory(bannerCategory.get());
            if (!postsMetadata.isEmpty()) {
                return postsMetadata.get(random.nextInt(postsMetadata.size()));
            }
        }
        return new PostMetadata();
    }

    private List<PostMetadata> metadataForCategory(Category category) {
        return category.getPostsSet().stream().map(post -> post.getMetadata()).filter(md -> md != null).collect(toList());
    }

    private List<Wrap> postsForCategory(Category category) {
        return category != null ? new PostsPresentationBean(category.getPostsSet()).getVisiblePosts(NUM_POSTS) : emptyList();
    }

}
