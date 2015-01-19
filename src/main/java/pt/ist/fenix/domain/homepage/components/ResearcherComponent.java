package pt.ist.fenix.domain.homepage.components;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentParameter;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.component.DynamicComponent;
import org.fenixedu.cms.rendering.TemplateContext;
import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenix.FenixIstConfiguration;
import pt.ist.fenix.domain.homepage.HomepageSite;

@ComponentType(name = "Researcher Section Data Component",
        description = "Provides homepage owner's researcher section page data.")
public class ResearcherComponent extends ResearcherComponent_Base {

    public ResearcherComponent(String titleKey, String dataKey) {
        this(titleKey, "resources.ResearcherResources", dataKey);
    }

    @DynamicComponent
    public ResearcherComponent(@ComponentParameter("Title Key") String titleKey,
            @ComponentParameter("Title Bundle") String titleBundle, @ComponentParameter("Data Key") String dataKey) {
        setTitleKey(titleKey);
        setDataKey(dataKey);
        setTitleBundle(titleBundle);
    }

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        global.put("bundle", getTitleBundle());
        global.put("researcher", ((HomepageSite) page.getSite()).getOwner().getUsername());
        global.put("sotisUrl", FenixIstConfiguration.getConfiguration().sotisURL());
        global.put("language", I18N.getLocale().toLanguageTag());
        global.put("dataKey", getDataKey());
        global.put("titleKey", getTitleKey());
    }

    public static boolean supportsSite(Site site) {
        return site instanceof HomepageSite;
    }

}
