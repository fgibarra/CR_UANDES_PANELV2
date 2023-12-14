package cl.uandes.panel.servicio.scheduler.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;

@Path("/")
public interface ServicioSuspendeEliminaCuentasEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/procese")
	public Response procese(ProcesoDiarioRequest request);
	
}
