package cl.uandes.panel.servicio.crearCuentas.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;

/**
 * Definicion de los Endpoints del servicio cxf/ESB/panel/serviciosAD
 * @author fernando
 *
 */
@Path("/")
public interface ConsultaADEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/consultaXrut")
	public ConsultaXrutResponse consultaXrut(ConsultaXrutRequest request);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/crearUsuario")
	public ServiciosLDAPResponse crearUsuario(ServiciosLDAPRequest request);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/validarUsuario")
	public ServiciosLDAPResponse validarUsuario(ServiciosLDAPRequest request);	
}
