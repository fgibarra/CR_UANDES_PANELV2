package cl.uandes.panel.apiCambiaApellidoServices.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.mail.json.SendmailRequest;

public interface SendMailEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON+"; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/send")
	
	public Object sendMail(SendmailRequest request);
}
