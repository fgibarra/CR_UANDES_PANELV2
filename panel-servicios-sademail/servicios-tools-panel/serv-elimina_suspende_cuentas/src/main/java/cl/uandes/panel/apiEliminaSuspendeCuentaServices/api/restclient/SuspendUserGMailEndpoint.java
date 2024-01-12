package cl.uandes.panel.apiEliminaSuspendeCuentaServices.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface SuspendUserGMailEndpoint {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/suspend/{username}")
	public Response suspendUser(@PathParam("username")String in_msg);
}
