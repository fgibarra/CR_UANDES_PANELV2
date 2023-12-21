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

import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.RequestOwners;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

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
	@PropertyInject(value = "ae-owners-members-grupo.nocturno.funcionSincronizar", defaultValue = "sincronizar_owners")
	private String funcionOwners;
	@PropertyInject(value = "ae-owners-members-grupo.procesos", defaultValue = "sincroniza,procesoAE")
	private String procesos;
	@PropertyInject(value = "ae-owners-members-grupo.servicios", defaultValue = "member,owner")
	private String servicios;
	@PropertyInject(value = "ae-owners-members-grupo.funciones", defaultValue = "add,del")
	private String funciones;
	
	public static String procesosValidos[];
	public static String serviciosValidos[];
	public static int OWNER = 1;
	public static int MEMBER = 0;
	public static String funcionesValidos[];
	public static int ADD = 0;
	public static int DEL = 1;
	
	private String msgErrorValidacion;
	
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
		String proceso = getFuncionOwners();
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext)
				.withHeader("proceso", proceso) // kco_funcion para inicializar
				.withHeader("request", request).withBody(request)
				.build();

		String uri = "direct:procesoSincronizar";

		logger.info(String.format("CrearGruposRestService.procese: %s proceso con funcion=%s header.request = %s", uri,
				getFuncionSincroniza(), exchange.getIn().getHeader("request")));

		procesoBatch.asyncSend(uri, exchange);

		Response response = Response.ok().status(200).entity(String.format("Partio %s", funcionOwners)).build();
		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/pueblaWrkOwnersGrupos")
	public Response procesar(AeOwnersMembersRequest request) {
		
		initServiciosFuncionesValidos();
		
		if (!valida(request))
			return Response.ok().status(401).entity(getMsgErrorValidacion()).build();

		String proceso;
		if (request.getServicio().equalsIgnoreCase(serviciosValidos[OWNER]))
			proceso = "asignar_owners";
		else
			proceso = "asignar_miembros";
		
		// Validado.
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext)
				.withHeader("proceso", proceso) // kco_funcion para inicializar
				.withHeader("request", request).withBody(request)
				.build();

		String uri = "direct:procesoAE";
		logger.info(String.format("CrearGruposRestService.procese: %s proceso con funcion=%s header.request = %s", uri,
				getFuncionEA(), exchange.getIn().getHeader("request")));

		procesoBatch.asyncSend(uri, exchange);

		Response response = Response.ok().status(200).entity(String.format("Partio %s", funcionOwners)).build();
		return response;
	}

	private void initServiciosFuncionesValidos() {
		if (serviciosValidos == null)
			serviciosValidos = getServicios().split(",");
		
		if (funcionesValidos == null)
			funcionesValidos = getFunciones().split(",");
		
	}

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

	private boolean valida(AeOwnersMembersRequest request) {
		//logger.info(String.format("valida: request: %s funcion |%s|", request, request.getCriterio().getFuncion().toLowerCase()));
		setMsgErrorValidacion(null);
		if (request.getCuentasEnvio() == null || request.getCuentasEnvio().length() == 0)
			setMsgErrorValidacion("Debe ingresar correo para informar");

		if (request.getServicio() == null || request.getServicio().length() == 0)
			setMsgErrorValidacion("Debe ingresar servicio a ejecutar");
		else if (!StringUtils.estaContenido(request.getServicio().toLowerCase(), serviciosValidos ))
			setMsgErrorValidacion(String.format("Servicio %s no es valido", request.getServicio()));
		
		if (request.getCriterio() == null)
			setMsgErrorValidacion ("Debe definir un criterio a procesar");
		else {
			RequestOwners criterio = request.getCriterio();
			if (criterio.getFuncion() == null || criterio.getFuncion().length() == 0)
				setMsgErrorValidacion ("El criterio debe contener una funcion");
			else if (!StringUtils.estaContenido(criterio.getFuncion().toLowerCase(), funcionesValidos ))
				setMsgErrorValidacion (String.format("Funcion %s es invalida", criterio.getFuncion()));
			
			if (criterio.getProgramas() == null || criterio.getProgramas().size() == 0)
				setMsgErrorValidacion("Debe indicar programas con que operar");
			
			if (criterio.getCuentas() == null || criterio.getCuentas().size() == 0)
				setMsgErrorValidacion("Debe indicar las cuentas con que operar");
		}
		return getMsgErrorValidacion() == null;
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

	public String getMsgErrorValidacion() {
		return msgErrorValidacion;
	}

	public void setMsgErrorValidacion(String msgErrorValidacion) {
		if (msgErrorValidacion == null || this.msgErrorValidacion == null) {
			this.msgErrorValidacion = msgErrorValidacion;
		} else {
			this.msgErrorValidacion = this.msgErrorValidacion+"; "+msgErrorValidacion;
		}
	}

	public synchronized String getServicios() {
		return servicios;
	}

	public synchronized void setServicios(String servicios) {
		this.servicios = servicios;
	}

	public synchronized String getFunciones() {
		return funciones;
	}

	public synchronized void setFunciones(String funciones) {
		this.funciones = funciones;
	}
}
