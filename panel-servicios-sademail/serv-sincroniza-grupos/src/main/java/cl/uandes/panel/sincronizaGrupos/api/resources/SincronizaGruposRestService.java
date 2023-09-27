package cl.uandes.panel.sincronizaGrupos.api.resources;

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

import cl.uandes.panel.comunes.json.sincronizaGrupos.SincronizaGruposRequest;
import cl.uandes.panel.comunes.json.sincronizaGrupos.SincronizaGruposResponse;

@Path("/")
public class SincronizaGruposRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());
	private static String opValidas[] = {"procesar", "reprocesar"};
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
    @Path("/sincronizaGrupos")
	public SincronizaGruposResponse procese(SincronizaGruposRequest request) {
		if (request.getOperacion() == null)
			request.setOperacion(opValidas[1]);
		else {
			String op = request.getOperacion();
			boolean valida = false;
			for (String opv : opValidas) {
				if (op.equalsIgnoreCase(opv)) {
					valida = true;
					break;
				}
			}
			if (!valida)
				return new SincronizaGruposResponse(-1, String.format("operacion %s no es una operación válida", op));
		}
		return (SincronizaGruposResponse)producer.requestBody(request);
	}
}
