package cl.uandes.panel.servicio.scheduler.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentas;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarGrupos;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;
import cl.uandes.panel.comunes.utils.JSonUtilities;
import cl.uandes.panel.comunes.utils.StringUtilities;
import cl.uandes.panel.servicio.scheduler.api.resource.SchedulerPanelRestService;
import cl.uandes.panel.servicio.scheduler.bean.dto.MiResultadoErroresDTO;
import cl.uandes.panel.servicio.scheduler.bean.dto.MiResultadosDTO;

/**
 * Multiples metodos usados en las rutas Camel del proceso
 * @author fernando
 *
 */
public class GeneraDatos {
	private SchedulerPanelRestService delegate;
	private String tiposCuenta[] = {"Alumnos"};
	private String tiposGrupos[] = {"crear_grupos", "grupos_inprogress", "grupos_inprogress_postgrado"};
	private String funcionNocturnoCrearCuentas;
	private String funcionNocturnoCrearGrupos;
	
	
	@EndpointInject(uri = "sql:classpath:sql/qryKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryKcoFunciones;
	@EndpointInject(uri = "sql:classpath:sql/qryMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryMiResultadoErrores;
	@EndpointInject(uri = "sql:classpath:sql/qryMiResultados.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryMiResultados;
	private Logger logger = Logger.getLogger(getClass());
	
	public GeneraDatos(String tiposCuenta, String crearGrupo0, String crarGrupo1, String crearGrupo3, String funcion1, String funcion2) {
		super();
		this.tiposCuenta = new String[] {tiposCuenta };
		this.tiposGrupos = new String[] {crearGrupo0, crarGrupo1, crearGrupo3 };
		this.funcionNocturnoCrearCuentas = funcion1;
		this.funcionNocturnoCrearGrupos = funcion2;
		logger.info(String.format("Constructor GeneraDatos: tipoCuenta=%s delegate es %s NULO"+
				" tiposGrupos=[%s, %s, %s]", 
				tiposCuenta, delegate!=null?"NO":"",
						crearGrupo0,crarGrupo1,crearGrupo3));
	}

	public void generaRequestCrearCuentas(Exchange exchange) throws Exception {
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionNocturnoCrearCuentas(), tiposCuenta);
		logger.info(String.format("generaRequestCrearCuentas: req:%s", req));
		exchange.getIn().setBody(req);
	}

	public void generaRequestCrearGupos(Exchange exchange) throws Exception {
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionNocturnoCrearGrupos(), tiposGrupos);
		logger.info(String.format("generaRequestCrearGupos: req:%s", req));
		exchange.getIn().setBody(req);
	}

	/**
	 * Recupera datos de KCO_FUNCIONES, MI_RESULTADO y MI_RESULTADO_ERRORES
	 * Deja datos en el header para que se pueda llenar el template del mail a enviar
	 * @param exchange
	 */
	public void preparaMailResultadoCrearCuentas(Exchange exchange) {
		exchange.getIn().setHeaders(generaHeaders(exchange));
	}
	/**
	 * Recupera datos de KCO_FUNCIONES, MI_RESULTADO y MI_RESULTADO_ERRORES
	 * Deja datos en el header para que se pueda llenar el template del mail a enviar
	 * @param exchange
	 */
	public void preparaMailResultadoCrearGrupos(Exchange exchange) {
		exchange.getIn().setHeaders(generaHeaders(exchange));
	}

	public void preparaMailResultadoSincronizarGrupos(Exchange exchange) {
		 // en el request viene sinc_grupos_*, convertirlo a sincronizar_grupos
		Message message = exchange.getIn();
		message.setBody("sincronizaGrupos");
		message.setHeaders(generaHeaders(exchange));
	}
	
	private Map<String,Object> generaHeaders(Exchange exchange) {
		Map<String,Object> headers = new HashMap<String,Object>();
		Message message = exchange.getIn();
		SchedulerPanelRequest request = (SchedulerPanelRequest) message.getHeader("request");
		String proceso = (String)message.getBody();
		Integer keyResultado = request.getKeyResultado();
		
		headers.put("fechaProceso", StringUtilities.getInstance().toString(new java.util.Date(), "E, dd MMM yyyy HH:mm:ss z"));
		headers.put("proceso", proceso);

		String ultimaEjecucion = getUltimaEjecucion(proceso);
		headers.put("ultimaEjecucion", ultimaEjecucion);
		MiResultadosDTO miResultadosDTO = getMiResultadosDTO(keyResultado);
		headers.put("horaComienzo", miResultadosDTO.getHoraComienzo());
		headers.put("horaTermino", miResultadosDTO.getHoraTermino());
		setContadores(headers, proceso, miResultadosDTO.getJsonContadores());
		List<MiResultadoErroresDTO> listaErrores = getListaErrores(keyResultado);
		headers.put("reporteErrores",  listaErrores);
		return headers;
	}
	
	@SuppressWarnings("unchecked")
	private String getUltimaEjecucion(String proceso) {
		logger.info(String.format("getUltimaEjecucion: busca funcion |%s| en KCO_FUNCIONES", proceso));
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
		if (datos != null && datos.size() > 0) {
			Map<String, Object> map = (Map<String, Object>)datos.get(0);
			
			return StringUtilities.getInstance().toString((java.sql.Timestamp)map.get("ULTIMA_EJECUCION"), "E, dd MMM yyyy HH:mm:ss z");
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", new java.util.Locale("es", "CL"));
			
			return sdf.format(new java.util.Date());
		}
	}

	@SuppressWarnings("unchecked")
	private MiResultadosDTO getMiResultadosDTO(Integer keyResultado) {
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryMiResultados.requestBodyAndHeader(null, "keyResultado", 
				StringUtilities.getInstance().toBigDecimal(keyResultado));
		
		return new MiResultadosDTO((Map<String, Object>)datos.get(0));
	}

	private void setContadores(Map<String, Object> headers, String operacion, String jsonContadores) {
		if (jsonContadores == null || jsonContadores.length() == 0)
			return;
		logger.info(String.format("setContadores: jsonContadores=%s", jsonContadores));
		if (delegate.esCrearCuentas(operacion)) {
			ContadoresCrearCuentas contadores;
			try {
				contadores = (ContadoresCrearCuentas)JSonUtilities.getInstance().
						json2java(jsonContadores, ContadoresCrearCuentas.class, false);
				logger.info(String.format("ContadoresCrearCuentas: %s", contadores));
				// colocar los headers para el template de crear cuentas
				headers.put("countProcesados", contadores.getCountProcesados());
				headers.put("countErrores", contadores.getCountErrores());
				headers.put("countAgregadosBD", contadores.getCountAgregadosBD());
				headers.put("countAgregadosAD", contadores.getCountAgregadosAD());
			} catch (Exception e) {
				logger.error(String.format("ContadoresCrearCuentas: json malo |%s|", jsonContadores), e);
			}
		} else if (delegate.esCrearGrupos(operacion)) {
			// colocar los headers para el template de crear grupos
			try {
				ContadoresCrearGrupos contadores = (ContadoresCrearGrupos)JSonUtilities.getInstance().
						json2java(jsonContadores, ContadoresCrearGrupos.class, false);
				logger.info(String.format("ContadoresCrearGrupos: %s", contadores));
				headers.put("countProcesados", contadores.getCountProcesados());
				headers.put("countErrores", contadores.getCountErrores());
				headers.put("countGruposAgregadosBD", contadores.getCountGruposAgregadosBD());
				headers.put("countGruposAgregadosAD", contadores.getCountGruposAgregadosAD());
				headers.put("countGruposSacadosBD", contadores.getCountGruposSacadosBD());
				headers.put("countGruposSacadosAD", contadores.getCountGruposSacadosAD());
				headers.put("countMiembrosAgregadosBD", contadores.getCountMiembrosAgregadosBD());
				headers.put("countMiembrosAgregadosAD", contadores.getCountMiembrosAgregadosAD());
				headers.put("countMiembrosSacadosBD", contadores.getCountMiembrosSacadosBD());
				headers.put("countMiembrosSacadosAD", contadores.getCountMiembrosSacadosAD());
			} catch (Exception e) {
				logger.error(String.format("ContadoresCrearGrupos: json malo |%s|", jsonContadores), e);
			}
		} else if (delegate.esSincronizarGrupos(operacion)) {
			try {
				ContadoresSincronizarGrupos contadores = (ContadoresSincronizarGrupos)JSonUtilities.getInstance().
						json2java(jsonContadores, ContadoresSincronizarGrupos.class, false);
				headers.put("countProcesados", contadores.getCountProcesados());
				headers.put("countErrores", contadores.getCountErrores());
				headers.put("countMiembrosSacadosBD", contadores.getCountSacados());
			} catch (Exception e) {
				logger.error(String.format("ContadoresSincronizarGrupos: json malo |%s|", jsonContadores), e);
			}
		}
	}

	private List<MiResultadoErroresDTO> getListaErrores(Integer keyResultado) {
		List<MiResultadoErroresDTO> lista = new ArrayList<MiResultadoErroresDTO>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryMiResultadoErrores.requestBodyAndHeader(null, "keyResultado", 
				StringUtilities.getInstance().toBigDecimal(keyResultado));
		if (datos != null) {
			for (Map<String, Object> map : datos)
				lista.add(new MiResultadoErroresDTO(map));
		}
		return lista;
	}

	public SchedulerPanelRestService getDelegate() {
		return delegate;
	}

	public void setDelegate(SchedulerPanelRestService delegate) {
		this.delegate = delegate;
	}

	public String getFuncionNocturnoCrearCuentas() {
		return funcionNocturnoCrearCuentas;
	}

	public String getFuncionNocturnoCrearGrupos() {
		return funcionNocturnoCrearGrupos;
	}
}
