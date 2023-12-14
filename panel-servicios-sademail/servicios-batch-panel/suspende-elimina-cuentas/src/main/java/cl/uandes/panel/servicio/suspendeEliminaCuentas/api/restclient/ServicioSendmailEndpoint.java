package cl.uandes.panel.servicio.suspendeEliminaCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.panel.comunes.json.sendmail.SendmailRequest;
import cl.uandes.panel.comunes.json.sendmail.SendmailResponse;


@Path("/")
public interface ServicioSendmailEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/send")
	public SendmailResponse sendmail(SendmailRequest request);
}
