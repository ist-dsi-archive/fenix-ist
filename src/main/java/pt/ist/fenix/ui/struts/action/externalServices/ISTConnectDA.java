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
package pt.ist.fenix.ui.struts.action.externalServices;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pt.ist.fenix.FenixIstConfiguration;
import pt.ist.fenix.domain.LegacyRoleUtils;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Mapping(module = "external", path = "/connect", scope = "request", parameter = "method")
public class ISTConnectDA extends ExternalInterfaceDispatchAction {

    private boolean doLogin(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {
        final String username = (String) getFromRequest(request, "username");
        final String password = (String) getFromRequest(request, "password");
        final String usernameProp = FenixIstConfiguration.getConfiguration().getExternalServicesISTConnectUsername();
        final String passwordProp = FenixIstConfiguration.getConfiguration().getExternalServicesISTConnectPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(usernameProp)
                || StringUtils.isEmpty(passwordProp)) {
            return false;
        }
        return username.equals(usernameProp) && password.equals(passwordProp);
    }

    public ActionForward getPerson(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (doLogin(mapping, actionForm, request, response)) {
            final String istID = (String) getFromRequest(request, "istID");
            final Person person = Person.readPersonByUsername(istID);
            final JSONObject jsonObject = DomainObjectJSONSerializer.getDomainObject(person);
            writeJSONObject(response, jsonObject);
        } else {
            response.sendError(404, "Not authorized");
        }
        return null;
    }

    /**
     * Known usages: IST official site to collect role information and Barra to collect role information and names
     */
    public ActionForward getBasicUserData(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (doLogin(mapping, actionForm, request, response)) {
            final String istID = (String) getFromRequest(request, "istID");
            UserProfile profile = User.findByUsername(istID).getProfile();

            final JsonObject jsonObject = new JsonObject();

            if (profile != null) {
                // probably not used:
                jsonObject.addProperty("externalId", profile.getPerson().getExternalId());
                jsonObject.addProperty("className", Person.class.getName());

                // probably used
                jsonObject.addProperty("email", profile.getEmail());
                jsonObject.addProperty("partyName", profile.getFullName());
                jsonObject.addProperty("nickname", profile.getDisplayName());

                JsonArray roles = new JsonArray();
                for (String role : LegacyRoleUtils.mainRoleKeys(profile.getUser())) {
                    roles.add(new JsonPrimitive(role));
                }
                jsonObject.add("roles", roles);
            }
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(new Gson().toJson(jsonObject).getBytes());
            }
        } else {
            response.sendError(404, "Not authorized");
        }
        return null;
    }

    private void writeJSONObject(HttpServletResponse response, final JSONArray jsonObject) throws IOException {
        final ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonObject.toJSONString().getBytes());
        outputStream.close();
    }

    public ActionForward getExternalIds(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String externalIds = (String) getFromRequest(request, "externalIds");
        if (doLogin(mapping, actionForm, request, response)) {
            JSONParser parser = new JSONParser();
            final JSONArray extIdsJSONArray = (JSONArray) parser.parse(externalIds);
            final JSONArray jsonArrayResult = new JSONArray();
            for (Object externalId : extIdsJSONArray) {
                jsonArrayResult.add(DomainObjectJSONSerializer.getDomainObject(FenixFramework
                        .getDomainObject((String) externalId)));
            }
            writeJSONObject(response, jsonArrayResult);
        } else {
            response.sendError(404, "Not authorized");
        }
        return null;
    }
}
