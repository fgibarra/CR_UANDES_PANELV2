package cl.uandes.panel.tools.actualizaCuentasAD.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.log4j.Logger;

import cl.uandes.panel.tools.actualizaCuentasAD.api.json.ActualizaCuentasADRequest;
import cl.uandes.panel.tools.actualizaCuentasAD.api.json.OperacionXFecha;

@Path("/")
public class ActualizaCuentasADRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

    private String msgError;
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
    @Path("/pregrado")
	public Response procesePregrado(ActualizaCuentasADRequest request) {
		logger.info(String.format("ActualizaCuentasADRestService: request [%s]", request));
		if (!validaPregrado(request)) {
			return Response.ok().status(401).entity(getMsgError()).build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("request", request).
				withBody(request).build();
		logger.info(String.format("ActualizaCuentasADRestService.procese: activa seda:procesaPregrado con header.request = %s", 
				exchange.getIn().getHeader("request")));
		procesoBatch.asyncSend("seda:procesaPregrado", exchange);
		
		Response response = Response.ok().status(200).entity("Partio actualizar_cuentas").build();
		return response;
	}

	private boolean validaPregrado(ActualizaCuentasADRequest request) {
		boolean valida = false;
		
		if (request.getOperacion().equalsIgnoreCase("pregrado"))
			valida = true;
		
		return valida;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/procese")
	public Response procesePosgrado(ActualizaCuentasADRequest request) {
		logger.info(String.format("ActualizaCuentasADRestService: request [%s]", request));
		if (!validaPosgrado(request.getDatos())) {
			return Response.ok().status(401).entity(getMsgError()).build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("request", request).
				withBody(request).build();
		logger.info(String.format("ActualizaCuentasADRestService.procese: activa seda:procesaActualizacionCuentas con header.request = %s", 
				exchange.getIn().getHeader("request")));
		procesoBatch.asyncSend("seda:procesaActualizacionCuentas", exchange);
		
		Response response = Response.ok().status(200).entity("Partio actualizar_cuentas").build();
		return response;
	}
/*
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/parearAD")
	public Response parearAD(ActualizaCuentasADRequest request) {
		logger.info(String.format("ActualizaCuentasADRestService: parearAD request [%s]", request));
		if (!valida(request)) {
			return Response.ok().status(401).entity(getMsgError()).build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("request", request).
				withBody(request).build();
		logger.info(String.format("ActualizaCuentasADRestService.procese: activa seda:pareaCuentasAD con header.request = %s", 
				exchange.getIn().getHeader("request")));
		procesoBatch.asyncSend("seda:pareaCuentasAD", exchange);
		
		Response response = Response.ok().status(200).entity("Partio pareao_cuentas").build();
		return response;
	}
*/
	private boolean validaPosgrado(OperacionXFecha request) {
		boolean valida = false;
		if (request != null) {
			if (request.getFechaDesde() == null) {
				setMsgError("fecha_desde no puedes ser null");
				return valida;
			}
			if (request.getFechaHasta() == null) {
				setMsgError("fecha_hasta no puedes ser null");
				return valida;
			}
			if (request.getMaxResultados() == null) {
				setMsgError("max_resultados no puedes ser null");
				return valida;
			}
			
			valida = true;
		}
		return valida;
	}

	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}
}
