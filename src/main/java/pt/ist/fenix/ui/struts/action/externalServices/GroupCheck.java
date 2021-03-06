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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.service.services.exceptions.NonExistingServiceException;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenix.service.services.externalServices.GroupCheckService;

@Mapping(module = "external", path = "/groupCheck", scope = "request", parameter = "method")
public class GroupCheck extends ExternalInterfaceDispatchAction {

    private static final String NON_EXISTING_GROUP_CODE = "NON_EXISTING_GROUP";

    /**
     * Checks if the user belongs to the group specified in query.
     * 
     * The response has the following format: <RESPONSE_CODE>\n<BOOLEAN_VALUE>
     * 
     * 
     * 
     * <RESPONSE_CODE> = NON_EXISTING_GROUP | SUCESS | NOT_AUTHORIZED |
     * UNEXPECTED_ERROR
     * 
     * <BOOLEAN_VALUE> = true || false
     * 
     * 
     * 
     */
    public ActionForward check(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String query = request.getParameter("query");

        String responseMessage = "";
        String responseCode;

        try {
            Boolean result = GroupCheckService.run(query);

            responseMessage = result.toString().toLowerCase();
            responseCode = SUCCESS_CODE;
        } catch (NonExistingServiceException e) {
            responseCode = NON_EXISTING_GROUP_CODE;
        } catch (Exception e) {
            responseCode = UNEXPECTED_ERROR_CODE;
        }

        writeResponse(response, responseCode, responseMessage);

        return null;
    }

}