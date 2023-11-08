package cl.uandes.panel.servicio.crearCuentas.procesor;

import java.util.ArrayList;
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

import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.Usuario;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;
import cl.uandes.panel.comunes.servicios.dto.DatosCuentasBanner;
import cl.uandes.panel.comunes.utils.ObjectFactory;

/**
 * Proceso que crea la cuenta en el Active Directory
 * 
 * @author fernando
 *
 */
public class CuentasThread extends cl.uandes.panel.comunes.utils.RegistrosEnBD implements Processor {

	@PropertyInject(value = "crear-cuentas-gmail.uri-servicios-ad", defaultValue = "http://localhost:8181/cxf/ESB/panel/serviciosAD")
	private String adServices;
	@PropertyInject(value = "servicios-ad.operacion.crearUsuario", defaultValue = "CrearUsuario")
	private String sevicioADCrearCuenta;
	@PropertyInject(value = "servicios-ad.operacion.validarUsuario", defaultValue = "ValidarUsuario")
	private String validarUsuario;
	@PropertyInject(value = "crear-cuentas-gmail.ramaAD", defaultValue = "AlumnosUA")
	private String ramaAD;
	@PropertyInject(value = "crear-cuentas-gmail.uri-servicio-crear-cuentas-panel", defaultValue = "http://localhost:8181/cxf/ESB/panel/panelToolCreaCuenta/creaCuenta")
	private String sevicioCrearCuentaPanel;

	// SQLs
	@EndpointInject(uri = "sql:classpath:sql/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate creaMiResultado;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;
	@EndpointInject(uri = "sql:classpath:sql/actualizarKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarKcoFunciones;
	@EndpointInject(uri = "sql:classpath:sql/qryCuentaGmail.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryCuentaGmail;
	/*
	 * @EndpointInject(uri =
	 * "sql:classpath:sql/insertCuentaGmail.sql?dataSource=#bannerDataSource")
	 * ProducerTemplate insertCuentaAzure;
	 * 
	 * @EndpointInject(uri =
	 * "sql:classpath:sql/getLikeLoginName.sql?dataSource=#bannerDataSource")
	 * ProducerTemplate getLikeLoginName;
	 */
	// REST APIs
	@EndpointInject(uri = "cxfrs:bean:rsADconsultaXrut?continuationTimeout=-1")
	ProducerTemplate consultaRutAD;
	String templateConsultaXrut = "%s/consultaXrut";

	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario?continuationTimeout=-1")
	ProducerTemplate createCuentaAD;
	String templateCreateCuentaAD = "%s/crearUsuario";

	@EndpointInject(uri = "cxfrs:bean:rsADvalidarUsuario?continuationTimeout=-1")
	ProducerTemplate validarUsuarioAD;
	String templateValidarUsuarioAD = "%s/validarUsuario";

	@EndpointInject(uri = "cxfrs:bean:rsCrearCuenta?continuationTimeout=-1")
	ProducerTemplate creaCuentaGmail;
	String templateCreaCuentaGmail = "%s/creaCuenta";

	Integer keyResultado;
	String mensajeError;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 1. Valida si el alumno tiene una cuenta en el AD 
	 * 1.1. Busca en al AD por RUT con el web service LDAP/src/wsLdapUandespanel.php 
	 * 		si tiene una cuenta creada nada 
	 * 2. Si no la tiene: 
	 * 2.1. Si no tiene, se crea una cuenta en el AD con el web service LDAPServiceb/LDAPService.svc
	 * 3. Crear la cuenta en Panel
	 * 3.1. Invoca servicio crear-cuenta-panel. Este servicio valida si existe o si debe crearla.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		keyResultado = (Integer) message.getHeader("keyResultado");
		Map<String, Object> headers = new HashMap<String, Object>();

		logger.info(String.format("keyResultado=%d", keyResultado));

		DatosCuentasBanner dto = (DatosCuentasBanner) message.getHeader("DatosCuentasBanner");
		headers.put("rut", dto.getSpridenId());
		logger.info(String.format("rut=%s", headers.get("rut")));

		defineCuentaEnAD(dto, exchange);

		if (crearCuentaEnGmail(dto, exchange))
			incAgregadosBD(message);
	}

	private boolean defineCuentaEnAD(DatosCuentasBanner dto, Exchange exchange) {
		boolean resultado = false;
		Message message = exchange.getIn();
		logger.info(
				String.format("defineCuentaEnAD: va a consultar por rut %s", dto.getSpridenId()));
		ConsultaXrutResponse response = consultaXrut(dto.getSpridenId());
		logger.info(String.format("defineCuentaEnAD: response de consultaXrut: %s",
				response.getEstado()));
		if (!"OK".equals(response.getEstado())) {
			// NO existe cuenta en AD --> crearla
			if (crearCuentaAD(dto)) {
				resultado = true;
				incAgregadosAD(message);
			}
		} else if ("OK".equals(response.getEstado())) {
			resultado = true;
		}

		return resultado;
	}

	private boolean crearCuentaAD(DatosCuentasBanner dto) {
		logger.info(String.format("crearCuentaAD: DatosCuentasBanner: %s", dto));

		ServiciosLDAPRequest request = new ServiciosLDAPRequest(getSevicioADCrearCuenta(), null,
				Usuario.createUsuario4crear(dto.getSpridenId(), dto.getPassword(), getRamaAD(), dto.getRut(), dto.getNombres(),
						dto.getApellidos()));
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCreateCuentaAD, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("createCuenta URL: %s", headers.get(Exchange.DESTINATION_OVERRIDE_URL)));
		logger.info(String.format("createCuenta: request: %s", request));

		ServiciosLDAPResponse response;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) createCuentaAD.requestBodyAndHeaders(request, headers),
					ServiciosLDAPResponse.class);
		} catch (Exception e) {
			logger.error("consultaXrut", e);
			response = new ServiciosLDAPResponse(-1, e.getMessage());
		}
		// ServiciosLDAPResponse response = (ServiciosLDAPResponse)
		// createCuentaAzure.requestBodyAndHeaders(request, headers);
		if (response.getCodigo() != 0) {
			// se produjo un error --> registrarlo
			List<String> args = new ArrayList<String>();
			args.add("--tipo");
			args.add(String.format("%s", getClass().getSimpleName()));
			args.add("--idUsuario");
			args.add(String.format("%s", dto.getSpridenId()));
			args.add("--causa");
			args.add(String.format("%s", response.getMensaje()));
			//
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < args.size(); i++)
				sb.append(args.get(i)).append(' ');
			logger.info(String.format("crearCuentaAD: fallo ws para crear en AD. %s %d", sb.toString(), keyResultado));
			registraError(args.toArray(new String[0]), keyResultado);
			return false;
		}
		return true;
	}

	private boolean crearCuentaEnGmail(DatosCuentasBanner dto, Exchange exchange) {
		CreaCuentaRequest request = factoryCrearCuentaRequest(dto);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCreaCuentaGmail, getSevicioCrearCuentaPanel()));
		headers.put("CamelHttpMethod", "POST");
		
		CreaCuentaResponse response;
		try {
			response = (CreaCuentaResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl) consultaRutAD.requestBodyAndHeaders(request, headers), CreaCuentaResponse.class);
			if (response.getCodigo() == 0) {
				// colocar en el header los datos de la cuenta creada
				return true;
			} else
				setMensajeError(response.getMensaje());
		} catch (Exception e) {
			logger.error("crearCuentaEnGmail", e);
			List<String> args = new ArrayList<String>();
			args.add("--tipo");
			args.add(String.format("%s", getClass().getSimpleName()));
			args.add("--idUsuario");
			args.add(String.format("%s", dto.getSpridenId()));
			args.add("--causa");
			args.add(String.format("%s", e.getMessage()));
			//
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < args.size(); i++)
				sb.append(args.get(i)).append(' ');
			logger.info(String.format("crearCuentaEnGmail: fallo ws para crear en panel. %s %d", sb.toString(), keyResultado));
			registraError(args.toArray(new String[0]), keyResultado);
		}
		
		return false;
	}

	private CreaCuentaRequest factoryCrearCuentaRequest(DatosCuentasBanner dto) {
		return new CreaCuentaRequest(Boolean.TRUE, dto.getSpridenId(), null, null, null, null);
	}

	private ConsultaXrutResponse consultaXrut(String rut) {
		ConsultaXrutRequest request = new ConsultaXrutRequest(rut);
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
		}
		return response;
	}

	public void incProcesados(Message message) {
		message.setHeader("countProcesados", (Integer) message.getHeader("countProcesados") + 1);
		logger.info(String.format("incProcesados: %d", message.getHeader("countProcesados")));
	}

	public void incErrores(Message message) {
		message.setHeader("countErrores", (Integer) message.getHeader("countErrores") + 1);
		logger.info(String.format("incErrores: %d", message.getHeader("countErrores")));
	}

	public void incAgregadosBD(Message message) {
		message.setHeader("countAgregadosBD", (Integer) message.getHeader("countAgregadosBD") + 1);
		logger.info(String.format("incAgregadosBD: %d", message.getHeader("countAgregadosBD")));
	}

	public void incAgregadosAD(Message message) {
		message.setHeader("countAgregadosAD", (Integer) message.getHeader("countAgregadosAD") + 1);
		logger.info(String.format("incAgregadosAD: %d", message.getHeader("countAgregadosAD")));
	}

	public String getValidarUsuario() {
		return validarUsuario;
	}

	public void setValidarUsuario(String validarUsuario) {
		this.validarUsuario = validarUsuario;
	}

	public String getRamaAD() {
		return ramaAD;
	}

	public void setRamaAD(String ramaAD) {
		this.ramaAD = ramaAD;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	@Override
	public ProducerTemplate getKey() {
		return getKey;
	}

	@Override
	public ProducerTemplate creaMiResultado() {
		return creaMiResultado;
	}

	@Override
	public ProducerTemplate insertMiResultadoErrores() {
		return insertMiResultadoErrores;
	}

	@Override
	public ProducerTemplate actualizarKcoFunciones() {
		return actualizarKcoFunciones;
	}

	public String getAdServices() {
		return adServices;
	}

	public void setAdServices(String adServices) {
		this.adServices = adServices;
	}

	public String getSevicioADCrearCuenta() {
		return sevicioADCrearCuenta;
	}

	public void setSevicioADCrearCuenta(String sevicioADCrearCuenta) {
		this.sevicioADCrearCuenta = sevicioADCrearCuenta;
	}

	public String getSevicioCrearCuentaPanel() {
		return sevicioCrearCuentaPanel;
	}

	public void setSevicioCrearCuentaPanel(String sevicioCrearCuentaPanel) {
		this.sevicioCrearCuentaPanel = sevicioCrearCuentaPanel;
	}
}
