package pt.ist.fenix.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import pt.ist.fenix.FenixIstConfiguration;

@Path("/fenix/v1/parking")
public class FenixParking {

    private static final Client client = ClientBuilder.newClient();

    /**
     * get information about parking
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String parking() {
        try {
            return client.target(FenixIstConfiguration.getConfiguration().getApiParkingUrl())
                    .queryParam("username", FenixIstConfiguration.getConfiguration().getApiParkingUsername())
                    .queryParam("password", FenixIstConfiguration.getConfiguration().getApiParkingPassword()).request()
                    .get(String.class);
        } catch (Exception e) {
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }
    }
}