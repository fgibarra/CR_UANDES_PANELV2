package cl.uandes.panel.servicio.crearCuentasAD.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentasAD;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.Usuario;
import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;

public class CuentasThread implements Processor {

	private RegistrosComunes registrosComunes;

	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario") // crear cuenta AD
	ProducerTemplate crearUsuarioAD;
	private final String uriADcrearUsuarioAD = "http://localhost:8181/cxf/ESB/panel/serviciosAD/crearUsuario";
	// SQLs
	@EndpointInject(uri = "sql:classpath:sql/estaEnBdc.sql?dataSource=#bannerDataSource")
	ProducerTemplate estaEnBdc;
	@EndpointInject(uri = "sql:classpath:sql/updateBdcUsuarioMillenium.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateBdcUsuarioMillenium;
	
	private ContadoresCrearCuentasAD contadoresCuentasAD;
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * - Completa el DTO solicitando un samaccountName
	 * 		Usa el servicio getSamaccountName de RegistrosComunes para generar un nombre de cuenta unico y no utilizado
	 * 		para el AD.
	 * - Invoca al API para crear la cuenta en el AD
	 * - Actualiza la BDC con la nueva cuenta.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		CuentasADDTO cuentasADDTO = (CuentasADDTO) message.getHeader("CuentasADDTO");
		contadoresCuentasAD = (ContadoresCrearCuentasAD)message.getHeader("contadoresCuentasAD");
		contadoresCuentasAD.incCountProcesados();
		ResultadoFuncion res = (ResultadoFuncion) message.getHeader("ResultadoFuncion");
		
		String samaccountName = null;
		try {
			samaccountName = registrosComunes.getSamaccountName(cuentasADDTO, exchange);
			if (samaccountName == null) {
				// no pudo generar uno
				String msg = String.format("Error: no pudo crear un samaccountName para %s",  cuentasADDTO);
				logger.error(msg);
				contadoresCuentasAD.incCountErrores();
				registrosComunes.registraMiResultadoErrores(cuentasADDTO.getRut(), "CreaCuentasAD", msg, null, res.getKey());
				return;
			}
		} catch (Exception e1) {
			String msg = String.format("Error al procesar %s",  cuentasADDTO);
			logger.error(msg, e1);
			contadoresCuentasAD.incCountErrores();
			registrosComunes.registraMiResultadoErrores(null, msg, e1, null, res.getKey());
			return;
		}
		logger.info(String.format("samaccountName=%s cuentasADDTO=%s", samaccountName, cuentasADDTO));
		
		final Map<String,Object> headers = new HashMap<String,Object>();
		// crear el usuario en el AD
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, uriADcrearUsuarioAD);
		headers.put("CamelHttpMethod", "POST");
		
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("CrearUsuario", null, Usuario.createUsuario4crear(
						cuentasADDTO.getSamaccountName(), 
						cuentasADDTO.getPassword(),
						cuentasADDTO.getRama(),
						cuentasADDTO.getEmployeeId(),
						cuentasADDTO.getNombres(),
						cuentasADDTO.getApellidos()));
		logger.info(String.format("asi quedaria la invocacion para crear la cuenta: %s", request));
		/*
		@SuppressWarnings("unused")
		ServiciosLDAPResponse response = null;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) crearUsuarioAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
			contadoresCuentasAD.incCountAgregadosAD();
		} catch (Exception e) {
			String msg = String.format("Error al invocar api para crear usuario AD. request=%s", request);
			logger.error(msg, e);
			registrosComunes.registraMiResultadoErrores(null, msg, e, null, res.getKey());
			contadoresCuentasAD.incCountErrores();
		}

		try {
			actualizarBDC(cuentasADDTO);
		} catch (Exception e) {
			String msg = String.format("Error al actualizar BDC para usuario. cuentasADDTO=%s", cuentasADDTO);
			logger.error(msg, e);
			registrosComunes.registraMiResultadoErrores(null, msg, e, null, res.getKey());
			contadoresCuentasAD.incCountErrores();
		}
		*/
	}

	/**
	 * Verifica que el rut este en la BDC
	 * si esta actualiza los datos del AD
	 * 
	 * @param cuentasADDTO
	 * @throws Exception
	 */
	private void actualizarBDC(CuentasADDTO cuentasADDTO) throws Exception {
		Boolean esta = Boolean.FALSE;
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) estaEnBdc.requestBodyAndHeader(null, "rut", cuentasADDTO.getRut());
		if (datos != null && datos.size() > 0) {
			esta = Boolean.valueOf((String)(datos.get(0).get("ESTA_EN_BDC")));
			logger.info(String.format("actualizarBDC: estaEnBdc=%b", esta));
		}
		if (esta) {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("sammacount", cuentasADDTO.getSamaccountName());
			headers.put("employeeid", cuentasADDTO.getEmployeeId());
			headers.put("password", cuentasADDTO.getPassword());
			headers.put("rut", cuentasADDTO.getRut());
			updateBdcUsuarioMillenium.requestBodyAndHeaders(null, headers);
			contadoresCuentasAD.incCountAgregadosBD();
			logger.info(String.format("actualizarBDC: sammacount=%s employeeid=%s password=%s rut=%s",
					cuentasADDTO.getSamaccountName(),cuentasADDTO.getEmployeeId(),
					cuentasADDTO.getPassword(), cuentasADDTO.getRut()));
		}
	}

	//===============================================================================================================
	// Getters y Setters
	//===============================================================================================================

	public RegistrosComunes getRegistrosComunes() {
		return registrosComunes;
	}

	public void setRegistrosComunes(RegistrosComunes registrosComunes) {
		this.registrosComunes = registrosComunes;
	}

}
