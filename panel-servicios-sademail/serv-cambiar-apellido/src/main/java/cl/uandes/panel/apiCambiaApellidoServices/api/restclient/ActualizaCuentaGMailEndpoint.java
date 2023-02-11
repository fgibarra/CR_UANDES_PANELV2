package cl.uandes.panel.apiCambiaApellidoServices.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;

@Path("/")
public interface ActualizaCuentaGMailEndpoint {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/update")
	public UserResponse updateUser(UserRequest in_msg);

}
