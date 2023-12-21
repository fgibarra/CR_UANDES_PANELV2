package cl.uandes.panel.apiCrearCuentaServices.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;

@Path("/")
public class CrearCuentaRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/help")
	public Response getDocumentacion() {
		String type = "application/pdf";
		java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("documentacion.pdf");		
		Response response = Response.ok(is, type).build();
		return response;
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/creaCuenta")
	public CreaCuentaResponse creaCuenta(CreaCuentaRequest request) {
		CreaCuentaResponse response = valida(request);
		if (response == null) {
			Map<String,Object> headers = new HashMap<String,Object>();
			headers.put("en_banner", request.getEnBanner());
			headers.put("request", request);
			headers.put("cuentaporciento", String.format("%s%c", request.getCuenta(),'%'));
			logger.info(String.format("CrearCuentaRestService: request: %s", request));
			return (CreaCuentaResponse)producer.requestBodyAndHeaders(request, headers);
		} else {
			return response;
		}
	}

	private CreaCuentaResponse valida(CreaCuentaRequest request) {
		if (request.getEnBanner()) {
			if (request.getRut() == null) {
				return new CreaCuentaResponse(-1, "Debe indicar un rut", null, null, null, null, null, null);
			}
		} else {
			if (request.getCuenta() == null || request.getNombreCuenta() == null || request.getApellidos() == null || request.getPassword() == null) {
				return new CreaCuentaResponse(-1, "Debe indicar cuenta, nombres, apéllidos y password", null, null, null, null, null, null);
			}
		}
		return null;
	}
}
