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
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.BdCuentasSinActualizarDTO;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.ContadoresActualizaCuentas;

public class PareaCuentas implements Processor {

	@PropertyInject(value = "actualizar-cuentas-AD.debug", defaultValue = "false")
	protected String debug;
	protected Boolean soloDebug = Boolean.valueOf(debug); //  true --> NO envia

	@EndpointInject(uri = "cxfrs:bean:rsADvalidarUsuario") // validar cuenta AD
	ProducerTemplate validarUsuarioAD;
	@EndpointInject(uri = "cxfrs:bean:rsConsultaXrut") // consulta por RUT al AD
	ProducerTemplate consultaXrut;
	
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

		BdCuentasSinActualizarDTO dto = (BdCuentasSinActualizarDTO)message.getHeader("BdCuentasSinActualizarDTO");
		logger.info(String.format("%s", dto));
		
		int resultado = existeCuenta(dto);
		if (resultado == 0) {
			// No existe samaccount_name en AD --> marcar status
			
			// existe RUT en AD -->
			
		} else if (resultado == 1) {
			// Existe
		} else {
			// error al recuperar
		}
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.decCounter();
	}

	/**
	 * Retorna 1 si existe el samaccountName en el AD, 0 si no existe, 2 si hay error
	 * @param dto
	 * @return
	 */
	private int existeCuenta(BdCuentasSinActualizarDTO dto) {
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
