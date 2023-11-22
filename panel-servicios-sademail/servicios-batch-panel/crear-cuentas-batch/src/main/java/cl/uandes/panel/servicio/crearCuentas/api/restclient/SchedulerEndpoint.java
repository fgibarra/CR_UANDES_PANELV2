package cl.uandes.panel.servicio.crearCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;

/**
 * Definicion de los Endpoints del servicio ESB/panel/servicio/schedulerPanel
 * @author fernando
 *
 */
@Path("/")
public interface SchedulerEndpoint {

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN+"; charset=UTF-8")
    @Path("/finProceso")
	public Response getFinProceso(SchedulerPanelRequest request);
}
