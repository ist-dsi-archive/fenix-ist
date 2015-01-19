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
package pt.ist.fenix.service.filters;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.core.util.CoreConfiguration.CasConfig;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet.LocalLoginStrategy;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet.PortalLoginStrategy;

import pt.ist.fenix.FenixIstConfiguration;

import com.google.common.base.Strings;

@WebListener
public class FenixIstLoginConfigurer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (CoreConfiguration.casConfig().isCasEnabled()) {
            PortalLoginServlet.setLoginStrategy(new FenixIstCasStrategy());
        } else {
            PortalLoginServlet.setLoginStrategy(new FenixIstLocalLoginStrategy());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private static class FenixIstLocalLoginStrategy extends LocalLoginStrategy {
        @Override
        public void showLoginPage(HttpServletRequest req, HttpServletResponse resp, String callback) throws IOException,
                ServletException {
            if (Strings.isNullOrEmpty(callback)) {
                callback = CoreConfiguration.getConfiguration().applicationUrl() + "/login.do";
            }
            super.showLoginPage(req, resp, callback);
        }
    }

    private static class FenixIstCasStrategy implements PortalLoginStrategy {

        @Override
        public void showLoginPage(HttpServletRequest req, HttpServletResponse resp, String callback) throws IOException,
                ServletException {
            if (Authenticate.isLogged()) {
                resp.sendRedirect(req.getContextPath() + "/login.do");
            } else {
                CasConfig casConfig = CoreConfiguration.casConfig();
                if (Strings.isNullOrEmpty(callback)) {
                    callback = casConfig.getCasServiceUrl();
                }
                String casLoginUrl = casConfig.getCasLoginUrl(callback);
                if (FenixIstConfiguration.barraLogin()) {
                    casLoginUrl = FenixIstConfiguration.getConfiguration().barraLoginUrl() + "?next=" + casLoginUrl;
                }
                resp.sendRedirect(casLoginUrl);
            }
        }
    }

}
