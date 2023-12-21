package cl.uandes.panel.apiActualizaIdGmailServices.scheduler.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.panel.comunes.json.updateidgmail.UpdateIdGmailResponse;

public interface ServicioActualizaIdGmailEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	@Path("/procesar")

	public UpdateIdGmailResponse procese();
}
