package cl.uandes.panel.servicio.crearGrupos.api.resources;

import java.util.Arrays;
import java.util.LinkedList;

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
 * Recibe y parte el proceso del request del servicio<br>
 * URI: cxf/ESB/panelv3/servicio/crearGrupos/<br><br>
 * Funciones<br>
 * <ul>
 * <li>documentacion: /help GET</li>
 * <li>proceso diario: /procese POST</li>
 * </ul>
 * @author fernando
 *
 */
@Path("/")
public class CrearGruposRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
    @PropertyInject(value = "crear-grupos-gmail.nocturno.funcion", defaultValue="generaGrupos")
	private String funcion;
    @PropertyInject(value = "crear-grupos-gmail.proceso", defaultValue="proceso")
	private String proceso;
    private String procesosValidos[] = {"crear_grupos", "grupos_inprogress", "grupos_inprogress_posgrado"};
	
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
		if (!valida(request)) {
			return Response.ok().status(401).entity("no indica un request valido").build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).
				withHeader("listaOperaciones", new LinkedList<String>(Arrays.asList(request.getOperaciones())))
				.withHeader("request", request)
				.withBody(request).build();
		
		logger.info(String.format("CrearCuentasRestService.procese: activa direct:proceso con header.request = %s", 
				exchange.getIn().getHeader("request")));
		procesoBatch.asyncSend("direct:proceso", exchange);
		
		Response response = Response.ok().status(200).entity("Partio crear_grupos").build();
		return response;
	}
	
	private boolean valida(ProcesoDiarioRequest req) {
		boolean valida = false;
		if (!req.getFuncion().equals(getFuncion()))
			return valida;
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
	
	public void setOperacionDesconocida(Exchange exchange) {
		throw new RuntimeException("Operacion desconocida");
	}
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getFuncion() {
		return funcion;
	}
	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}
}
