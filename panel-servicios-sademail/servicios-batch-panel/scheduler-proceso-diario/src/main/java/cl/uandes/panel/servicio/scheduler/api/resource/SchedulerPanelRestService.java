package cl.uandes.panel.servicio.scheduler.api.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;

/**
 * 
 * Recive el String que informa que proceso batch termino
 * @author fernando
 *
 */
@Path("/")
public class SchedulerPanelRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

    @PropertyInject(value = "crear-cuentas-azure.proceso", defaultValue="crear_cuentas_azure")
	private String procesoCrearCuentas;
    @PropertyInject(value = "crear-grupos-azure.proceso", defaultValue="crear_grupos_azure")
	private String procesoCrearGrupos;
    @PropertyInject(value = "grupos_inprogress_azure.proceso", defaultValue="grupos_inprogress_azure")
	private String procesoCrearGruposInprogress;
    @PropertyInject(value = "grupos_inprogress_posgrado_azure.proceso", defaultValue="grupos_inprogress_posgrado_azure")
	private String procesoCrearGruposPosgrado;

    Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/finProceso")
	public Response getFinProceso(SchedulerPanelRequest request) {
		Exchange exchange = ExchangeBuilder.anExchange(producer.getCamelContext()).withHeader("request", request)
				.withBody(request.getOperacion()).build();
		logger.info(String.format(" %s", request));
		logger.info(String.format("esCrearCuentas: %b esCrearGrupos: %b",
				esCrearCuentas(request.getOperacion()), esCrearGrupos(request.getOperacion())));
		if (esCrearCuentas(request.getOperacion()))
			producer.asyncSend("direct:terminaCrearCuentas", exchange);
		else if (esCrearGrupos(request.getOperacion()))
			producer.asyncSend("direct:terminaCrearGrupos", exchange);
		logger.info("va a crear Response");
		Response response = Response.ok().status(200).entity("{ \"respuesta\": \"ACK\"}").build();
		return response;
	}

	public boolean esCrearCuentas(String operacion) {
		return getProcesoCrearCuentas().equalsIgnoreCase(operacion);
	}

	/**
	 * @param operacion
	 * @return true si es cualquiera de los procesos de creacion de grupos
	 */
	public boolean esCrearGrupos(String operacion) {
		if (getProcesoCrearGrupos().equalsIgnoreCase(operacion))
			return true;
		if (getProcesoCrearGruposInprogress().equalsIgnoreCase(operacion))
			return true;
		if (getProcesoCrearGruposPosgrado().equalsIgnoreCase(operacion))
			return true;
		return false;
	}

	public String getProcesoCrearCuentas() {
		return procesoCrearCuentas;
	}

	public void setProcesoCrearCuentas(String procesoCrearCuentas) {
		this.procesoCrearCuentas = procesoCrearCuentas;
	}

	public String getProcesoCrearGrupos() {
		return procesoCrearGrupos;
	}

	public void setProcesoCrearGrupos(String procesoCrearGrupos) {
		this.procesoCrearGrupos = procesoCrearGrupos;
	}

	public String getProcesoCrearGruposInprogress() {
		return procesoCrearGruposInprogress;
	}

	public void setProcesoCrearGruposInprogress(String procesoCrearGruposInprogress) {
		this.procesoCrearGruposInprogress = procesoCrearGruposInprogress;
	}

	public String getProcesoCrearGruposPosgrado() {
		return procesoCrearGruposPosgrado;
	}

	public void setProcesoCrearGruposPosgrado(String procesoCrearGruposPosgrado) {
		this.procesoCrearGruposPosgrado = procesoCrearGruposPosgrado;
	}
}
