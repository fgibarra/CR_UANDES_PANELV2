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

    @PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue="crear_cuentas")
	private String procesoCrearCuentas;
    @PropertyInject(value = "crear-grupos-gmail.proceso", defaultValue="crear_grupos")
	private String procesoCrearGrupos;
    @PropertyInject(value = "grupos_inprogress_gmail.proceso", defaultValue="grupos_inprogress")
	private String procesoCrearGruposInprogress;
    @PropertyInject(value = "grupos_inprogress_posgrado_gmail.proceso", defaultValue="grupos_inprogress_posgrado")
	private String procesoCrearGruposPosgrado;
    @PropertyInject(value = "sinc_grupos_generales.operacion", defaultValue="sinc_grupos_generales")
	private String procesoSyncGruposGenerales;
    @PropertyInject(value = "sinc_grupos_vigentes.operacion", defaultValue="sinc_grupos_vigentes")
	private String procesoSyncGruposVigentes;
    @PropertyInject(value = "sinc_grupos_postgrado.operacion", defaultValue="sinc_grupos_postgrado")
	private String procesoSyncGruposPostgrado;
    
    Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/finProceso")
	public Response getFinProceso(SchedulerPanelRequest request) {
		Exchange exchange = ExchangeBuilder.anExchange(producer.getCamelContext()).withHeader("request", request)
				.withBody(request.getOperacion()).build();
		logger.info(String.format(" %s", request));
		logger.info(String.format("esCrearCuentas: %b esCrearGrupos: %b esSincronizarGrupos: %b",
				esCrearCuentas(request.getOperacion()), esCrearGrupos(request.getOperacion()),
				esSincronizarGrupos(request.getOperacion())));
		if (esCrearCuentas(request.getOperacion()))
			producer.asyncSend("direct:terminaCrearCuentas", exchange);
		else if (esCrearGrupos(request.getOperacion()))
			producer.asyncSend("direct:terminaCrearGrupos", exchange);
		else if (esSincronizarGrupos(request.getOperacion()))
			producer.asyncSend("direct:terminaSincronizarGrupos", exchange);
		
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

	public boolean esSincronizarGrupos(String operacion) {
		if (getProcesoSyncGruposGenerales().equalsIgnoreCase(operacion))
			return true;
		if (getProcesoSyncGruposVigentes().equalsIgnoreCase(operacion))
			return true;
		if (getProcesoSyncGruposPostgrado().equalsIgnoreCase(operacion))
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

	public synchronized String getProcesoSyncGruposGenerales() {
		return procesoSyncGruposGenerales;
	}

	public synchronized void setProcesoSyncGruposGenerales(String procesoSyncGruposGenerales) {
		this.procesoSyncGruposGenerales = procesoSyncGruposGenerales;
	}

	public synchronized String getProcesoSyncGruposVigentes() {
		return procesoSyncGruposVigentes;
	}

	public synchronized void setProcesoSyncGruposVigentes(String procesoSyncGruposVigentes) {
		this.procesoSyncGruposVigentes = procesoSyncGruposVigentes;
	}

	public synchronized String getProcesoSyncGruposPostgrado() {
		return procesoSyncGruposPostgrado;
	}

	public synchronized void setProcesoSyncGruposPostgrado(String procesoSyncGruposPostgrado) {
		this.procesoSyncGruposPostgrado = procesoSyncGruposPostgrado;
	}
}
