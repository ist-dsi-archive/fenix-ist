package pt.ist.fenix.servlet;

import org.fenixedu.academic.domain.Person;
import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixframework.FenixFramework;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by borgez on 20-02-2015.
 */
@WebListener public class FenixISTLearningContextListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent sce) {
        FenixFramework.getDomainModel().registerDeletionListener(Person.class, (person) -> {
            HomepageSite homepageSite = person.getHomepage();
            if (homepageSite != null) {
                person.setHomepage(null);
                homepageSite.delete();
            }
        });
    }

    @Override public void contextDestroyed(ServletContextEvent sce) {

    }
}
