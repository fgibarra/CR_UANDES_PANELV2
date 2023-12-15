package cl.uandes.panel.comunes.bean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.Contadores;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.comunes.utils.StringUtilities;

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

	String proceso = null;
	
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
			List<?> resRoute = (List<?>)getKey.requestBody(null);
			//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", resRoute, resRoute!=null?resRoute.getClass().getName():"es NULO"));
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)resRoute.get(0);
			//logger.info(String.format("InicialiceCrearGrupos: map: %s clase: %s", map, map!=null?map.getClass().getName():"es NULO"));
			BigDecimal valor = (BigDecimal)map.get("NEXTVAL");
			//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", valor, valor!=null?valor.getClass().getName():"es NULO"));
			
			// inicializa el resultado
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
	

	private void registraError(String proceso, String apoyo, Exception e) {
		StackTraceElement[] elements = e.getStackTrace();
		String clase = elements[2].getClassName();;
		String metodo = elements[2].getMethodName();
		for (int i = 2; i < elements.length; i++) {
			String valor = elements[i].getClassName();
			if (valor.startsWith("cl.uandes")) {
				clase = elements[i].getClassName();
				metodo = elements[i].getMethodName();
				break;
			}
		}
		
		logger.info(String.format("registraError: proceso=%s apoyo=%s clase=%s metodo=%s",
				proceso, apoyo, clase, metodo));
		registraLogError(clase, metodo, apoyo, e, null);
	}

	public Integer registraMiResultado(String funcion, Integer minThread, Integer maxThread) {
		Integer key = null;
		
		return key;
	}
	/**
	 * @param idUsuario
	 * @param tipo
	 * @param causa
	 * @param keyGrupo
	 * @param keyResultado
	 * @return
	 */
	public Integer registraMiResultadoErrores(String idUsuario, String tipo, String causa, Integer keyGrupo, Integer keyResultado) {
		// TODO
		Integer key = null;
		
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
		Map<String, Object> headers = new HashMap<String, Object>();
		String excepcion = e.getClass().getCanonicalName();
		String msg = e.getMessage();
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
			
			logger.info(String.format("registraLogError headers: %s", StringUtilities.getInstance().dumpMap(headers)));
		
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

	//============================================================================================================
	// Getters y Setters
	//============================================================================================================

	public String getLogAplicacion() {
		return logAplicacion;
	}

	public void setLogAplicacion(String logAplicacion) {
		this.logAplicacion = logAplicacion;
	}
}
