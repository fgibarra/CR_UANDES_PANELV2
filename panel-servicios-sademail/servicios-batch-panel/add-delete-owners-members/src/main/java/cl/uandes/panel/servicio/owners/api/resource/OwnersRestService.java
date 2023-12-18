package cl.uandes.panel.servicio.owners.api.resource;

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
 * Recibe el json con que se invoca. 1.- valida json con datos correctos 2.- si
 * el proceso es:
 * <ul>
 * <li>sincronizar - invoca la ruta direct:procesoSincronizar</li>
 * <li>procesoAE - invoca la ruta direct:procesoAE</li>
 * </ul>
 * Responde que el proceso batch se ha inicializado
 * 
 * @author fernando ibarra
 *
 */
public class OwnersRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
	@PropertyInject(value = "ae-owners-members-grupo.nocturno.kco-funcion", defaultValue = "asignar_owners")
	private String funcionOwners;
	@PropertyInject(value = "ae-owners-members-grupo.procesos", defaultValue = "sincroniza,procesoAE")
	private String procesos;
	public static String procesosValidos[];

	Logger logger = Logger.getLogger(getClass());

	public OwnersRestService(String procesos) {
		super();
		procesosValidos = procesos.split(",");
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/help")
	public Response getDocumentacion() {
		String type = "application/pdf";
		java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("documentacion.pdf");
		Response response = Response.ok(is, type).build();
		return response;

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/procese")
	public Response procese(ProcesoDiarioRequest request) {

		if (!valida(request)) {
			return Response.ok().status(401).entity("no indica un request valido").build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext)
				.withHeader("proceso", request.getFuncion()) // kco_funcion para inicializar
				.withHeader("request", request).withBody(request)
				.build();

		String uri = "";
		if (request.getFuncion().equals(getFuncionEA()))
			uri = "direct:procesoAE";
		else
			uri = "direct:procesoSincronizar";

		logger.info(String.format("CrearGruposRestService.procese: %s proceso con funcion=%s header.request = %s", uri,
				getFuncionEA(), exchange.getIn().getHeader("request")));

		procesoBatch.asyncSend(uri, exchange);

		Response response = Response.ok().status(200).entity(String.format("Partio %s", funcionOwners)).build();
		return response;
	}

	@SuppressWarnings("unused")
	private String getFuncionSincroniza() {
		return procesosValidos[0];
	}

	private String getFuncionEA() {
		return procesosValidos[1];
	}

	private boolean valida(ProcesoDiarioRequest req) {
		logger.info(String.format("valida: req=%s getFuncionOwners=%s procesosValidos[%s, %s]", 
				req, getFuncionOwners(), procesosValidos[0], procesosValidos[1]));
		boolean valida = false;
		if (!(req.getFuncion().equals(getFuncionOwners()))) 
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

	// ===========================================================================================================
	// Getters y Setters
	// ===========================================================================================================

	public String getFuncionOwners() {
		return funcionOwners;
	}

	public void setFuncionOwners(String funcionOwners) {
		this.funcionOwners = funcionOwners;
	}

	public String getProcesos() {
		return procesos;
	}

	public void setProcesos(String procesos) {
		this.procesos = procesos;
	}
}
