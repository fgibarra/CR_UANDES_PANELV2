package cl.uandes.panel.apiEliminaSuspendeCuentaServices.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;

@Path("/")
public interface ConsultaGMailEndpoint {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieve/{username}")
	public UserResponse retrieveUser(@PathParam("username")String in_msg);
}
