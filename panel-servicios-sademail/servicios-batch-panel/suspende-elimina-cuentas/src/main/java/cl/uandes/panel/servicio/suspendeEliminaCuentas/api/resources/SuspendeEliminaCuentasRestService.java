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

/**
 * Recibe el JSON y comienza el proceso
 * @author fernando
 *
 */
@Path("/")
public class SuspendeEliminaCuentasRestService {
	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

    @PropertyInject(value = "se-cuentas-gmail.nocturno.funciones", defaultValue="sincronizar,eliminar,suspender")
	private String funciones;
    @PropertyInject(value = "se-cuentas-gmail.tiposCuenta", defaultValue="Alumnos,Profesores,Otros")
	private String tiposCuenta;

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
		if ("suspender".equals(request.getFuncion()))
			procesoBatch.asyncSend("direct:proceso", exchange);
		else if ("eliminar".equals(request.getFuncion()))
			procesoBatch.asyncSend("direct:proceso", exchange);
		else if ("sincronizar".equals(request.getFuncion()))
			procesoBatch.asyncSend("direct:sincronizar", exchange);
		
		Response response = Response.ok().status(200).entity(String.format("Partio %s",request.getFuncion())).build();
		return response;
	}

	private boolean valida(ProcesoDiarioRequest req) {
		String funcionesValidas[] = getFunciones().split(",");
		String procesosValidos[] = getTiposCuenta().split(",");
		boolean valida = false;
		for (String valor : funcionesValidas) {
			if (!req.getFuncion().equals(valor)) {
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
		if (valida) {
			// las combinaciones
			if (req.getFuncion().equals(funcionesValidas[0])) {
				if (req.getOperaciones()[0].equals(procesosValidos[1])) {
					setMsgError("No se pueden eliminar profesores");
					valida = false;
				}
			}
		} else {
			setMsgError(String.format("operacion %s no es valida", req.getOperaciones()[0]));
		}
		return valida;
	}

	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public String getTiposCuenta() {
		return tiposCuenta;
	}

	public void setTiposCuenta(String tiposCuenta) {
		this.tiposCuenta = tiposCuenta;
	}

	public String getFunciones() {
		return funciones;
	}

	public void setFunciones(String funciones) {
		this.funciones = funciones;
	}

}
