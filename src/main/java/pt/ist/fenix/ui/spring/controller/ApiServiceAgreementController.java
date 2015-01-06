package pt.ist.fenix.ui.spring.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLayoutInjector;
import org.fenixedu.bennu.spring.I18NBean;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

@SpringApplication(group = "!#developers", path = "api-service-agreement", title = "api.service.agreement.title")
@SpringFunctionality(app = ApiServiceAgreementController.class, title = "api.service.agreement.title")
@Controller
@RequestMapping("/api-service-agreement")
public class ApiServiceAgreementController {

    @Autowired
    I18NBean i18nBean;

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("serviceAgreement", getServiceAgreementHtml());
        return "fenix-ist/public/agreeServiceAgreement";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String agree(Model model, @RequestParam Boolean agreedServiceAgreement) {
        if (agreedServiceAgreement) {
            addDeveloperRole(Authenticate.getUser());
            return "redirect:/personal";
        }
        return "redirect:/api-service-agreement";
    }

    @Atomic
    private void addDeveloperRole(User user) {
        DynamicGroup.get("developers").mutator().grant(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "show")
    public String showServiceAgreementHtml(Model model, HttpServletRequest request) {
        String serviceAgreementHtml = getServiceAgreementHtml();
        model.addAttribute("serviceAgreement", serviceAgreementHtml);
        model.addAttribute("serviceAgreementChecksum", Hashing.md5().newHasher().putString(serviceAgreementHtml, Charsets.UTF_8)
                .hash().toString());
        PortalLayoutInjector.skipLayoutOn(request);
        return "fenix-ist/public/showServiceAgreement";
    }

    private String getServiceAgreementHtml() {
        final InputStream resourceAsStream = getClass().getResourceAsStream("/api/serviceAgreement.html");
        if (resourceAsStream == null) {
            return i18nBean.message("oauthapps.default.service.agreement");
        }
        try {
            return new String(ByteStreams.toByteArray(resourceAsStream));
        } catch (IOException e) {
            return i18nBean.message("oauthapps.default.service.agreement");
        }
    }

}
