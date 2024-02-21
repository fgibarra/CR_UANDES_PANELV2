package cl.uandes.panel.comunes.bean;

import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.Contadores;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.batch.crearcuentas.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.json.batch.crearcuentas.Usuario;
import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.comunes.utils.StringUtilities;
import cl.uandes.sadmemail.comunes.gmail.json.AliasResponse;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class RegistrosComunes {

	String logAplicacion;
	
	@EndpointInject(uri = "sql:classpath:sql_comunes/getKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKcoFunciones;

	@EndpointInject(uri = "sql:classpath:sql_comunes/updateKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateKcoFunciones;

	@EndpointInject(uri = "sql:classpath:sql_comunes/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;

	@EndpointInject(uri = "sql:classpath:sql_comunes/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultado;

	@EndpointInject(uri = "sql:classpath:sql_comunes/actualizarMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarMiResultado;

	@EndpointInject(uri = "sql:classpath:sql_comunes/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;

	@EndpointInject(uri = "sql:classpath:sql_comunes/insertKcoLogError.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertKcoLogError;

	@EndpointInject(uri = "sql:classpath:sql_comunes/getLoginNameADNombresCuentas.sql?dataSource=#bannerDataSource")
	ProducerTemplate getLoginNameADNombresCuenta;

	@EndpointInject(uri = "sql:classpath:sql_comunes/insertNombresCuenta.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertNombresCuenta;
	
	String proceso = null;
	@PropertyInject(value = "RegistrosComunes.debug", defaultValue = "false")
	private String debug;
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * @param logAplicacion
	 */
	public RegistrosComunes(String logAplicacion) {
		super();
		this.logAplicacion = logAplicacion;
	}

	/**
	 * Inicializa KCO_FUNCIONES y MI_RESULTADO
	 * Devuelve los datos con que se inicializaron. Nulo si no pudo inicializar cualquiera de las 2 tablas
	 * @param exchange
	 * @return Map<String, Object> keys:(DatosKcoFunciones, ResultadoFuncion)
	 */
	public Map<String, Object> inicializar(Exchange exchange) {
		Message message = exchange.getIn();
		Map<String, Object> datosInicializacion = new HashMap<String, Object>();
		
		proceso = (String) message.getHeader("proceso");
		logger.info(String.format("inicializar proceso %s", proceso));
		
		DatosKcoFunciones data = initDatosKcoFunciones(proceso);
		if (data == null) return null;
		datosInicializacion.put("DatosKcoFunciones", data);

		ResultadoFuncion res = initResultadoFuncion(data);
		if (res == null) return null;
		datosInicializacion.put("ResultadoFuncion", res);
		
		return datosInicializacion;
	}
	
	/**
	 * @param proceso
	 * @return
	 */
	private DatosKcoFunciones initDatosKcoFunciones(String proceso) {
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>>datos = (List<Map<String, Object>>)getKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
			
			DatosKcoFunciones data = new DatosKcoFunciones(datos.get(0));
			
			updateKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
			
			logger.info(String.format("initDatosKcoFunciones data: %s", data));
			
			return data;
		} catch (Exception e) {
			registraError(proceso, null, e);
			return null;
		}
	}
	
	/**
	 * @param data
	 * @return
	 */
	private ResultadoFuncion initResultadoFuncion(DatosKcoFunciones data) {
		Map<String, Object> headers = new HashMap<String,Object>();
		try {
			// inicializa el resultado
			BigDecimal valor = getKey();
			ResultadoFuncion res = ObjectFactory.createResultadoFuncion(data, valor);
			
			headers.put("key", BigDecimal.valueOf(res.getKey().longValue()));
			headers.put("funcion", res.getFuncion());
			headers.put("horaComienzo", res.getHoraComienzo());
			headers.put("minThreads", BigDecimal.valueOf(res.getMinThreads().longValue()));
			headers.put("maxThreads", BigDecimal.valueOf(res.getMaxThreads().longValue()));
			
			insertMiResultado.requestBodyAndHeaders(null, headers);
			
			logger.info(String.format("initResultadoFuncion res: %s", res));
			
			return res;
			
		} catch (Exception e) {
			registraError(proceso, StringUtilities.getInstance().dumpMap(headers), e);
			return null;
		}
	}
	
	private BigDecimal getKey() {
		List<?> resRoute = (List<?>)getKey.requestBody(null);
		//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", resRoute, resRoute!=null?resRoute.getClass().getName():"es NULO"));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)resRoute.get(0);
		//logger.info(String.format("InicialiceCrearGrupos: map: %s clase: %s", map, map!=null?map.getClass().getName():"es NULO"));
		BigDecimal valor = (BigDecimal)map.get("NEXTVAL");
		//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", valor, valor!=null?valor.getClass().getName():"es NULO"));
		return valor;
	}

	private void registraError(String proceso, String apoyo, Exception e) {
		Map<String, String> map = getClaseMetodo(e);
		String clase = map.get("clase");
		String metodo = map.get("metodo");
		logger.info(String.format("registraError: proceso=%s apoyo=%s clase=%s metodo=%s",
				proceso, apoyo, clase, metodo));
		registraLogError(clase, metodo, apoyo, e, null);
	}

	public Map<String, String> getClaseMetodo(Exception e) {
		Map<String, String> map = new HashMap<String, String>();
		StackTraceElement[] elements = e.getStackTrace();
		String clase = elements[0].getClassName();;
		String metodo = elements[0].getMethodName();
		for (int i = 0; i < elements.length; i++) {
			String valor = elements[i].getClassName();
			if (valor.startsWith("cl.uandes")) {
				clase = elements[i].getClassName();
				metodo = elements[i].getMethodName();
				map.put("clase", clase);
				map.put("metodo", metodo);
				break;
			}
		}
		return map;
	}
	/**
	 * @param idUsuario
	 * @param e
	 * @param keyGrupo
	 * @param keyResultado
	 * @return
	 */
	public void registraMiResultadoErrores(String idUsuario, String apoyo, Throwable e, Integer keyGrupo, Integer keyResultado) {
		try {
			logger.info(String.format("registraMiResultadoErrores:  keyResultado=%d apoyo [%s]", keyResultado, apoyo));
			Map<String, Object> headers = new HashMap<String, Object>();
			String msg = e.getMessage();
			Map<String, String> map = getClaseMetodo((Exception) e);
			String clase = map.get("clase");
			String metodo = map.get("metodo");
			
			
			BigDecimal key = getKey();
			
			headers.put("KEY", key);
			headers.put("ID_USUARIO", idUsuario);
			headers.put("TIPO", metodo);
			headers.put("CAUSA", msg);
			headers.put("KEY_GRUPO", ObjectFactory.toBigDecimal(keyGrupo));
			headers.put("KEY_RESULTADO", ObjectFactory.toBigDecimal(keyResultado));
			
			insertMiResultadoErrores.requestBodyAndHeaders(null, headers);
			
			registraLogError(clase, metodo, apoyo, e, key.intValue());
		} catch (Exception e1) {
			logger.error("registraMiResultadoErrores", e);
		}
		
	}
	
	/**
	 * @param idUsuario
	 * @param tipo
	 * @param causa
	 * @param keyGrupo
	 * @param keyResultado
	 * @return
	 */
	public BigDecimal registraMiResultadoErrores(String idUsuario, String tipo, String causa, Integer keyGrupo, Integer keyResultado) {
		Map<String, Object> headers = new HashMap<String, Object>();
		BigDecimal key = getKey();
		headers.put("KEY", key);
		headers.put("ID_USUARIO", idUsuario);
		headers.put("TIPO", tipo);
		headers.put("CAUSA", causa);
		headers.put("KEY_GRUPO", ObjectFactory.toBigDecimal(keyGrupo));
		headers.put("KEY_RESULTADO", ObjectFactory.toBigDecimal(keyResultado));
		insertMiResultadoErrores.requestBodyAndHeaders(null, headers);
		
		return key;
	}
	
	/**
	 * @param logClase
	 * @param logMetodo
	 * @param logApoyo
	 * @param e
	 * @param keyMiResultadoErrores
	 */
	public void registraLogError(String logClase, String logMetodo, String logApoyo, Throwable e, Integer keyMiResultadoErrores) {
		logger.info(String.format("registraLogError: logClase:%s logMetodo:%s", logClase,logMetodo));
		Map<String, Object> headers = new HashMap<String, Object>();
		String excepcion = e.getClass().getCanonicalName();
		String msg = e.getMessage();
		if (msg == null) msg = "NULL";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		headers.put("logClase", logClase);
		headers.put("logMetodo", logMetodo);
		headers.put("logException", excepcion);
		headers.put("logMessage", msg);
		headers.put("logApoyo", logApoyo);
		headers.put("stacktrace", sw.toString());
		headers.put("logAplicacion", getLogAplicacion());
		headers.put("keyResultadoError", ObjectFactory.toBigDecimal(keyMiResultadoErrores));

		insertLogError(headers);
	}

	private void insertLogError(Map<String, Object> headers) {
		try {
			
			logger.info(String.format("registraLogError headers: %s", StringUtilities.getInstance().dumpMapKeys(headers)));
			insertKcoLogError.requestBodyAndHeaders(null, headers);
		} catch (Exception e) {
			logger.error("insertLogError", e);
		}
	}

	public void actualizaHoraTermino(String proceso) {
		logger.info(String.format("actualizaHoraTermino: en tabla KCO_FUNCIONES proceso=%s", proceso));
		updateKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
	}
	
	/**
	 * Dejar en el header los datos necesarios para actualizar las tablas KCO_FUNCIONES y MI_RESULTADOS
	 * @param exchange
	 */
	public void cierraMiResultados(Exchange exchange, String keyContador) {
		Message message = exchange.getIn();
		Contadores contadores = (Contadores) message.getHeader(keyContador);
		ResultadoFuncion res = (ResultadoFuncion)message.getHeader("ResultadoFuncion");
		logger.info(String.format("cierraMiResultados: keyContador=%s contadores: %s ResultadoFuncion:%s",
				keyContador, contadores, res));
		
		// MI_RESULTADOS
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("countProcesados", contadores.getCountProcesados());
		headers.put("countErrores", contadores.getCountErrores());
		headers.put("count1", contadores.getCount1());
		headers.put("count2", contadores.getCount2());
		headers.put("jsonResultado",contadores.toString());
		headers.put("keyResultado", ObjectFactory.toBigDecimal(res.getKey()));
		actualizarMiResultado.requestBodyAndHeaders(null, headers);
	}

	/**
	 * Busca un nombre para cuenta mail y AD que no haya sido usado
	 * - arma el posible nombre a partir de la primera letra del primer nombre + 
	 * 		la primera letra del segundo nombre (si existe) +
	 * 		apellido paterno.
	 * - busca que no se encuentre en tabla AD_NOMBRES_CUENTA
	 * 		si lo encuentra, agrega un numero de secuencia hasta que no exista.
	 * - busca si hay un nickname definido en gmail (si hay incrementa la secuencia)
	 * - busca si no hay una cuenta en gmail
	 * - cuando no encuentre por ningun criterio, actualiza la tabla y devuelve el nombre
	 * 
	 * @param nombres
	 * @return
	 * @throws Exception 
	 */
	public String getSamaccountName(@Header(value = "CuentasADDTO")CuentasADDTO cuentasADDTO, Exchange exchange) throws Exception {
		if (cuentasADDTO != null) {
			// recuperar un login name desde AD_NOMBRES_CUENTA que no este ocupado
			cuentasADDTO = getSamaccountName(cuentasADDTO);
			
			// verifica que no se haya ocupado como nombre de cuenta o nickname en GMAIL
			boolean esteOcupado = true;
			do {
				esteOcupado = estaOcupadoEngmail(cuentasADDTO);
				if (esteOcupado) {
					add2AdNombresCuenta(cuentasADDTO);
					cuentasADDTO.incSeq();
					cuentasADDTO.setLastLoginName();
				}
			} while (esteOcupado);

			// verifica que no este ocupado como sAmaccountName en el AD
			esteOcupado = true;
			do {
				try {
					esteOcupado = estaOcupadoEnAD(cuentasADDTO);
				} catch (Exception e) {
					throw e;
				}
				if (esteOcupado) {
					add2AdNombresCuenta(cuentasADDTO);
					cuentasADDTO.incSeq();
					cuentasADDTO.setLastLoginName();
				}
			} while (esteOcupado);

			add2AdNombresCuenta(cuentasADDTO);
		}
		logger.info(String.format("getSamaccountName devuelve %s", cuentasADDTO.getSamaccountName()));
		exchange.getIn().setHeader("CuentasADDTO", cuentasADDTO);
		return cuentasADDTO.getSamaccountName();
	}
	
	private CuentasADDTO getSamaccountName(CuentasADDTO cuentasADDTO) {
		logger.info(String.format("getSamaccountName: loginName0=%s seq=%d", cuentasADDTO.getLoginName0(), cuentasADDTO.getSeq()));
		String smaccountName = cuentasADDTO.getLoginName0();
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> datos = (List<Map<String, Object>>) getLoginNameADNombresCuenta.
				requestBodyAndHeader(null, "cuentaporciento", smaccountName+"%");
		logger.info(String.format("datos es %s NULO, size = %d", datos != null?"NO":"", datos != null?datos.size() : -1));
		if (datos != null && datos.size() > 0) {
			if (datos.size() >= 1) {
				// separar nombres de secuencias
				// encontrar la seq mayor y actualizarla
				for (Map<String, Object> map : datos) {
					String loginName = (String) map.get("SAMACCOUNT_NAME");
					if (loginName.length() != smaccountName.length()) {
						// solo se saca la seq del login name cuando no es el mismo
						logger.info(String.format("loginName=%s smaccountName=%s", loginName, smaccountName));
						if (loginName.length() > smaccountName.length()) {
							logger.error(String.format("lo traido desde tabla AD_NOMBRES_CUENTA no corresponde al qry. solicitado: %s%c, comparado con: %s - cuentasADDTO [%s]",
									smaccountName, '%', loginName, cuentasADDTO));
							return cuentasADDTO;
						}
						String valor = loginName.substring(smaccountName.length());
						Integer seq;
						try {
							seq = StringUtilities.getInstance().toInteger(valor);
							if (seq != null && seq > cuentasADDTO.getSeq())
								cuentasADDTO.setSeq(seq);
							else if (seq == null) {
								// se esta tratando de convertir kk
								logger.error(String.format("se esta considerando como SEQ este valor=%s - cuentasADDTO=[%s]", valor, cuentasADDTO));
								return cuentasADDTO;
							}
						} catch (Exception e) {
							;
						}
					}
				}
				cuentasADDTO.incSeq();
			}
			// genera nuevo nombre incluyendo la seq
			smaccountName = cuentasADDTO.setLastLoginName();
			
		} else
			// no se recuperaron datos del SQL
			cuentasADDTO.setLoginName(smaccountName);
			
		return cuentasADDTO;
	}
	
	public void add2AdNombresCuenta(CuentasADDTO cuentasADDTO) {
		// ingresa este nombre a la tabla AD_NOMBRES_CUENTA
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("smaccountName", cuentasADDTO.getSamaccountName());
		map.put("rut", cuentasADDTO.getRut());
		logger.info(String.format("getSamaccountName: smaccountName=%s rut=%s", 
				cuentasADDTO.getSamaccountName(),cuentasADDTO.getRut()));
		insertNombresCuenta.requestBodyAndHeaders(null, map);
	}
	
	private Boolean estaOcupadoEngmail (CuentasADDTO cuentasADDTO) {
		logger.info(String.format("estaOcupadoEngmail: loginName=%s seq=%d", cuentasADDTO.getLoginName(), cuentasADDTO.getSeq()));
		if (hayNickName(cuentasADDTO)) {
			return Boolean.TRUE;
		}
		if (hayCuentaGmail(cuentasADDTO)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	@EndpointInject(uri = "cxfrs:bean:consultaNickNameGMail") // recupera de Gmail nickName
	ProducerTemplate apiHayNickName;
	private final String templateUriHayNickName = "http://localhost:8181/cxf/ESB/panel/gmailServices/nickName/retrieve/%s";
	public boolean hayNickName(CuentasADDTO cuentasADDTO) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateUriHayNickName, cuentasADDTO.getLoginName()));
		headers.put("CamelHttpMethod", "GET");
		try {
			AliasResponse obj = (AliasResponse)procesaResponseImpl((ResponseImpl)apiHayNickName.requestBodyAndHeaders(null, headers), AliasResponse.class);
			return obj.getHayAlias();
		} catch (Exception e) {
			logger.error("hayNickName", e);
		}
		return false;
	}

	@EndpointInject(uri = "cxfrs:bean:consultaGMail") // recupera de Gmail cuenta
	ProducerTemplate apiHayCuenta;
	private final String templateUriHayCuenta = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/retrieve/%s";
	public boolean hayCuentaGmail(CuentasADDTO cuentasADDTO) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateUriHayCuenta, cuentasADDTO.getLoginName()));
		headers.put("CamelHttpMethod", "GET");
		try {
			UserResponse obj = (UserResponse)procesaResponseImpl((ResponseImpl)apiHayCuenta.requestBodyAndHeaders(null, headers), UserResponse.class);
			return obj.getCodigo() < 0 ? Boolean.FALSE : Boolean.TRUE;
		} catch (Exception e) {
			logger.error("hayCuenta", e);
		}
		return false;
	}

	public Object procesaResponseImpl(ResponseImpl responseImpl, Class<?> clss) throws Exception {
		StringBuffer sb = new StringBuffer();
		java.io.SequenceInputStream in = (SequenceInputStream) responseImpl.getEntity();
		int incc;
		while ((incc=in.read()) != -1)
			sb.append((char)incc);
		String jsonString =  sb.toString();
		
		int status = responseImpl.getStatus();
		logger.info(String.format("procesaResponseImpl: Http status: %d errorsExist [%b]", status, jsonString.contains("errorsExist")));
		if (status >= 300) {
			return null;
		}

		return JSonUtilities.getInstance().json2java(jsonString, clss, false);
	}

	@EndpointInject(uri = "cxfrs:bean:rsADvalidarUsuario") // recupera de Gmail cuenta
	ProducerTemplate validarUsuarioAD;
	private final String uriADvalidarUsuario = "http://localhost:8181/cxf/ESB/panel/serviciosAD/validarUsuario";
	private Boolean estaOcupadoEnAD (CuentasADDTO cuentasADDTO) throws Exception {
		logger.info(String.format("estaOcupadoEnAD: loginName=%s seq=%d", cuentasADDTO.getLoginName(), cuentasADDTO.getSeq()));
		
		if (!Boolean.valueOf(getDebug())) {
			// Si no se definio debug o esta en false
			final Map<String,Object> headers = new HashMap<String,Object>();
			
			// consultarlo a AD
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, uriADvalidarUsuario);
			headers.put("CamelHttpMethod", "POST");
			
			ServiciosLDAPRequest request = new ServiciosLDAPRequest("ValidarUsuario", null, Usuario.createUsuario4validar(cuentasADDTO.getLoginName()));
			ServiciosLDAPResponse response;
			try {
				response = (ServiciosLDAPResponse) ObjectFactory.procesaResponseImpl(
						(ResponseImpl) validarUsuarioAD.requestBodyAndHeaders(request, headers),
						ServiciosLDAPResponse.class);
			} catch (Exception e) {
				logger.error("estaOcupadoEnAD", e);
				//registraLogError(getClass(),"crearCuentaAD", String.format("createCuenta: request: %s", request), e, keyResultado);
				throw new RuntimeException(e.getMessage());
				
			}
			if (response.getCodigo() == 0)
				return response.getMensaje().equals("NOK") ? Boolean.FALSE : Boolean.TRUE;
				
			throw new RuntimeException(response.getMensaje());
		} else
			return Boolean.FALSE;
	}

	//============================================================================================================
	// Getters y Setters
	//============================================================================================================

	public String getLogAplicacion() {
		return logAplicacion;
	}

	public void setLogAplicacion(String logAplicacion) {
		this.logAplicacion = logAplicacion;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}
}
