package cl.uandes.panel.servicio.suspendeEliminaCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.uandes.sadmemail.comunes.gmail.json.AllUsersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.AllUsersResponse;

@Path("/")
public interface GmailEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieveAllUsers")
	public AllUsersResponse retrieveAllUser(AllUsersRequest in_msg);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/suspend/{username}")
	public Response suspendUser(@PathParam("username")String in_msg);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/reactivar/{username}")
	public Response reactivarUser(@PathParam("username")String in_msg);

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/delete/{username}")
	public Response deleteUser(@PathParam("username")String in_msg);
}
