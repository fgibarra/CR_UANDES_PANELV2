package cl.uandes.panel.servicio.crearCuentas.api.resources;

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
public class CrearCuentasRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
    @PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue="crear_cuentas_gmail")
	private String procesoCrearCuentas;
    @PropertyInject(value = "crear-cuentas-gmail.tiposCuenta", defaultValue="Alumnos")
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
		if (!valida(request)) {
			return Response.ok().status(401).entity(getMsgError()).build();
		}
		// responder al Scheduler y partir el proceso en forma batch
		CamelContext camelContext = producer.getCamelContext();
		// partir el proceso batch
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withHeader("request", request).
				withHeader("proceso", request.getFuncion())
				.withBody(request).build();
		logger.info(String.format("CrearCuentasRestService.procese: activa direct:proceso con header.request = %s", 
				exchange.getIn().getHeader("request")));
		procesoBatch.asyncSend("direct:proceso", exchange);
		
		Response response = Response.ok().status(200).entity("Partio crear_cuentas").build();
		return response;
	}

	private boolean valida(ProcesoDiarioRequest request) {
		if (!funcionesValidas(request.getFuncion()))
			return false;
		if (!operacionesValidas(request))
			return false;
		return true;
	}

	private boolean funcionesValidas(String funcion) {
		return funcion.equalsIgnoreCase(getProcesoCrearCuentas());
	}

	private boolean operacionesValidas(ProcesoDiarioRequest request) {
		for (String op : request.getOperaciones()) {
			if (getTiposCuenta().equalsIgnoreCase(op))
				return true;
		}
		return false;
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

	public String getProcesoCrearCuentas() {
		return procesoCrearCuentas;
	}

	public void setProcesoCrearCuentas(String procesoCrearCuentas) {
		this.procesoCrearCuentas = procesoCrearCuentas;
	}
}
