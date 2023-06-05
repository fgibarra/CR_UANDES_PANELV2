package cl.uandes.panel.apiEliminaSuspendeCuentaServices.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaRequest;
import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaResponse;

@Path("/")
public class EliminaSuspendeRestService {

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
    @Path("/cambiaCuenta")
	public EliminaSuspendeCuentaResponse procese(EliminaSuspendeCuentaRequest in_msg) {
		Map<String,Object> headers = new HashMap<String,Object>();
		String funcion = in_msg.getFuncion();
		String msg = null;
		if (funcion == null) {
			funcion ="error";
			msg = "No viene una funcion definida en el request";
		} else {
			if ("reactivar".equalsIgnoreCase(funcion)) {
				invocaRuta("seda:reactivaCuentas", in_msg);
			} else if ("suspende_elimina".equalsIgnoreCase(funcion)) {
				invocaRuta("seda:procesaSuspendeElimina", in_msg);
			} else if ("recrea_suspendidos".equalsIgnoreCase(funcion)) {
				invocaRuta("seda:procesaRecreaSuspendidos", in_msg);
			} else {
				msg = String.format("Funcion %s definida en el request no esta implementada", funcion);
				funcion = "error";
			}
		}
		headers.put("funcion", funcion);
		if (msg != null)
			headers.put("msgError", msg);
		return (EliminaSuspendeCuentaResponse)producer.requestBodyAndHeaders(in_msg, headers);
	}

	private void invocaRuta(String ruta, EliminaSuspendeCuentaRequest in_msg) {
		CamelContext camelContext = producer.getCamelContext();
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("EliminaSuspendeCuentaRequest", in_msg)
				.withBody(in_msg).build();
		procesoBatch.asyncSend(ruta, exchange);
	}
}
