package cl.uandes.panel.servicio.crearCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;

@Path("/")
public interface ServiciosPanelEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/creaCuenta")
	public CreaCuentaResponse creaCuenta(CreaCuentaRequest request);
}
