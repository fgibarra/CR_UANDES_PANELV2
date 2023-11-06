package cl.uandes.panel.servicio.crearCuentas.procesor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.Usuario;
import cl.uandes.panel.comunes.servicios.dto.CuentasMiUandes;
import cl.uandes.panel.comunes.servicios.dto.DatosCuentasBanner;
import cl.uandes.panel.comunes.utils.ObjectFactory;

/**
 * Proceso que crea la cuenta en el Active Directory
 * @author fernando
 *
 */
public class CuentasThread extends cl.uandes.panel.comunes.utils.RegistrosEnBD implements Processor {

    @PropertyInject(value = "crear-cuentas-azure.uri-servicios-ad", defaultValue="http://localhost:8181/cxf/ESB/panelV3/azureServices")
	private String adServices;

	@EndpointInject(uri = "sql:classpath:sql/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate creaMiResultado;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;
	@EndpointInject(uri = "sql:classpath:sql/actualizarKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarKcoFunciones;


	@PropertyInject(value = "servicios-ad.operacion.crearUsuario", defaultValue="CrearUsuario")
	private String sevicioCrearCuenta;
    @PropertyInject(value = "servicios-ad.operacion.validarUsuario", defaultValue="ValidarUsuario")
    private String validarUsuario;
    @PropertyInject(value = "crear-cuentas-azure.ramaAD", defaultValue="AlumnosUA")
    private String ramaAD;
	
	@EndpointInject(uri = "cxfrs:bean:rsADconsultaXrut?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testCreateGrupoAzure")
	ProducerTemplate consultaRutAD;
	String templateConsultaXrut = "%s/consultaXrut";
	
	@EndpointInject(uri = "cxfrs:bean:rsADcrearUsuario?continuationTimeout=-1")
	ProducerTemplate createCuentaAzure;
	String templateCreateCuentaAzure = "%s/crearUsuario";
	
	@EndpointInject(uri = "cxfrs:bean:rsADvalidarUsuario?continuationTimeout=-1")
	ProducerTemplate validarUsuarioAD;
	String templateValidarUsuarioAD = "%s/validarUsuario";
	
	@EndpointInject(uri = "sql:classpath:sql/qryCuentaAzure.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryCuentaAzure;
	
	@EndpointInject(uri = "sql:classpath:sql/insertCuentaAzure.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertCuentaAzure;
	
	@EndpointInject(uri = "sql:classpath:sql/getLikeLoginName.sql?dataSource=#bannerDataSource")
	ProducerTemplate getLikeLoginName;
	
	Integer keyResultado;
	String mensajeError;
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 1. Valida si el alumno tiene una cuenta registrada en la tabla MI_CUENTAS_AZURE
     * 2. Si no la tiene:
     *    2.1. Busca en al AD por RUT con el web service LDAP/src/wsLdapUandespanel.php si tiene una cuenta creada
     *    2.2. Si la tiene, se crea una entrada en la tabla MI_CUENTAS_AZURE
     *    2.3. Si no tiene, se crea una cuenta en el AD con el web service LDAPServiceb/LDAPService.svc y se crea una entrada en la tabla MI_CUENTAS_AZURE
     * 3. Si existe en MI_CUENTAS_AZURE, se valida que exista la cuenta en el AD buscando por RUT con el 
     *    web service LDAP/src/wsLdapUandespanel.php
     *    3.1. Si no existe cuenta en el AD, se crea una con el web service LDAPServiceb/LDAPService.svc
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		keyResultado = (Integer) message.getHeader("keyResultado");
		Map<String, Object> headers= new HashMap<String, Object>();
		
		logger.info(String.format("keyResultado=%d", keyResultado));

		DatosCuentasBanner dto = (DatosCuentasBanner)message.getHeader("DatosCuentasBanner");
		headers.put("rut", dto.getSpridenId());
		logger.info(String.format("rut=%s", headers.get("rut")));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryCuentaAzure.requestBodyAndHeaders(null, headers);
		@SuppressWarnings("unused")
		CuentasMiUandes cuenta = null;
		logger.info(String.format("leyo %d datos", datos != null?datos.size():-1));
		if (datos != null && datos.size() > 0) {
			// cuenta existe en MI_CUENTAS_AZURE
			cuenta = new CuentasMiUandes(datos.get(0));
			if (procesaCuentaExisteEnMiCuentasMiUandes(dto, exchange))
				incProcesados(message);
			else
				incErrores(message);
		} else
			// cuenta NO existe en MI_CUENTAS_AZURE
			if (procesaCuentaNoExisteEnMiCuentasMiUandes(dto, exchange))
				incProcesados(message);
			else
				incErrores(message);
	}

	/**
	 * 1.- se valida que exista la cuenta en el AD buscando por RUT con el web service LDAP/src/wsLdapUandespanel.php
	 * 2.- Si no existe cuenta en el AD, se crea una con el web service LDAPServiceb/LDAPService.svc
	 * @param cuenta
	 * @param exchange
	 */
	private boolean procesaCuentaExisteEnMiCuentasMiUandes(DatosCuentasBanner dto, Exchange exchange) {
		boolean resultado = false;
		Message message = exchange.getIn();
		logger.info(String.format("procesaCuentaExisteEnMiCuentasMiUandes: va a consultar por rut %s", dto.getSpridenId()));
		ConsultaXrutResponse response = consultaXrut(dto.getSpridenId());
		logger.info(String.format("procesaCuentaExisteEnMiCuentasMiUandes: response de consultaXrut: %s", response.getEstado()));
		if (!"OK".equals(response.getEstado())) {
			// NO existe cuenta en AD --> crearla
			if (crearCuentaAD(dto, "existeEnBD")) {
				resultado = true;
				incAgregadosAD(message);
			}
		} else if ("OK".equals(response.getEstado())) {
			resultado = true;
		}
			
		return resultado;
	}

	/**
	 * 1.- Busca en al AD por RUT con el web service LDAP/src/wsLdapUandespanel.php si tiene una cuenta creada
	 * 2.- Si la tiene, se crea una entrada en la tabla MI_CUENTAS_AZURE
	 * 3.- Si no tiene, se crea una cuenta en el AD con el web service LDAPServiceb/LDAPService.svc y se crea 
	 *     una entrada en la tabla MI_CUENTAS_AZURE
	 * @param rut
	 * @param exchange
	 */
	private boolean procesaCuentaNoExisteEnMiCuentasMiUandes(DatosCuentasBanner dto, Exchange exchange) {
		boolean resultado = false;
		logger.info(String.format("procesaCuentaNoExisteEnMiCuentasMiUandes: va a consultar por rut %s", dto.getSpridenId()));
		ConsultaXrutResponse response = consultaXrut(dto.getSpridenId());
		logger.info(String.format("procesaCuentaNoExisteEnMiCuentasMiUandes: response.getEstado()=%s", response.getEstado()));
		if ("OK".equals(response.getEstado())) {
			// agregar el entry en la tabla
			if (insertarMiCuentasMiUandes(dto)) {
				resultado = true;
				incAgregadosBD(exchange.getIn());
			} else {
				// registrar el error que no pudo insertar en MI_CUENTAS_AZURE
				List<String> args = new ArrayList<String>();
				args.add("--tipo");
				args.add(String.format("%s", getClass().getSimpleName()));
				args.add("--idUsuario");
				args.add(String.format("%s", dto.getSpridenId()));
				args.add("--causa");
				args.add(String.format("%s", getMensajeError()));
				//
				StringBuffer sb = new StringBuffer();
				for (int i=0; i<args.size(); i++)
					sb.append(args.get(i)).append(' ');
				logger.info(String.format("procesaCuentaNoExisteEnMiCuentasMiUandes: fallo ws para crear en AD. %s %d",
						sb.toString(), keyResultado));
				registraError(args.toArray(new String[0]), keyResultado);
			}
		} else {
			if (crearCuentaAD(dto, "noExisteEnBD") && insertarMiCuentasMiUandes(dto)) {
				resultado = true;
				incAgregadosBD(exchange.getIn());
				incAgregadosAD(exchange.getIn());
			}
		}
		
		return resultado;
	}

	private boolean crearCuentaAD(DatosCuentasBanner dto, String existe) {
		logger.info(String.format("crearCuentaAD: existe=%s DatosCuentasBanner: %s", existe, dto));
		
		ServiciosLDAPRequest request = new ServiciosLDAPRequest(getSevicioCrearCuenta(), null, factoryUsuario(dto, existe));
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCreateCuentaAzure, getAdServices()));
		headers.put("CamelHttpMethod", "POST");
		logger.info(String.format("reateCuentaAzure URL: %s", headers.get(Exchange.DESTINATION_OVERRIDE_URL)));
		logger.info(String.format("reateCuentaAzure: request: %s", request));

		ServiciosLDAPResponse response;
		try {
			response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl((ResponseImpl)createCuentaAzure.requestBodyAndHeaders(request, headers), ServiciosLDAPResponse.class);
		} catch (Exception e) {
			logger.error("consultaXrut", e);
			response = new ServiciosLDAPResponse(-1, e.getMessage());
		}
		//ServiciosLDAPResponse response = (ServiciosLDAPResponse) createCuentaAzure.requestBodyAndHeaders(request, headers);
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
			for (int i=0; i<args.size(); i++)
				sb.append(args.get(i)).append(' ');
			logger.info(String.format("crearCuentaAD: fallo ws para crear en AD. %s %d",
					sb.toString(), keyResultado));
			registraError(args.toArray(new String[0]), keyResultado);
			return false;
		}
		return true;
	}

	/**
	 * El nombre de la cuenta no debe existir ni en MI_CUENTAS_AZURE ni en el A.D
	 * @param dto
	 * @return
	 */
	private Usuario factoryUsuario(DatosCuentasBanner dto, String existeNombre) {
		logger.info(String.format("factoryUsuario: caso %s", existeNombre));
		String nombreCuenta = dto.getLoginName();
		if ("noExisteEnBD".equals(existeNombre)) {
			String nombreCuentaLike = nombreCuenta+"%";
			logger.info(String.format("factoryUsuario: nombreCuenta=%s", nombreCuentaLike));
			// verificar si no hay repetidos en MI_CUENTAS_AZURE
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datos = (List<Map<String, Object>>)getLikeLoginName.requestBody(nombreCuentaLike);
			Integer maxSeq = 0;
			String base = "";
			logger.info(String.format("factoryUsuario: encontro %d hits", (datos != null && datos.size() > 0)? datos.size() : 0));
			if (datos != null && datos.size() > 0) {
				dto.setNombreCuenta(nombreCuenta);
				for (Map<String, Object> map : datos) {
					String loginName = (String)map.get("LOGIN_NAME");
					logger.info(String.format("loginName %s", loginName));
					StringBuilder sb = new StringBuilder();
					char chars[] = new char[loginName.length()];
					loginName.getChars(0,loginName.length(),chars,0);
					@SuppressWarnings("unused")
					int i=chars.length - 1;
					for (; i > 0; i--) {
						char cc = chars[i];
						if (cc >= '0' && cc <= '9')
							sb.append(cc);
						else {
							base = loginName.substring(0, i+1);
							break;
						}
					}
					
					logger.info(String.format("base %s sb.length()=%d", base, sb.length()));

					Integer valor = null;
					if (sb.length() > 0) {
						String seq = sb.reverse().toString();
						valor = Integer.valueOf(seq);
						if (valor > maxSeq) {
							maxSeq = valor;
							dto.setSecuencia(maxSeq);
						}
					}
					
				}
				nombreCuenta = String.format("%s%02d", nombreCuenta, ++maxSeq);
			}
					
			// Validar si se encuentra ocupado en Active Directory
			boolean existe = true;
			do {
				Map<String, Object> headers = new HashMap<String, Object>();
				headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateValidarUsuarioAD, getAdServices()));
				headers.put("CamelHttpMethod", "POST");
				ServiciosLDAPRequest requestValidarUsuario =  createValidarUsuarioRequest(getValidarUsuario(), nombreCuenta);
				//ServiciosLDAPResponse response = (ServiciosLDAPResponse)validarUsuarioAD.requestBodyAndHeaders(requestValidarUsuario, headers);
				ServiciosLDAPResponse response;
				try {
					response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl((ResponseImpl)validarUsuarioAD.requestBodyAndHeaders(requestValidarUsuario, headers), ServiciosLDAPResponse.class);
				} catch (Exception e) {
					logger.error("consultaXrut", e);
					response = new ServiciosLDAPResponse(-1, e.getMessage());
				}
				if (response.getCodigo() == 0) {
					if (!"OK".equals(response.getMensaje())) {
						existe = false;
					} else {
						if (maxSeq != 0) {
							nombreCuenta = String.format("%s%02d", base, ++maxSeq);
							dto.setSecuencia(maxSeq);
						}
					}
				}
			} while (existe);
			
			dto.setNombreCuenta(nombreCuenta);
		}
		return Usuario.createUsuario4crear(nombreCuenta, dto.getPassword(), getRamaAD(), dto.getRut(), dto.getNombres(), dto.getApellidos());
	}
	
	private ServiciosLDAPRequest createValidarUsuarioRequest(String servicio, String nombreCuenta) {
		Usuario usuario = Usuario.createUsuario4validar(nombreCuenta);
		return new ServiciosLDAPRequest(getValidarUsuario(), null, usuario);
	}

	private boolean insertarMiCuentasMiUandes(DatosCuentasBanner dto) {
		boolean resultado = true;
		logger.info(String.format("insertarMiCuentasMiUandes: DatosCuentasBanner: %s", dto));
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("rut", dto.getSpridenId());
		headers.put("pidm", ObjectFactory.toBigDecimal(dto.getSpridenPidm()));
		headers.put("loginName", dto.getLoginName());
		headers.put("nombres", dto.getNombres());
		headers.put("apellidos", dto.getApellidos());
		try {
			insertCuentaAzure.requestBodyAndHeaders(null, headers);
		} catch (CamelExecutionException e) {
			logger.error("insertarMiCuentasMiUandes", e);
			setMensajeError(String.format("Error insertar:%s - datos a insertar: %s",e.getMessage(), dto.toString()));
			resultado = false;
		}
		return resultado;
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
			response = (ConsultaXrutResponse) ObjectFactory.procesaResponseImpl((ResponseImpl)consultaRutAD.requestBodyAndHeaders(request, headers), ConsultaXrutResponse.class);
		} catch (Exception e) {
			logger.error("consultaXrut", e);
			response = new ConsultaXrutResponse(-1, e.getMessage(),null,null,null,null,null,null,null,null,null,null);
		}
		return response;
	}

	public String getSevicioCrearCuenta() {
		return sevicioCrearCuenta;
	}

	public void setSevicioCrearCuenta(String sevicioCrearCuenta) {
		this.sevicioCrearCuenta = sevicioCrearCuenta;
	}
	
	public void incProcesados(Message message) {
		message.setHeader("countProcesados", (Integer)message.getHeader("countProcesados") + 1);
		logger.info(String.format("incProcesados: %d", message.getHeader("countProcesados")));
	}
	public void incErrores(Message message) {
		message.setHeader("countErrores", (Integer)message.getHeader("countErrores") + 1);
		logger.info(String.format("incErrores: %d", message.getHeader("countErrores")));
	}

	public void incAgregadosBD(Message message) {
		message.setHeader("countAgregadosBD", (Integer)message.getHeader("countAgregadosBD") + 1);
		logger.info(String.format("incAgregadosBD: %d", message.getHeader("countAgregadosBD")));
	}

	public void incAgregadosAD(Message message) {
		message.setHeader("countAgregadosAD", (Integer)message.getHeader("countAgregadosAD") + 1);
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
}
