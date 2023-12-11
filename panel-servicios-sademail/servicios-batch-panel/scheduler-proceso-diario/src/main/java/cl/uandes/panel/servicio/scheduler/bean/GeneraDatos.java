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
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentas;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarCuentas;
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
	private String tiposSincronizarGrupos[] = {"sinc_grupos_generales", "sinc_grupos_generales", "sinc_grupos_postgrado"};
	private String tiposSincronizarSuspenderEliminar[] = {"sincronizar", "suspender", "eliminar"};
	
	private String funcionNocturnoCrearCuentas;
	private String funcionNocturnoCrearGrupos;
	private String funcionSincronizaGrupos;
	private String funcionSincronizaCuentas;
	@PropertyInject(value = "crear-cuentas-gmail.nocturno.leyenda", defaultValue="actualizacion de cuentas nocturno")
	private String crearCuentasLeyenda;
	@PropertyInject(value = "crear-grupos-gmail.nocturno.leyenda", defaultValue="actualizacion de grupos nocturno")
	private String crearGruposLeyenda;
	@PropertyInject(value = "sinc_grupos-gmail.nocturno.leyenda", defaultValue="sincronizacion de grupos nocturno")
	private String sincGruposLeyenda;
	
	@PropertyInject(value = "scheduler.tiposCuenta.nocturno.operaciones", defaultValue="sincronizar,eliminar,suspender")
	private String schedulerTiposCuenta;
	@PropertyInject(value = "scheduler.tiposGrupos.nocturno.operaciones", defaultValue="sincronizar,eliminar,suspender")
	private String schedulerTiposGrupos;
	@PropertyInject(value = "scheduler.tiposSincronizarGrupos.nocturno.operaciones", defaultValue="sincronizar,eliminar,suspender")
	private String schedulerTiposSincronizarGrupos;
	@PropertyInject(value = "scheduler.sincronizarSuspenderEliminar.nocturno.operaciones", defaultValue="sincronizar,eliminar,suspender")
	private String sincronizarSuspenderEliminar;
	
	@EndpointInject(uri = "sql:classpath:sql/qryKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryKcoFunciones;
	@EndpointInject(uri = "sql:classpath:sql/qryMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryMiResultadoErrores;
	@EndpointInject(uri = "sql:classpath:sql/qryMiResultados.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryMiResultados;
	private Logger logger = Logger.getLogger(getClass());
	
	public GeneraDatos(String funcion1, String funcion2, String funcion3, String funcion4) {
		super();
		this.funcionNocturnoCrearCuentas = funcion1;
		this.funcionNocturnoCrearGrupos = funcion2;
		this.funcionSincronizaGrupos = funcion3;
		this.funcionSincronizaCuentas = funcion4;		
	}

	public void generaRequestCrearCuentas(Exchange exchange) throws Exception {
		tiposCuenta = getSchedulerTiposCuenta().split(",");
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionNocturnoCrearCuentas(), tiposCuenta);
		logger.info(String.format("generaRequestCrearCuentas: req:%s", req));
		exchange.getIn().setBody(req);
	}

	public void generaRequestCrearGupos(Exchange exchange) throws Exception {
		tiposGrupos = getSchedulerTiposGrupos().split(",");
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionNocturnoCrearGrupos(), tiposGrupos);
		logger.info(String.format("generaRequestCrearGupos: req:%s", req));
		exchange.getIn().setBody(req);
	}

	public void generaRequestSincronizaGrupos(Exchange exchange) throws Exception {
		tiposSincronizarGrupos = getSchedulerTiposSincronizarGrupos().split(",");
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionSincronizaGrupos(), tiposSincronizarGrupos);
		logger.info(String.format("generaRequestSincronizaGrupos: req:%s", req));
		exchange.getIn().setBody(req);
	}

	public void generaRequestSincronizaCuentas(Exchange exchange) throws Exception {
		tiposSincronizarSuspenderEliminar = getSincronizarSuspenderEliminar().split(",");
		ProcesoDiarioRequest req = new ProcesoDiarioRequest(getFuncionSincronizaCuentas(), tiposSincronizarSuspenderEliminar);
		logger.info(String.format("generaRequestSincronizaCuentas: req:%s", req));
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

	/**
	 * Recupera datos de KCO_FUNCIONES, MI_RESULTADO y MI_RESULTADO_ERRORES
	 * Deja datos en el header para que se pueda llenar el template del mail a enviar
	 * En header.resOperacion viene la funcion para recuperar datos desde KCO_FUNCIONES e instanciar Contadores
	 * @param exchange
	 */
	public void preparaMailResultadoSincronizarGrupos(Exchange exchange) {
		exchange.getIn().setHeaders(generaHeaders(exchange));
	}
	
	/**
	 * @param exchange
	 */
	public void preparaMailResultadoSincronizarSuspenderEliminar(Exchange exchange) {
		exchange.getIn().setHeaders(generaHeaders(exchange));
	}
	
	
	private Map<String,Object> generaHeaders(Exchange exchange) {
		Map<String,Object> headers = new HashMap<String,Object>();
		Message message = exchange.getIn();
		SchedulerPanelRequest request = (SchedulerPanelRequest) message.getHeader("request");
		String proceso = (String)message.getBody();
		Integer keyResultado = request.getKeyResultado();
		String operacion = (String)message.getHeader("operacion");
		
		logger.info(String.format("generaHeaders: proceso/funcion=%s keyResultado=%d operacion=%s", 
				proceso, keyResultado, operacion));
		
		headers.put("operacion", operacion);
		headers.put("proceso", getTexto(proceso));
		headers.put("fechaProceso", StringUtilities.getInstance().toString(new java.util.Date(), "E, dd MMM yyyy HH:mm:ss z"));

		headers.put("funcion", proceso);
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
	private String getUltimaEjecucion(String funcion) {
		logger.info(String.format("getUltimaEjecucion: busca funcion |%s| en KCO_FUNCIONES", funcion));
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryKcoFunciones.requestBodyAndHeader(null, "funcion", funcion);
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
		} else if (delegate.esSincronizarSuspenderEliminar(operacion)) {
			try {
				ContadoresSincronizarCuentas contadores = (ContadoresSincronizarCuentas)JSonUtilities.getInstance().
						json2java(jsonContadores, ContadoresSincronizarCuentas.class, false);
				logger.info(String.format("ContadoresSincronizarCuentas: %s", contadores));
				headers.put("countProcesados", contadores.getCountProcesados());
				headers.put("countErrores", contadores.getCountErrores());
				headers.put("countRegistradas", contadores.getCountRegistrados());
				headers.put("countNoRegistradas", contadores.getCountNoRegistrados());
				headers.put("countSuspendidas", contadores.getCountSuspendidas());
				headers.put("countEliminadas", contadores.getCountEliminadas());
			} catch (Exception e) {
				logger.error(String.format("ContadoresSincronizarSuspenderEliminar: json malo |%s|", jsonContadores), e);
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

	private Object getTexto(String operacion) {
		if (delegate.esSincronizarGrupos(operacion))
			return String.format("%s (%s)",getSincGruposLeyenda(), operacion);
		if (delegate.esCrearCuentas(operacion))
			return getCrearCuentasLeyenda();
		if (delegate.esCrearGrupos(operacion))
			return String.format("%s (%s)",getCrearGruposLeyenda(), operacion);
		return String.format("operacion %s", operacion);
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

	public synchronized String getCrearCuentasLeyenda() {
		return crearCuentasLeyenda;
	}

	public synchronized void setCrearCuentasLeyenda(String crearCuentasLeyenda) {
		this.crearCuentasLeyenda = crearCuentasLeyenda;
	}

	public synchronized String getCrearGruposLeyenda() {
		return crearGruposLeyenda;
	}

	public synchronized void setCrearGruposLeyenda(String crearGruposLeyenda) {
		this.crearGruposLeyenda = crearGruposLeyenda;
	}

	public synchronized String getSincGruposLeyenda() {
		return sincGruposLeyenda;
	}

	public synchronized void setSincGruposLeyenda(String sincGruposLeyenda) {
		this.sincGruposLeyenda = sincGruposLeyenda;
	}

	public synchronized String getFuncionSincronizaGrupos() {
		return funcionSincronizaGrupos;
	}

	public synchronized String getFuncionSincronizaCuentas() {
		return funcionSincronizaCuentas;
	}

	public synchronized String getSchedulerTiposCuenta() {
		return schedulerTiposCuenta;
	}

	public synchronized void setSchedulerTiposCuenta(String schedulerTiposCuenta) {
		this.schedulerTiposCuenta = schedulerTiposCuenta;
	}

	public synchronized String getSchedulerTiposGrupos() {
		return schedulerTiposGrupos;
	}

	public synchronized void setSchedulerTiposGrupos(String schedulerTiposGrupos) {
		this.schedulerTiposGrupos = schedulerTiposGrupos;
	}

	public synchronized String getSchedulerTiposSincronizarGrupos() {
		return schedulerTiposSincronizarGrupos;
	}

	public synchronized void setSchedulerTiposSincronizarGrupos(String schedulerTiposSincronizarGrupos) {
		this.schedulerTiposSincronizarGrupos = schedulerTiposSincronizarGrupos;
	}

	public synchronized String getSincronizarSuspenderEliminar() {
		return sincronizarSuspenderEliminar;
	}

	public synchronized void setSincronizarSuspenderEliminar(String sincronizarSuspenderEliminar) {
		this.sincronizarSuspenderEliminar = sincronizarSuspenderEliminar;
	}

}
