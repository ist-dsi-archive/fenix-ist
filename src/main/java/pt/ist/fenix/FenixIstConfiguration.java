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
package pt.ist.fenix;

import java.util.Map;

import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

import pt.ist.fenix.util.HostAccessControl;

public class FenixIstConfiguration {
    @ConfigurationManager(description = "Fenix IST specific properties")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "barra.as.authentication.broker",
                description = "CAS ticket validation through barra: https://fenix-ashes.ist.utl.pt/fenixWiki/Barra",
                defaultValue = "false")
        public Boolean barraAsAuthenticationBroker();

        @ConfigurationProperty(key = "barra.loginUrl",
                description = "Login URL to use when barra is set as authentication broker")
        public String barraLoginUrl();

        @ConfigurationProperty(key = "ciistCostCenterCode", description = "Deprecated, to be removed", defaultValue = "8431")
        public Integer getCIISTCostCenterCode();

        @ConfigurationProperty(key = "externalServices.ISTConnect.password")
        public String getExternalServicesISTConnectPassword();

        @ConfigurationProperty(key = "externalServices.ISTConnect.username")
        public String getExternalServicesISTConnectUsername();

        @ConfigurationProperty(key = "externalServices.koha.password")
        public String getExternalServicesKohaPassword();

        @ConfigurationProperty(key = "externalServices.koha.username")
        public String getExternalServicesKohaUsername();

        @Deprecated
        @ConfigurationProperty(
                key = "host.control.name.*",
                description = "Comma separated hostname values that are allowed to access the url host.control.name. See HostAccessControl.isAllowed(name, request)")
        public Map<String, String> getHostControlName();

        @ConfigurationProperty(
                key = "sibs.destinationInstitutionId",
                description = "used in header payments file and represents entity service identification (i.e. sibs). Default value '50000000' (sibs identification)",
                defaultValue = "50000000")
        public String getSibsDestinationInstitutionId();

        @ConfigurationProperty(
                key = "sibs.sourceInstitutionId",
                description = "used in header payments file, and represents institution identification accordding to transfer service (i.e. sibs). Must be given by the entity that is peforming this service. Format: '9XXXXXXX'",
                defaultValue = "11111111")
        public String getSibsSourceInstitutionId();

        @ConfigurationProperty(key = "ldap.sync.services.password")
        public String ldapSyncServicesPassword();

        @ConfigurationProperty(key = "ldap.sync.services.username")
        public String ldapSyncServicesUsername();

        @ConfigurationProperty(key = "legacyFilesRedirectMapLocation", defaultValue = "")
        public String legacyFilesRedirectMapLocation();

        @ConfigurationProperty(key = "sotisURL", defaultValue = "https://sotis.tecnico.ulisboa.pt")
        public String sotisURL();

        @ConfigurationProperty(key = "merge.units.emails",
                description = "comma separated emails of persons who want to receive emails about merge of units.")
        public String getMergeUnitsEmails();

        @ConfigurationProperty(key = "store.quota.warning.email",
                description = "The email address to send notifications about the AFS store quotas.")
        public String getStoreQuotaWarningEmail();
    }

    public static boolean barraLogin() {
        return barraLogin && "ashes-ist".equals(PortalConfiguration.getInstance().getTheme());
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

    @Deprecated
    public static HostAccessControl getHostAccessControl() {
        return hostAccessControl;
    }

    public static void setBarraLogin(boolean state) {
        barraLogin = state;
    }

    @Deprecated
    private static HostAccessControl hostAccessControl = new HostAccessControl(getConfiguration().getHostControlName());

    private static boolean barraLogin = getConfiguration().barraAsAuthenticationBroker();

}
