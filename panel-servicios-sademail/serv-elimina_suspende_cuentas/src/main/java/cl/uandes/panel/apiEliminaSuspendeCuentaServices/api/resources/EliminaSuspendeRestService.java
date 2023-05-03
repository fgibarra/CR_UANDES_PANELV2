package cl.uandes.panel.apiEliminaSuspendeCuentaServices.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaRequest;
import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaResponse;

@Path("/")
public class EliminaSuspendeRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/cambiaCuenta")
	public EliminaSuspendeCuentaResponse procese(EliminaSuspendeCuentaRequest in_msg) {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("EliminaSuspendeCuentaRequest", in_msg);

		return (EliminaSuspendeCuentaResponse)producer.requestBodyAndHeaders(in_msg, headers);
	}
}
