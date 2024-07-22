package cl.uandes.panel.tools.actualizaCuentasAD.procesor;

import java.util.HashMap;
import java.util.Map;

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
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.AdCuentasCreadasDTO;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.ContadoresActualizaCuentas;

public class CuentasThread implements Processor {

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

	@EndpointInject(uri = "sql:classpath:sql/updateAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateAdCuentasCreadas;
	@EndpointInject(uri = "sql:classpath:sql/insertAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertAdCuentasCreadas;
	@EndpointInject(uri = "sql:classpath:sql/deleteAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteAdCuentasCreadas;
	@EndpointInject(uri = "sql:commit?dataSource=#bannerDataSource")
	ProducerTemplate commit;
	@EndpointInject(uri = "sql:classpath:sql/insertWrkPlanilla.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertWrkPlanilla;
	
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
		
		AdCuentasCreadasDTO dto = (AdCuentasCreadasDTO)message.getHeader("AdCuentasCreadasDTO");
		logger.info(String.format("%s", dto));
		
		// dependiendo del samaccountName
		int resultado = existeCuenta(dto);
		if (resultado == 1) { // existe
			if (esCuentaCreadaXpanel(dto)) {
				if (dto.getSammacountName().startsWith("@"))
					eliminaCreaCuenta(dto);
				else
					actualizaCuenta(dto);
			}
			
		} else if ( resultado == 0) {
			// No existe la cuenta en AD
			registraBd(dto, String.format("no existe %s en AD", dto.getSammacountName()));
			deleteAdCuentasCreadas.requestBodyAndHeader(null, "samaccountName", dto.getSammacountName());
			contadores.incCountErrores();
			commit.requestBody(null);
		} else if (resultado == 2) {
			// error incrementa
			contadores.incCountErrores();
			registraBd(dto, String.format("error funcion ValidarUsuario (%s): %s", dto.getSammacountName(), dto, getMsgError()));
		}
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.decCounter();
	}


	/**
	 * Retorna 1 si existe el samaccountName en el AD, 0 si no existe, 2 si hay error
	 * @param dto
	 * @return
	 */
	private int existeCuenta(AdCuentasCreadasDTO dto) {
		int existeCuenta = 0;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/validarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ValidarUsuario", 
				null, 
				Usuario.createUsuario4validar(dto.getSammacountName()));
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) validarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("validar cuenta para samaccountName: %s msg: %s",
						dto.getSammacountName(), response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje()))
					existeCuenta = 1;
				else
					setMsgError(String.format("WS validar responde %s", response.getMensaje()));
			} else {
				String msg = String.format("ERROR: no se valido la cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
				existeCuenta = 2;
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para validar usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
			existeCuenta = 2;
		}
		return existeCuenta;
	}

	/**
	 * Recupera el distinguishedName para validar que la rama sea AlumnosUA (OU=AlumnosUA)
	 * @param dto
	 * @return
	 */
	private boolean esCuentaCreadaXpanel(AdCuentasCreadasDTO dto) {
		boolean esCuentaCreadaXpanel = false;
		UsuarioResponse datos = consultaUsuario(dto);
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
		logger.info(String.format("esCuentaCreadaXpanel: esCuentaCreadaXpanel=%b samaccount_name=%s dto: %s",
				esCuentaCreadaXpanel, dto.getSammacountName(), dto));
		return esCuentaCreadaXpanel;
	}
	/**
	 * retorna TRUE si pudo actualizar, FALSE si no, deja msgError
	 * @param dto
	 * @return
	 */
	private boolean actualizaCuenta(AdCuentasCreadasDTO dto) {
		boolean resultado = false;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/actualizarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ActualizarUsuario", 
				null, 
				new Usuario(dto.getSammacountName(),null,null,null,null,null,null,null,null,
						dto.getPidm(), dto.getNivel(), dto.getEstado()));
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) actualizarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("actualizar cuenta para samaccountName: %s msg: %s",
						dto.getSammacountName(), response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje())) {
					resultado = true;
					registraBd(dto, String.format("actualiza cuenta para samaccountName: %s", dto.getSammacountName()));
					headers.clear();
					headers.put("samaccountName", dto.getSammacountName());
					headers.put("rut", dto.getRut());
					updateAdCuentasCreadas.requestBodyAndHeaders(null, headers);
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
		}
		if (!resultado)
			registraBd(dto, String.format("error en ActualizaUsuario (%s) msg: %s", dto.getSammacountName(), getMsgError()));
		return resultado;
	}


	/**
	 * retorna TRUE si pudo eliminar y crear, FALSE si no, deja msgError
	 * @param dto
	 * @return
	 */
	private boolean eliminaCreaCuenta(AdCuentasCreadasDTO dto) {
		boolean resultado = false;
		UsuarioResponse usuarioResponse = consultaUsuario(dto);
		if (usuarioResponse != null) {
			// eliminar cuenta con el @
			resultado = eliminaCuenta(dto);
			if (resultado)
				// crear cuenta sin @
				resultado = crearCuenta(dto, usuarioResponse);
		}
		if (resultado)
			registraBd(dto, String.format("recreada cuenta %s para %s", dto.getSammacountName().substring(1), dto.getRut()));
		else
			registraBd(dto, String.format("error al recrear %s msg: %s", dto.getSammacountName(), getMsgError()));
		return resultado;
	}

	private void registraBd(AdCuentasCreadasDTO dto, String resultado) {
		registraBd(dto.getSammacountName(), resultado);
	}
	
	protected void registraBd(String samaccountName, String resultado) {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("id", samaccountName);
		headers.put("resultado", resultado);
		insertWrkPlanilla.requestBodyAndHeaders(null, headers);
	}


	/**
	 * recuperar los datos de la cuenta @ para crear cuenta sin @
	 * @param dto
	 * @return
	 */
	private UsuarioResponse consultaUsuario(AdCuentasCreadasDTO dto) {
		return consultaUsuario(dto.getSammacountName());
	}
	
	protected UsuarioResponse consultaUsuario(String samaccountName) {
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
			logger.error(msg, e);
			setMsgError(msg);
		}
		return usuarioResponse;
	}

	/**
	 * Elimina la cuenta con @
	 * @param dto
	 * @return
	 */
	private boolean eliminaCuenta(AdCuentasCreadasDTO dto) {
		boolean resultado = false;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/eliminarUsuario");
		headers.put("CamelHttpMethod", "POST");
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("EliminarUsuario", 
				null, 
				Usuario.createUsuario4validar(dto.getSammacountName()));
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) eliminarUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			if (response.getCodigo() == 0) {
				logger.info(String.format("eliminar cuenta para samaccountName: %s msg: %s",
						dto.getSammacountName(), response.getMensaje()));
				if ("OK".equalsIgnoreCase(response.getMensaje())) {
					resultado = true;
					eliminarUsuarioAD.requestBodyAndHeader(null, "samaccountName", dto.getSammacountName());
				} else
					setMsgError(String.format("WS eliminar responde %s", response.getMensaje()));
			} else {
				String msg = String.format("ERROR: no se elimino cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para actualizar usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
		}
		
		return resultado;
	}


	/**
	 * Crea nueva cuenta para @ sin @ en samaccountName
	 * @param dto
	 * @param usuarioResponse
	 * @return
	 */
	private boolean crearCuenta(AdCuentasCreadasDTO dto, UsuarioResponse usuarioResponse) {
		boolean resultado = false;
		Map<String,Object> headers = new HashMap<String,Object>();
		// validar el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, "http://localhost:8181/cxf/ESB/panel/serviciosADv3/crearUsuario");
		headers.put("CamelHttpMethod", "POST");
		Usuario usuario = new Usuario(dto.getRut().substring(1), soloDebug?"12345678,A#":dto.getPassword(), "AlumnosUA", dto.getRut().substring(1),
				usuarioResponse.getNombre(), usuarioResponse.getApellidos(), usuarioResponse.getCorreo(),
				usuarioResponse.getDireccion(), usuarioResponse.getComuna(), dto.getPidm(), dto.getNivel(), dto.getEstado());
		
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
				if ("OK".equalsIgnoreCase(response.getMensaje())) {
					resultado = true;
					// crear en la tabla
					headers.clear();
					headers.put("samaccountName", usuario.getCuenta());
					headers.put("rut", dto.getRut());
					headers.put("ou", String.format("cn=%s %s,ou=%s creada", usuario.getNombre(), usuario.getApellidos(), dto.getRut()));
					insertAdCuentasCreadas.requestBodyAndHeaders(null, headers);
					deleteAdCuentasCreadas.requestBodyAndHeader(null, "samaccountName", dto.getRut());
				} else
					setMsgError(String.format("WS crear cuenta responde %s", response.getMensaje()));
			} else {
				String msg = String.format("ERROR: no se creo cuenta cuenta AD %s", response);
				setMsgError(msg);
				logger.error(msg);
			}
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para crear usuario AD. request=%s", request);
			logger.error(msg, e);
			setMsgError(msg);
		}
		
		return resultado;
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
