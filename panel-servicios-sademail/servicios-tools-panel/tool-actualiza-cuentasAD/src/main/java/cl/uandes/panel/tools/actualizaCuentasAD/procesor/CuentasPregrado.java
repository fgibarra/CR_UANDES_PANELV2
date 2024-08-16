package cl.uandes.panel.tools.actualizaCuentasAD.procesor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.serviciosLDAP.Usuario;
import cl.uandes.panel.comunes.json.serviciosLDAP.UsuarioResponse;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.ConsultaCuentaADException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.CrearCuentaADException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.CrearEnTablaException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.EliminarCuentaADException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.EliminarDeTablaException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.EsCuentaCreadaXpanelException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.ExisteCuentaADException;
import cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions.ValidarCuentaADException;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.ContadoresActualizaCuentas;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.CorregirPregradoDTO;

/**
 * 
 * Si existe la cuenta por rut:
 * - Eliminarla del AD y de la tabla AD_CUENTAS_CREADAS
 * - Crear cuenta en AD y en tabla
 * - contadores
 * 
 * Si existe cuenta por loginName(samaccountName)
 * - Validar que employeeID sea RUT sin @
 * - contadores
 * 
 * Si no exsite por ninguna de los dos criterios:
 * - Crearla con samaccountName con el loginName
 * - crear en tabla
 * - contadores
 * 
 * @author sademail
 *
 */
public class CuentasPregrado implements Processor {

	@PropertyInject(value = "actualizar-cuentas-AD.debug", defaultValue = "false")
	protected String debug;
	protected Boolean soloDebug = Boolean.valueOf(debug); //  true --> NO envia

	@EndpointInject(uri = "cxfrs:bean:rsADvalidarUsuario") // validar cuenta AD
	ProducerTemplate validarUsuarioAD;
	@EndpointInject(uri = "cxfrs:bean:rsADactualizarUsuario") // actualizar cuenta AD
	ProducerTemplate actualizarUsuarioAD;
	@EndpointInject(uri = "cxfrs:bean:rsADconsultarUsuario") // consultar cuenta AD
	ProducerTemplate consultarUsuarioAD;
	@EndpointInject(uri = "cxfrs:bean:rsADeliminarUsuario") // eliminar cuenta AD
	ProducerTemplate eliminarUsuarioAD;
	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario") // crear cuenta AD
	ProducerTemplate crearUsuarioAD;

	@EndpointInject(uri = "sql:classpath:sql/insertAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertAdCuentasCreadas;

	private String msgError;
	private ContadoresActualizaCuentas contadores;
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		this.soloDebug = Boolean.valueOf(getDebug());
		this.contadores = (ContadoresActualizaCuentas)message.getHeader("contadores");
		if (contadores == null)
			throw new RuntimeException("No se inicializo ContadoresActualizaCuentas");
		contadores.incCountProcesados();
		CorregirPregradoDTO dto = (CorregirPregradoDTO) message.getHeader("CorregirPregradoDTO");
		try {
			if (existeCuentaAD(dto.getEmployeeId())) {
				if (esCuentaCreadaXpanel(dto.getEmployeeId())) {
					if (!soloDebug) {
						eliminarCuentaAD(dto.getEmployeeId());
						eliminarDeTabla(dto.getRut());
						crearCuentaAD(dto);
					}
					contadores.incCountRecreadasAD();
				}
			} else if (existeCuentaAD(dto.getSammacountName())) {
				if (esCuentaCreadaXpanel(dto.getSammacountName())) {
					validarCuentaAD(dto);
					contadores.incCountActualizadasAD();
				}
			} else {
				crearCuentaAD(dto);
				crearEnTabla(dto);
				contadores.incCountCreadasAD();
			}
		} catch (Exception e) {
			contadores.incCountErrores();
			String msg = e.getMessage();
			if (e instanceof ExisteCuentaADException) {
				
			} else if ( e instanceof CrearCuentaADException) {
			} else if ( e instanceof ValidarCuentaADException) {
			} else if ( e instanceof EliminarDeTablaException) {
			} else if ( e instanceof CrearEnTablaException) {
				
			}
			
		}
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.decCounter();
	}

	private boolean existeCuentaAD(String sammacountName) throws ValidarCuentaADException {
		boolean existeCuenta = false;
		try {
			Map<String,Object> headers = new HashMap<String,Object>();
			// validar el usuario en el AD
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/validarUsuario");
			headers.put("CamelHttpMethod", "POST");
			ServiciosLDAPRequest request = new ServiciosLDAPRequest("ValidarUsuario", 
					null, 
					Usuario.createUsuario4validar(sammacountName));
			ServiciosLDAPResponse response = null;
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) validarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("validar cuenta para samaccountName: %s msg: %s",
						sammacountName, response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje()))
					existeCuenta = true;
				else
					existeCuenta = false;
			} else {
				String msg = String.format("ERROR: no se valido la cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
				throw new ValidarCuentaADException(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al validarCuenta %s", sammacountName);
			setMsgError(msg);
			throw new ValidarCuentaADException(msg, e);
		}
		return existeCuenta;
	}

	/**
	 * - Consultar cuenta por samaccountName
	 * - validar si employeeId no comienza con rut, tiene correo?, nivel?, estado?. pidm?, nombres?, apellidos?
	 * - Si hay que corregir --> actualizar cuenta AD
	 * @param dto
	 * @throws ValidarCuentaADException
	 */
	private void validarCuentaAD(CorregirPregradoDTO dto) throws ValidarCuentaADException {
		UsuarioResponse cuentaAD = null;
		try {
			cuentaAD = consultaCuentaAD(dto.getSammacountName());
		} catch (ConsultaCuentaADException e) {
			String msg = String.format("Error al consultar %s", dto.getSammacountName());
			throw new ValidarCuentaADException(msg, e);
		}
		boolean hayQueActualizar = false;
		Map<String, String> campos = new HashMap<String, String>();
		if ((cuentaAD.getApellidos() == null || !cuentaAD.getApellidos().equalsIgnoreCase(dto.getApellidos())) &&
				dto.getApellidos() != null) {
			hayQueActualizar = true;
			campos.put("Apellidos", dto.getApellidos());
		}
		if ((cuentaAD.getNombre() == null || !cuentaAD.getNombre().equalsIgnoreCase(dto.getNombres())) &&
				dto.getNombres() != null) {
			hayQueActualizar = true;
			campos.put("Nombre", dto.getNombres());
		}
		if ((cuentaAD.getEstadoAcademico() == null || !cuentaAD.getEstadoAcademico().equalsIgnoreCase(dto.getEstado())) &&
				dto.getEstado() != null) {
			hayQueActualizar = true;
			campos.put("EstadoAcademico", dto.getEstado());
		}
		if ((cuentaAD.getNivel() == null || !cuentaAD.getNivel().equalsIgnoreCase(dto.getNivel())) &&
				dto.getNivel() != null) {
			hayQueActualizar = true;
			campos.put("Nivel", dto.getNivel());
		}
		if ((cuentaAD.getPidm() == null || !cuentaAD.getPidm().equalsIgnoreCase(dto.getPidm())) &&
				dto.getPidm() != null) {
			hayQueActualizar = true;
			campos.put("Pidm", dto.getPidm());
		}
		if ((cuentaAD.getRut() == null || !cuentaAD.getRut().equalsIgnoreCase(dto.getEmployeeId())) &&
				dto.getEmployeeId() != null) {
			hayQueActualizar = true;
			campos.put("Rut", dto.getEmployeeId());
		}
		if ((cuentaAD.getCorreo() == null || !cuentaAD.getCorreo().equalsIgnoreCase(dto.getCorreo())) &&
				dto.getCorreo() != null) {
			hayQueActualizar = true;
			campos.put("Correo", dto.getCorreo());
		}
		if (hayQueActualizar ) {
			// crear el json de rfequest
			Usuario usuario = Usuario.createUsuario4validar(dto.getSammacountName());
			for (String at : campos.keySet()) {
				String metodo = String.format("set%s", at);
				try {
					Method setValor = Usuario.class.getMethod(metodo, String.class);
					setValor.invoke(usuario, campos.get(at));
				} catch (Exception e) {
					String msg = String.format("Error al setear %s con %s", at, campos.get(at));
					throw new ValidarCuentaADException(msg, e);
				}
			}
			if (soloDebug)
				logger.info(String.format("actualizar los siguientes atributos cuenta %s", usuario));
			
			// actualizar el AD
			if (!soloDebug) {
				actualizaCuenta(usuario);
			}
		}
	}

	private boolean actualizaCuenta(Usuario usuario) throws ValidarCuentaADException {
		boolean resultado = false;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/actualizarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ActualizarUsuario", 
				null, 
				usuario);
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) actualizarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("actualizar cuenta para usuario: %s msg: %s",
						usuario, response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje())) {
					resultado = true;
				} else
					setMsgError(String.format("WS actualizar responde %s", response.getMensaje()));
			} else {
				String msg = String.format("ERROR: no se actualizo cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para actualizar usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
			throw new ValidarCuentaADException(msg, e);
		}
		return resultado;
	}

	private void eliminarCuentaAD(String samaccountName) throws EliminarCuentaADException {
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/eliminarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("EliminarUsuario", 
				null, 
				Usuario.createUsuario4validar(samaccountName));
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) eliminarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("eliminar cuenta para samaccountName: %s msg: %s",
						samaccountName, response.getMensaje()));
				if (!"OK".equalsIgnoreCase(response.getMensaje())) {
					String msg = String.format("WS eliminar responde %s", response.getMensaje());
					setMsgError(msg);
					throw new EliminarCuentaADException(msg);
				}
			} else {
				String msg = String.format("ERROR: no se elimino cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para actualizar usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
			throw new EliminarCuentaADException(msg, e);
		}
	}

	private void crearCuentaAD(CorregirPregradoDTO dto) throws CrearEnTablaException {
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/crearUsuario");
		headers.put("CamelHttpMethod", "POST");
		Usuario usuario = new Usuario(dto.getSammacountName(), soloDebug?"12345678,A#":dto.getPassword(), "AlumnosUA", dto.getRut().substring(1),
				dto.getNombres(), dto.getApellidos(), dto.getCorreo(),
				null, null, dto.getPidm(), dto.getNivel(), dto.getEstado());
		
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("CrearUsuario", 
				null, 
				usuario);
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) crearUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("crear cuenta para samaccountName: %s msg: %s",
						dto.getSammacountName(), response.getMensaje()));
				if (!"OK".equalsIgnoreCase(response.getMensaje())) {
					String msg = String.format("WS crear cuenta responde %s", response.getMensaje());
					setMsgError(msg);
					throw new CrearEnTablaException(msg);
				}
			} else {
				String msg = String.format("ERROR: no se creo cuenta cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
				throw new CrearEnTablaException(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para crear usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
			throw new CrearEnTablaException(msg, e);
		}
	}

	private void eliminarDeTabla(String samaccountName) throws EliminarDeTablaException {
		try {
			eliminarUsuarioAD.requestBodyAndHeader(null, "samaccountName", samaccountName);
		} catch (CamelExecutionException e) {
			throw new EliminarDeTablaException(String.format("Error al tratar de eliminar %s de la tabla", samaccountName), e);
		}
		
	}

	private void crearEnTabla(CorregirPregradoDTO dto) throws CrearCuentaADException {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("samaccountName", dto.getSammacountName());
		headers.put("rut", dto.getRut());
		headers.put("ou", String.format("cn=%s %s,ou=%s creada", dto.getNombres(), dto.getApellidos(), dto.getRut()));
		try {
			insertAdCuentasCreadas.requestBodyAndHeaders(null, headers);
		} catch (CamelExecutionException e) {
			throw new CrearCuentaADException(String.format("Al tratar de crear entrada en tabla: %s", dto), e);
		}
	}

	/**
	 * Recupera el distinguishedName para validar que la rama sea AlumnosUA (OU=AlumnosUA)
	 * @param dto
	 * @return
	 */
	private boolean esCuentaCreadaXpanel(String samaccountName) throws EsCuentaCreadaXpanelException {
		boolean esCuentaCreadaXpanel = false;
		try {
			UsuarioResponse datos = consultaCuentaAD(samaccountName);
			if (datos != null) {
				String distinguishedName = datos.getDistinguishedName();
				if (distinguishedName != null) {
					String adAtts[] = distinguishedName.split(",");
					for (String at : adAtts) {
						String valores[] = at.split("=");
						if (valores[0].equalsIgnoreCase("OU")) {
							if (valores[1].equalsIgnoreCase("AlumnosUA"))
								esCuentaCreadaXpanel = true;
						}
					}
				}
			}
			logger.info(String.format("esCuentaCreadaXpanel: esCuentaCreadaXpanel=%b samaccount_name=%s",
					esCuentaCreadaXpanel, samaccountName));
		} catch (ConsultaCuentaADException e) {
			throw new EsCuentaCreadaXpanelException(e.getMessage(), e);
		}
		return esCuentaCreadaXpanel;
	}

	private UsuarioResponse consultaCuentaAD(String samaccountName) throws ConsultaCuentaADException {
		UsuarioResponse usuarioResponse = null;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/consultarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ConsultarUsuario", 
				null, 
				Usuario.createUsuario4validar(samaccountName));
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) consultarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("validar cuenta para samaccountName: %s msg: %s",
						samaccountName, response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje()))
					usuarioResponse = response.getUsuario();
				else
					setMsgError(String.format("WS consultar responde %s", response.getMensaje()));
			} else {
				String msg = String.format("ERROR: no se consulto cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para consultar usuario AD. request=%s", request);
			setMsgError(msg);
			throw new ConsultaCuentaADException(msg, e);
		}
		return usuarioResponse;
	}

	//===============================================================================================================
	// Getters y Setters
	//===============================================================================================================


	public String getDebug() {
		return debug;
	}


	public void setDebug(String debug) {
		this.debug = debug;
	}


	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}
}
