package cl.uandes.panel.servicio.suspendeEliminaCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.sadmemail.comunes.report.json.ReportResponse;

@Path("/")
public interface ReporteEndpoint {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/report/usage/{userId}")
	public ReportResponse retrieveUser(@PathParam("userId")String in_msg);
}
