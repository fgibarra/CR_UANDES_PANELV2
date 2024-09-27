package cl.uandes.panel.apiCrearCuentaServices.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.apache.cxf.jaxrs.impl.ResponseImpl;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosCuentaAdDTO;
import cl.uandes.panel.apiCrearCuentaServices.exceptions.ValidaEnAdException;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.serviciosLDAP.Usuario;
import cl.uandes.panel.comunes.json.serviciosLDAP.UsuarioResponse;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;

public class ValidaEnAD {

	@PropertyInject(value = "uri.serviciosAD", defaultValue = "http://localhost:8181/cxf/ESB/panel/serviciosADv3")
	private String adServices;

	@EndpointInject(uri = "cxfrs:bean:rsADconsultaXrut?continuationTimeout=-1") // consultar cuenta AD por RUT
	ProducerTemplate consultaRutAD;
	String templateConsultaXrut = "%s/consultaXrut";

	@EndpointInject(uri = "cxfrs:bean:rsADconsultarUsuario") // consultar cuenta AD
	ProducerTemplate consultarSamaccountNameAD;
	String templateConsultaXSamaccountName = "%s/consultaXrut";
	
	@EndpointInject(uri = "cxfrs:bean:rsADactualizarUsuario") // actualizar cuenta AD
	ProducerTemplate actualizarUsuarioAD;
	String templateActualizarUsuario = "%s/actualizarUsuario";
	
	@EndpointInject(uri = "cxfrs:bean:rsADeliminarUsuario") // eliminar cuenta AD
	ProducerTemplate eliminarUsuarioAD;
	String templateEliminarUsuario = "%s/eliminarUsuario";
	
	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario") // crear cuenta AD
	ProducerTemplate crearUsuarioAD;
	String templateCrearUsuario = "%s/crearUsuario";
	
	private Logger logger = Logger.getLogger(getClass());
	private String msgError;
	
	/**
	 * Consulta por rut al Ad
	 * Si se encuentra y esta en la rama AlumnosUA o ProfesoresUA
	 * 	- si el samaccountName no es el login_name:
	 * 		- se recrea la cuenta con el login_name como samaccountName (queda actualizado el userPrincipalName),
	 * 			los datos se sacan de la consulta al AD por samaccountName
	 *  - si samaccountName = login_name:
	 *  	- consulta por samaccountName el AD
	 *  	- si no tiene userPrincipalName
	 *  		- actualizar el correo para que se actualice el userPrincipalName
	 * Si no se encuentra el rut en el AD:
	 * 	- avisar que no hay cuenta AD
	 * @param exchange
	 * @throws ValidaEnAdException 
	 */
	public void validaUserPrincipalName(Exchange exchange) throws ValidaEnAdException {
		Message message = exchange.getMessage();
		CreaCuentaRequest request = (CreaCuentaRequest)message.getHeader("request");
		String rut = request.getRut();
		UserResponse cuentaCreadaGmail = (UserResponse)message.getHeader("cuentaCreadaGmail");
		
		if (rut != null) {
			ConsultaXrutResponse consultaXrutResponse = consultaXrut(getEmployeeId(rut));
			if (consultaXrutResponse.getCodigo() == 0) {
				if (consultaXrutResponse.getEstado().indexOf("ERROR") >= 0 ) {
					// informar que la cuenta no existe
					informar(String.format("Cuenta %s NO existe", getEmployeeId(rut)));
				} else {
					// La cuenta existe en el AD
					DatosCuentaAdDTO dc = new DatosCuentaAdDTO(consultaXrutResponse);
					if (dc.isInRamaPermitida()) {
						// el metodo puede ser invocado cuando:
						// 1.- la cuenta ya existia
						// 2.- la cuenta ya ha sido creada en gmail
						boolean cuentaGmailExistia = cuentaCreadaGmail == null;
						// buscar la cuenta por samaccountName en el AD
						ServiciosLDAPRequest consultaXsamaccountNameRequest = new ServiciosLDAPRequest("ConsultarUsuario",
																				null,
																				Usuario.createUsuario4validar(dc.getSamaccountName())
																				);
						ServiciosLDAPResponse consultaXsamaccountNameResponse = consultaXsamaccountName(consultaXsamaccountNameRequest);
						String loginName = cuentaGmailExistia ? dc.getLoginName() : getLoginName(consultaXsamaccountNameResponse);
						if (loginName == null) {
							throw new ValidaEnAdException(msgError);
						}
						
						if (loginName.equals(dc.getSamaccountName())) {
							// validar que existe el userPrincipalName
							if (consultaXsamaccountNameResponse.getUsuario().getUserPrincipalName() == null) {
								// no tiene --> actualizar correo para que se cree el userPrincipalName
								actualizarUsuario(consultaXsamaccountNameResponse.getUsuario());
							}
						} else {
							dc.setLoginName(loginName);
							// recrear la cuenta AD con samaccountName = loginName
							recrearCuentaAd(dc, consultaXsamaccountNameResponse.getUsuario());
						}
					} else {
						informar (String.format("Cuenta %s está definida en la rama %s", dc.getRama()));
					}
				}
			}
		}
	}
	
	private String getLoginName(ServiciosLDAPResponse consultaXsamaccountNameResponse) {
		String loginName = null;
		if (consultaXsamaccountNameResponse.getCodigo() == 0) {
			loginName = consultaXsamaccountNameResponse.getUsuario().getCorreo();
		} else {
			msgError = String.format("No pudo recuperar loginName desde AD por %s", consultaXsamaccountNameResponse.getMensaje());
		}
		return loginName;
	}

	private void informar(String msg) {
		// TODO Auto-generated method stub
		
	}

	private String getEmployeeId(String rut) {
		if (rut.startsWith("@"))
			return rut.substring(1);
		return rut;
	}

	public ConsultaXrutResponse consultaXrut(String employeeId) {
		ConsultaXrutRequest request = new ConsultaXrutRequest(employeeId);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateConsultaXrut, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("consultaXrut URL: %s", headers.get(Exchange.DESTINATION_OVERRIDE_URL)));
		logger.info(String.format("consultaXrut: request(por employeeID): %s", request));
		ConsultaXrutResponse response;
		try {
			response = (ConsultaXrutResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) consultaRutAD.requestBodyAndHeaders(request, headers), ConsultaXrutResponse.class);
		} catch (Exception e) {
			String msg = String.format("existeCuentaAD: error en producer consultaRutAD %s",employeeId);
			logger.error(msg, e);
			response = new ConsultaXrutResponse(-1, e.getMessage(), null, null, null, null, null, null, null, null,
					null, null);
		}
		return response;
	}

	private ServiciosLDAPResponse consultaXsamaccountName(ServiciosLDAPRequest request) {
		ServiciosLDAPResponse response = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateConsultaXSamaccountName, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("consultaXsamaccountName URL: %s", consultarSamaccountNameAD.getDefaultEndpoint().getEndpointUri()));
		logger.info(String.format("consultaXsamaccountName: request(por samAccountName): %s", request));
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) consultarSamaccountNameAD.requestBodyAndHeaders(request, headers), ServiciosLDAPResponse.class);
		} catch (Exception e) {
			String msg = String.format("consultaXsamaccountName: error en producer consultarSamaccountNameAD %s",request);
			logger.error(msg, e);
			response = new ServiciosLDAPResponse(-1, e.getMessage());
		}
		return response;
	}

	/**
	 * actualizar correo para que se cree el userPrincipalName
	 * @param usuario
	 * @throws ValidaEnAdException 
	 */
	private void actualizarUsuario(UsuarioResponse usuarioResponse) throws ValidaEnAdException {
		ServiciosLDAPResponse response = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateActualizarUsuario, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("actualizarUsuario URL: %s", actualizarUsuarioAD.getDefaultEndpoint().getEndpointUri()));
		Usuario usuario = Usuario.createUsuario4validar(usuarioResponse.getCuenta());
		usuario.setCorreo(usuarioResponse.getCorreo());
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ActualizarUsuario", null, usuario);
		logger.info(String.format("actualizarUsuario: request: %s", request));
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) actualizarUsuarioAD.requestBodyAndHeaders(request, headers), ServiciosLDAPResponse.class);
			logger.info(String.format("actualizarUsuario: response= %s", response));
		} catch (Exception e) {
			msgError = String.format("actualizarUsuario: error %s en producer actualizarUsuarioAD %s",
					e.getMessage(),request);
			logger.error(msgError, e);
			throw new ValidaEnAdException(msgError);
		}
	}

	private void recrearCuentaAd(DatosCuentaAdDTO dc, UsuarioResponse usuarioResponse) throws ValidaEnAdException {
		eliminaCuenta(dc.getSamaccountName());
		String nombreCuenta = dc.getLoginName();
		String password = dc.getEmployeeId();
		String rama = dc.getRama();
		String rut = dc.getEmployeeId();
		String nombre = usuarioResponse.getNombre();
		String apellidos = usuarioResponse.getApellidos();
		Usuario usuario = Usuario.createUsuario4crear(nombreCuenta, password, rama, rut, nombre, apellidos);
		usuario.setComuna(usuarioResponse.getComuna());
		usuario.setCorreo(usuarioResponse.getCorreo());
		usuario.setDireccion(usuarioResponse.getDireccion());
		usuario.setEstadoAcademico(usuarioResponse.getEstadoAcademico());
		usuario.setNivel(usuarioResponse.getNivel());
		usuario.setPidm(usuarioResponse.getPidm());
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("CrearUsuario", null, usuario);
		crearCuenta(request);
	}
	
	private void eliminaCuenta(String samAccountName) throws ValidaEnAdException {
		ServiciosLDAPResponse response = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateEliminarUsuario, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("eliminaCuenta URL: %s", eliminarUsuarioAD.getDefaultEndpoint().getEndpointUri()));
		Usuario usuario = Usuario.createUsuario4validar(samAccountName);
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("EliminarUsuario", null, usuario);
		logger.info(String.format("eliminaCuenta: request: %s", request));
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) eliminarUsuarioAD.requestBodyAndHeaders(request, headers), ServiciosLDAPResponse.class);
			logger.info(String.format("eliminaCuenta: response= %s", response));
		} catch (Exception e) {
			msgError = String.format("eliminaCuenta: error %s en producer eliminarUsuarioAD %s",
					e.getMessage(),request);
			logger.error(msgError, e);
			throw new ValidaEnAdException(msgError);
		}
	}

	private void crearCuenta(ServiciosLDAPRequest request) throws ValidaEnAdException {
		ServiciosLDAPResponse response = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCrearUsuario, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("crearCuenta URL: %s", crearUsuarioAD.getDefaultEndpoint().getEndpointUri()));
		logger.info(String.format("crearCuenta: request: %s", request));
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) crearUsuarioAD.requestBodyAndHeaders(request, headers), ServiciosLDAPResponse.class);
			logger.info(String.format("crearCuenta: response= %s", response));
		} catch (Exception e) {
			msgError = String.format("crearCuenta: error %s en producer crearUsuarioAD %s",
					e.getMessage(),request);
			logger.error(msgError, e);
			throw new ValidaEnAdException(msgError);
		}
	}
	//===============================================================================================================
	// Getters y Setters
	//===============================================================================================================

	public String getAdServices() {
		return adServices;
	}

	public void setAdServices(String adServices) {
		this.adServices = adServices;
	}
}
