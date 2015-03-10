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
package pt.ist.fenix.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.academic.domain.AlumniIdentityCheckRequest;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.FirstTimeCandidacyStage;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.service.services.candidacy.LogFirstTimeCandidacyTimestamp;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.idcards.ui.candidacydocfiller.CGDPdfFiller;

import pt.ist.fenix.FenixIstConfiguration;
import pt.ist.fenix.dto.PersonInformationDTO;
import pt.ist.fenix.dto.PersonInformationFromUniqueCardDTO;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;

@Path("/fenix-ist/ldapSync")
public class LdapSyncServices extends BennuRestResource {

    @Context
    private HttpServletRequest request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/person/{username}")
    public Response getPersonInformation(@PathParam("username") String username) {
        checkAccessControl();
        User user = User.findByUsername(username);
        if (user == null || user.getPerson() == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return Response.ok(gson.toJson(new PersonInformationDTO(user.getPerson()))).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/person")
    public Response updatePersonInformation(String json) {
        checkAccessControl();
        PersonInformationFromUniqueCardDTO personDTO = new Gson().fromJson(json, PersonInformationFromUniqueCardDTO.class);
        Collection<Person> persons = Person.readByDocumentIdNumber(personDTO.getDocumentIdNumber());
        if (persons.isEmpty() || persons.size() > 1) {
            return Response.serverError().build();
        }

        Person person = persons.iterator().next();
        if (person.getIdDocumentType() != IDDocumentType.IDENTITY_CARD) {
            return Response.serverError().build();
        }

        try {
            personDTO.edit(person);
        } catch (ParseException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/alumni/{requestOID}/{requestUUID}")
    public Response alumniIdentityCheck(@PathParam("requestOID") String requestOID, @PathParam("requestUUID") String requestUUID) {
        checkAccessControl();
        AlumniIdentityCheckRequest identityCheckRequest = FenixFramework.getDomainObject(requestOID);
        if (identityCheckRequest.getRequestToken().equals(UUID.fromString(requestUUID))) {
            JsonObject obj = new JsonObject();
            obj.addProperty("username", identityCheckRequest.getAlumni().getLoginUsername());
            return Response.ok(toJson(obj)).build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/candidacy-summary-file/{user}")
    public Response getCandidacySummaryFile(@PathParam("user") String username) {
        checkAccessControl();
        final User foundUser = User.findByUsername(username);
        final StudentCandidacy candidacy =
                foundUser.getPerson().getStudent().getRegistrationsSet().iterator().next().getStudentCandidacy();
        final CandidacySummaryFile file = candidacy.getSummaryFile();

        if (file == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        LogFirstTimeCandidacyTimestamp.logTimestamp(candidacy, FirstTimeCandidacyStage.RETRIEVED_SUMMARY_PDF);
        return Response.ok(file.getContent()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/cgd-form/{user}")
    public Response getCGDPersonalFormFile(@PathParam("user") String username) {
        checkAccessControl();
        final User foundUser = User.findByUsername(username);
        if (foundUser == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final Person person = foundUser.getPerson();
        final CGDPdfFiller pdfFiller = new CGDPdfFiller();

        ByteArrayOutputStream file;
        try {
            file = pdfFiller.getFilledPdf(person);
            return Response.ok(file.toByteArray()).build();
        } catch (IOException | DocumentException e) {
            return Response.serverError().build();
        }
    }

    private void checkAccessControl() {
        boolean authorized =
                Objects.equals(request.getHeader("__username__"), FenixIstConfiguration.getConfiguration()
                        .ldapSyncServicesUsername());
        authorized &=
                Objects.equals(request.getHeader("__password__"), FenixIstConfiguration.getConfiguration()
                        .ldapSyncServicesPassword());
        if (!authorized) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }

    /***
     * <p>
     * Set user institutional email address.
     * </p>
     * 
     * <p>
     * Request example :
     * <p>
     * 
     * <pre>
     * curl -X POST -H '__username__: user' -H '__password__: pass' --data "email=user1@fenixedu.org" /api/fenix-ist/ldapSync/setEmail/user1
     * </pre>
     * 
     * @param username the username to set the email to
     * @param email the new email value
     * @return {@link Status.OK} if successful, {@link Status.NOT_FOUND} otherwise
     */
    @POST
    @Path("/setEmail/{username}")
    public Response userEmail(@PathParam("username") String username, @FormParam("email") String email) {
        checkAccessControl();
        return Response.status(setEmail(username, email)).build();
    }

    @Atomic
    public Status setEmail(String username, String email) {
        final User foundUser = User.findByUsername(username);
        if (Strings.isNullOrEmpty(email) || foundUser == null || foundUser.getPerson() == null) {
            return Status.NOT_FOUND;
        }
        foundUser.getPerson().setInstitutionalEmailAddressValue(email);
        return Status.OK;
    }
}
