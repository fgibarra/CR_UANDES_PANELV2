package cl.uandes.panel.paraGui.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.uandes.panel.paraGui.json.ActualizaConsumoPanelRequest;

@Path("/")
public interface WebappUandesEndpoint {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/th_panel/1/")
	public Response retrieveAllUser(ActualizaConsumoPanelRequest in_msg);
}
