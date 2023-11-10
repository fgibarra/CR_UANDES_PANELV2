package cl.uandes.comun.ad.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;

/**
 * APIREST para consultas que se hacen al AD<br>
 * URI base: cxf/ESB/panel/serviciosAD<br>
 * <table>
 * <caption>Funciones</caption>
 * <tr><th>FUNCION</th><th>URI</th><th>METODO HTTP</th><th>OUTPUT/INPUT</th></tr>
 * <tr><td>consulta por RUT</td><td>/consultaXrut</td><td>POST</td><td>ConsultaXrutResponse consultaXrut(ConsultaXrutRequest request)</td></tr>
 * <tr><td>activar/desactivar usuario</td><td>/activarDesactivarUsuario</td><td>POST</td><td>ServiciosLDAPResponse activarDesactivarUsuario(ServiciosLDAPRequest request)</td></tr>
 * <tr><td>actualizar usuario</td><td>/actualizarUsuario</td><td>POST</td><td>ServiciosLDAPResponse actualizarUsuario(ServiciosLDAPRequest request)</td></tr>
 * <tr><td>crear usuario</td><td>/crearUsuario</td><td>POST<td>ServiciosLDAPResponse crearUsuario(ServiciosLDAPRequest request)</td></tr>
 * <tr><td>desbloquesr usuario</td><td>/desbloquearUsuario</td><td>POST</td><td>ServiciosLDAPResponse desbloquearUsuario(ServiciosLDAPRequest request)</td></tr>
 * <tr><td>resetear password</td><td>/resetearPassword</td><td>POST</td><td>ServiciosLDAPResponse resetearPassword(ServiciosLDAPRequest request)</td></tr>
 * <tr><td>validat usuario</td><td>/validarUsuario</td><td>POST</td><td>ServiciosLDAPResponse validarUsuario(ServiciosLDAPRequest request)</td></tr>
 * </table>
 * @author fernando
 *
 */
@Path("/")
public class ConsultasADRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

    @PropertyInject(value = "operacion.consultaXrut", defaultValue="consultaXrut")
    private String consultaXrut;
    @PropertyInject(value = "operacion.activarDesactivarUsuario", defaultValue="ActivarDesactivarUsuario")
    private String activarDesactivarUsuario;
    @PropertyInject(value = "operacion.actualizarUsuario", defaultValue="ActualizarUsuario")
    private String actualizarUsuario;
    @PropertyInject(value = "operacion.crearUsuario", defaultValue="CrearUsuario")
    private String crearUsuario;
    @PropertyInject(value = "operacion.desbloquearUsuario", defaultValue="DesbloquearUsuario")
    private String desbloquearUsuario;
    @PropertyInject(value = "operacion.resetearPassword", defaultValue="ResetearPassword")
    private String resetearPassword;
    @PropertyInject(value = "operacion.validarUsuario", defaultValue="ValidarUsuario")
    private String validarUsuario;

	Logger logger = Logger.getLogger(getClass());
	String msgErrorValidacion;

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
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/consultaXrut")
	public ConsultaXrutResponse consultaXrut(ConsultaXrutRequest request) {
		logger.info(String.format("llego: %s", request!=null?request:"NULO"));
		if (!validar(request))
			return new ConsultaXrutResponse(-1, getMsgErrorValidacion(),null,null,null,null,null,null,null,null,null,null);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		headers.put("id", request.getRut());
		return (ConsultaXrutResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/activarDesactivarUsuario")
	public ServiciosLDAPResponse activarDesactivarUsuario(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, getMsgErrorValidacion());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/actualizarUsuario")
	public ServiciosLDAPResponse actualizarUsuario(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, getMsgErrorValidacion());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/crearUsuario")
	public ServiciosLDAPResponse crearUsuario(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, getMsgErrorValidacion());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/desbloquearUsuario")
	public ServiciosLDAPResponse desbloquearUsuario(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, getMsgErrorValidacion());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/resetearPassword")
	public ServiciosLDAPResponse resetearPassword(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, getMsgErrorValidacion());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/validarUsuario")
	public ServiciosLDAPResponse validarUsuario(ServiciosLDAPRequest request) {
		if (!validar(request))
			return new ServiciosLDAPResponse(-1, msgErrorValidacion);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		return (ServiciosLDAPResponse)producer.requestBodyAndHeaders(request, headers);
	}

	private boolean validar(Object request) {
		if (request != null) {
			if (request instanceof ServiciosLDAPRequest)
				return validaServiciosLDAPRequest((ServiciosLDAPRequest) request);

			if (request instanceof ConsultaXrutRequest)
				return validaConsultaXrutRequest((ConsultaXrutRequest) request);

			setMsgErrorValidacion(String.format("El tipo %s no es una consulta valida", request.getClass().getName()));
		} else
			setMsgErrorValidacion("La consulta no puede ser nula");

		return false;
	}

	private boolean validaServiciosLDAPRequest(ServiciosLDAPRequest request) {
		String operacion = request.getServicio();
		if (operacion.equals(activarDesactivarUsuario))
			return validaCuentaActiva(request);
		else if (operacion.equals(actualizarUsuario))	
			return validaActualizarUsuario(request);
		else if (operacion.equals(crearUsuario))	
			return validaCrearUsuario(request);
		else if (operacion.equals(desbloquearUsuario))	
			return validaCuenta(request);
		else if (operacion.equals(resetearPassword))	
			return validaCuentaPassword(request);
		else if (operacion.equals(validarUsuario))	
			return validaCuenta(request);
		
		return false;
	}

	private boolean validaCuenta(ServiciosLDAPRequest request) {
		if (request.getUsuario() == null || request.getUsuario().getCuenta() == null || request.getUsuario().getCuenta().length() == 0) {
			setMsgErrorValidacion("Debe indicar cuenta del A.D.");
			return false;
		}
		return true;
	}

	private boolean validaCuentaPassword(ServiciosLDAPRequest request) {
		if (request.getUsuario() == null || request.getUsuario().getCuenta() == null || request.getUsuario().getCuenta().length() == 0) {
			setMsgErrorValidacion("Debe indicar cuenta del A.D.");
			return false;
		}
		if (request.getUsuario() == null || request.getUsuario().getPassword() == null || request.getUsuario().getPassword().length() == 0) {
			setMsgErrorValidacion("Debe indicar password de la cuenta del A.D.");
			return false;
		}
		return true;
	}

	private boolean validaCuentaActiva(ServiciosLDAPRequest request) {
		if (request.getUsuario() == null || request.getUsuario().getCuenta() == null || request.getUsuario().getCuenta().length() == 0) {
			setMsgErrorValidacion("Debe indicar cuenta del A.D.");
			return false;
		}
		if (request.getActivar() == null) {
			setMsgErrorValidacion("Debe indicar activar en TRUE o FALSE");
			return false;
		}
		return true;
	}

	private boolean validaCrearUsuario(ServiciosLDAPRequest request) {
		if (request.getUsuario() == null || request.getUsuario().getCuenta() == null || request.getUsuario().getCuenta().length() == 0) {
			setMsgErrorValidacion("Debe indicar cuenta del A.D.");
			return false;
		}
		if (request.getUsuario().getPassword() == null || request.getUsuario().getPassword().length() == 0) {
			setMsgErrorValidacion("Debe indicar password para la cuenta del A.D.");
			return false;
		}
		if (request.getUsuario().getRut() == null || request.getUsuario().getRut().length() == 0) {
			setMsgErrorValidacion("Debe indicar RUT para la cuenta del A.D.");
			return false;
		}
		if (request.getUsuario().getRama() == null || request.getUsuario().getRama().length() == 0) {
			setMsgErrorValidacion("Debe indicar RAMA donde crear la cuenta del A.D.");
			return false;
		}
		if (request.getUsuario().getNombre() == null || request.getUsuario().getNombre().length() == 0) {
			setMsgErrorValidacion("Debe indicar nombre");
			return false;
		}
		return true;
	}

	private boolean validaActualizarUsuario(ServiciosLDAPRequest request) {
		if (request.getUsuario() == null || request.getUsuario().getCuenta() == null || request.getUsuario().getCuenta().length() == 0) {
			setMsgErrorValidacion("Debe indicar cuenta del A.D.");
			return false;
		}
		if (request.getUsuario().getApellidos() != null)
			return true;
		if (request.getUsuario().getCargo() != null)
			return true;
		if (request.getUsuario().getCompania() != null)
			return true;
		if (request.getUsuario().getDepartamento() != null)
			return true;
		if (request.getUsuario().getJefatura() != null)
			return true;
		if (request.getUsuario().getNombre() != null)
			return true;
		if (request.getUsuario().getTelefono() != null)
			return true;

		setMsgErrorValidacion("Al menos debe indicar un atributo distinto del nombre de la cuenta a modificar");
		return false;
	}

	private boolean validaConsultaXrutRequest(ConsultaXrutRequest request) {
		if (request.getRut() == null || request.getRut().length()==0) {
			setMsgErrorValidacion("Rut no puede ser nulo o vacio");
			return false;
		}
		return true;
	}

	public String getMsgErrorValidacion() {
		return msgErrorValidacion;
	}

	public void setMsgErrorValidacion(String msgErrorValidacion) {
		this.msgErrorValidacion = msgErrorValidacion;
	}

	public String getConsultaXrut() {
		return consultaXrut;
	}

	public void setConsultaXrut(String consultaXrut) {
		this.consultaXrut = consultaXrut;
	}

	public String getActivarDesactivarUsuario() {
		return activarDesactivarUsuario;
	}

	public void setActivarDesactivarUsuario(String activarDesactivarUsuario) {
		this.activarDesactivarUsuario = activarDesactivarUsuario;
	}

	public String getActualizarUsuario() {
		return actualizarUsuario;
	}

	public void setActualizarUsuario(String actualizarUsuario) {
		this.actualizarUsuario = actualizarUsuario;
	}

	public String getCrearUsuario() {
		return crearUsuario;
	}

	public void setCrearUsuario(String crearUsuario) {
		this.crearUsuario = crearUsuario;
	}

	public String getDesbloquearUsuario() {
		return desbloquearUsuario;
	}

	public void setDesbloquearUsuario(String desbloquearUsuario) {
		this.desbloquearUsuario = desbloquearUsuario;
	}

	public String getResetearPassword() {
		return resetearPassword;
	}

	public void setResetearPassword(String resetearPassword) {
		this.resetearPassword = resetearPassword;
	}

	public String getValidarUsuario() {
		return validarUsuario;
	}

	public void setValidarUsuario(String validarUsuario) {
		this.validarUsuario = validarUsuario;
	}

}
