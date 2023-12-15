package cl.uandes.panel.servicio.suspendeEliminaCuentas.api.resources;

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
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

/**
 * Recibe el JSON y comienza el proceso
 * @author fernando
 *
 */
@Path("/")
public class SuspendeEliminaCuentasRestService {
	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

    @PropertyInject(value = "se-cuentas-gmail.nocturno.funciones", defaultValue="suspender_eliminar_cuentas")
	private String funcion;
    @PropertyInject(value = "se-cuentas-gmail.operaciones", defaultValue="sincronizar,suspender,eliminar")
	private String operaciones;

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
    @Path("/procese")
	public Response procese(ProcesoDiarioRequest request) {
		logger.info(String.format("SuspendeEliminaCuentasRestService: request |%s|", request));
		if (!valida(request)) {
			return Response.ok().status(401).entity(getMsgError()).build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("request", request)
				.withHeader("hayToken", Boolean.TRUE)
				.withHeader("proceso", request.getFuncion())
				.withBody(request).build();
		logger.info(String.format("SuspendeEliminaCuentasRestService.procese: activa direct:proceso/direct:sincronizar con header.request = %s", 
				exchange.getIn().getHeader("request")));
		if (StringUtils.estaContenido("suspender",request.getOperaciones()))
			procesoBatch.asyncSend("direct:sincronizar", exchange);
		else if (StringUtils.estaContenido("eliminar",request.getOperaciones()))
			procesoBatch.asyncSend("direct:sincronizar", exchange);
		else if (StringUtils.estaContenido("sincronizar",request.getOperaciones()))
			procesoBatch.asyncSend("direct:sincronizar", exchange);
		
		Response response = Response.ok().status(200).entity(String.format("Partio %s",request.getFuncion())).build();
		return response;
	}

	private boolean valida(ProcesoDiarioRequest req) {
		String funcionesValidas[] = getFuncion().split(",");
		String procesosValidos[] = getOperaciones().split(",");
		boolean valida = false;
		for (String valor : funcionesValidas) {
			if (req.getFuncion().equals(valor)) {
				valida = true;
				break;
			}
		}
		logger.info(String.format("funcion %s es valida %b", req.getFuncion(), valida));
		if (!valida) {
			setMsgError(String.format("funcion %s no es valida", req.getFuncion()));
			return false;
		}
		
		for (String operacion : req.getOperaciones()) {
			logger.info(String.format("valida operacion: %s", operacion));
			boolean found = false;
			for (String valido: procesosValidos) {
				if (operacion.equalsIgnoreCase(valido)) {
					found = true;
					break;
				}
			}
			if (found)
				valida = true;
			else
				valida = false;
			logger.info(String.format("operacion %s es valida %b", operacion, valida));
		}
		return valida;
	}

	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public synchronized String getFuncion() {
		return funcion;
	}

	public synchronized void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public synchronized String getOperaciones() {
		return operaciones;
	}

	public synchronized void setOperaciones(String operaciones) {
		this.operaciones = operaciones;
	}

}
