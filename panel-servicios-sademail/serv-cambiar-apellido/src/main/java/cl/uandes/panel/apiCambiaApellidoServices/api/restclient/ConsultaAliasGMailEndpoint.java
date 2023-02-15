package cl.uandes.panel.apiCambiaApellidoServices.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.sadmemail.comunes.gmail.json.AliasesResponse;

@Path("/")
public interface ConsultaAliasGMailEndpoint {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickName/retrieve/{username}")
	public AliasesResponse retrieveNicknames(@PathParam("username")String in_msg);
}
