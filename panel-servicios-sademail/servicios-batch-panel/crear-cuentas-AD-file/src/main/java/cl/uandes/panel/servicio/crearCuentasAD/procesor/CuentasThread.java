package cl.uandes.panel.servicio.crearCuentasAD.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentasAD;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.Usuario;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutResponse;
import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;

public class CuentasThread implements Processor {

	private RegistrosComunes registrosComunes;
	@PropertyInject(value = "crear-cuentas-AD.debug", defaultValue = "false")
	private String debug;
	@PropertyInject(value = "uri.serviciosAD", defaultValue = "http://localhost:8181/cxf/ESB/panel/serviciosAD")
	private String adServices;

	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario") // crear cuenta AD
	ProducerTemplate crearUsuarioAD;
	private final String uriADcrearUsuarioAD = "http://localhost:8181/cxf/ESB/panel/serviciosAD/crearUsuario";
	// SQLs
	@EndpointInject(uri = "sql:classpath:sql/estaEnBdc.sql?dataSource=#bannerDataSource")
	ProducerTemplate estaEnBdc;
	@EndpointInject(uri = "sql:classpath:sql/updateBdcUsuarioMillenium.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateBdcUsuarioMillenium;
	@EndpointInject(uri = "sql:commit?dataSource=#bannerDataSource")
	ProducerTemplate commit;
	
	private ContadoresCrearCuentasAD contadoresCuentasAD;
	private Logger logger = Logger.getLogger(getClass());
	private CuentasADDTO cuentasADDTO;
	
	/**
	 * - Verifica que el RUT co tenga una cuenta AD asociada.
	 * - Si no tiene:
	 * 		- Completa el DTO solicitando un samaccountName
	 * 			Usa el servicio getSamaccountName de RegistrosComunes para generar un nombre de cuenta unico y no utilizado
	 * 			para el AD.
	 * 		- Invoca al API para crear la cuenta en el AD
	 * - si tiene, se usa la que tiene
	 * - Actualiza la BDC con la nueva cuenta.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		CountThreads countThread = (CountThreads) message.getHeader("countThread");

		try {
			cuentasADDTO = (CuentasADDTO) message.getHeader("CuentasADDTO");
			contadoresCuentasAD = (ContadoresCrearCuentasAD)message.getHeader("contadoresCuentasAD");
			contadoresCuentasAD.incCountProcesados();
			ResultadoFuncion res = (ResultadoFuncion) message.getHeader("ResultadoFuncion");
			
			int existe = existeCuentaAD();
			if (existe == 0) { // respondio en ws que no esta en el AD
				String samaccountName = null;
				try {
					/*
					 * Cambiado el 08-03-24 a peticion de Fco Fiogueroa
					 * 
					samaccountName = registrosComunes.getSamaccountName(cuentasADDTO, exchange);
					 */
					samaccountName = cuentasADDTO.getRut();
					
					/*       fin del cambio                                        */
					
					logger.info(String.format("en el message cuentasADDTO: %s", (CuentasADDTO) message.getHeader("CuentasADDTO")));
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
				logger.info(String.format("CuentasThread: samaccountName=%s cuentasADDTO=%s", samaccountName, cuentasADDTO));
				
				final Map<String,Object> headers = new HashMap<String,Object>();
				// crear el usuario en el AD
				headers.put(Exchange.DESTINATION_OVERRIDE_URL, uriADcrearUsuarioAD);
				headers.put("CamelHttpMethod", "POST");
				
				ServiciosLDAPRequest request = new ServiciosLDAPRequest("CrearUsuario", null, Usuario.createUsuario4crear(
								cuentasADDTO.getRut(), 
//								cuentasADDTO.getSamaccountName(), //Cambiado el 08-03-24 a peticion de Fco Fiogueroa
								cuentasADDTO.getPassword(),
								cuentasADDTO.getRama(),
								cuentasADDTO.getEmployeeId(),
								cuentasADDTO.getNombres(),
								cuentasADDTO.getApellidos()));
				logger.info(String.format("asi quedaria la invocacion para crear la cuenta: %s", request));
				
				if (!Boolean.valueOf(getDebug())) {
					// Si no se definio debug o esta en false
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
				}
			} else if (existe == 2) {
				// se produjo un error en el WS
				return;
			}
			// Actualizar la BDC
			try {
				actualizarBDC(cuentasADDTO);
			} catch (Exception e) {
				String msg = String.format("Error al actualizar BDC para usuario. cuentasADDTO=%s", cuentasADDTO);
				logger.error(msg, e);
				registrosComunes.registraMiResultadoErrores(null, msg, e, null, res.getKey());
				contadoresCuentasAD.incCountErrores();
			}
		} finally {
			countThread.decCounter();
		}
	}

	@EndpointInject(uri = "cxfrs:bean:rsADconsultaXrut?continuationTimeout=-1")
	ProducerTemplate consultaRutAD;
	String templateConsultaXrut = "%s/consultaXrut";
	private int existeCuentaAD() {
		int existe = 0;
		ConsultaXrutRequest request = new ConsultaXrutRequest(cuentasADDTO.getEmployeeId());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateConsultaXrut, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("consultaXrut URL: %s", headers.get(Exchange.DESTINATION_OVERRIDE_URL)));
		logger.info(String.format("consultaXrut: request: %s", request));
		ConsultaXrutResponse response;
		try {
			response = (ConsultaXrutResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) consultaRutAD.requestBodyAndHeaders(request, headers), ConsultaXrutResponse.class);
		} catch (Exception e) {
			logger.error("consultaXrut", e);
			response = new ConsultaXrutResponse(-1, e.getMessage(), null, null, null, null, null, null, null, null,
					null, null);
			existe = 2;
		}
		if (response.getCodigo() == 0 && response.getEstado().equalsIgnoreCase("OK")) {
			existe = 1; 
			cuentasADDTO.setLoginName(response.getUsuario());
		}
		return existe;
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
		logger.info(String.format("actualizarBDC: update bdc_usuario_millenium set usuario_ad=%s, userid_alma=%s, passwd_alma=%s where spriden_id=%s", 
				cuentasADDTO.getSamaccountName(),cuentasADDTO.getEmployeeId(), cuentasADDTO.getPassword(), cuentasADDTO.getRut()));
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
			commit.requestBody(null);
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

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getAdServices() {
		return adServices;
	}

	public void setAdServices(String adServices) {
		this.adServices = adServices;
	}

}
