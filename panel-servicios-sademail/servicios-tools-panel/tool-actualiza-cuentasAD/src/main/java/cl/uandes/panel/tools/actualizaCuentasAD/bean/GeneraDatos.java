package cl.uandes.panel.tools.actualizaCuentasAD.bean;

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

import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.StringUtilities;
import cl.uandes.panel.tools.actualizaCuentasAD.api.json.OperacionXFecha;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.AdCuentasCreadasDTO;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.BdCuentasSinActualizarDTO;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.ContadoresActualizaCuentas;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.CorregirPregradoDTO;

public class GeneraDatos {

	@PropertyInject(value = "actualizar-cuentas-AD.debug", defaultValue = "false")
	protected String debug;
	protected Boolean soloDebug = Boolean.valueOf(debug); //  true --> NO envia

	// procese masivo actualizar datos que faltaban
	@EndpointInject(uri = "sql:classpath:sql/qryAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryAdCuentasCreadas;
	@EndpointInject(uri = "sql:classpath:sql/qryAdCuentasCreadasDebug.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryAdCuentasCreadasDebug;
	@EndpointInject(uri = "sql:delete from wrk_planilla?dataSource=#bannerDataSource")
	ProducerTemplate deleteWrkPlanilla;
	@EndpointInject(uri = "sql:commit?dataSource=#bannerDataSource")
	ProducerTemplate commit;
	@EndpointInject(uri = "sql:classpath:sql/qryBdCuentasSinActualizar.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryBdCuentasSinActualizar;

	// Corrige cuentas pregrado
	@EndpointInject(uri = "sql:classpath:sql/corregir_pregrado.sql?dataSource=#bannerDataSource")
	ProducerTemplate corregirPregrado;
	
	
	
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Genera lista para crear cuentas AD segun el request recibido
	 * 
	 * @param exchange
	 */
	@SuppressWarnings("unchecked")		
	public void generaListaXrequest(Exchange exchange) {
		Message message = exchange.getIn();
		this.soloDebug = Boolean.valueOf(getDebug());
		List<AdCuentasCreadasDTO> lista = new ArrayList<AdCuentasCreadasDTO>();
		OperacionXFecha request = (OperacionXFecha)message.getHeader("request");
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("fechaDesde", request.getTimestampFechaDesde());
		headers.put("fechaHasta", request.getTimestampFechaHasta());
		logger.info(String.format("generaListaXrequest: soloDebug: %b qry: %s\nheaders: %s", 
				soloDebug, soloDebug ? qryAdCuentasCreadasDebug.getDefaultEndpoint().getEndpointUri() : 
					qryAdCuentasCreadas.getDefaultEndpoint().getEndpointUri(),
					StringUtilities.getInstance().dumpMap(headers)));
		
		List<Map<String, Object>> datos;		
		if (soloDebug)
			datos = (List<Map<String, Object>>) qryAdCuentasCreadasDebug.requestBodyAndHeaders(null, headers);
		else
			datos = (List<Map<String, Object>>) qryAdCuentasCreadas.requestBodyAndHeaders(null, headers);
		
		if (datos != null && datos.size() > 0) {
			Integer filas = 0;
			for (Map<String, Object> dato : datos) {
				lista.add(new AdCuentasCreadasDTO(dato));
				if (request.getMaxResultados() > 0) {
					if (++filas >= request.getMaxResultados())
						break;
				}
			}
		}
		logger.info(String.format("generaListaXrequest: elementos en la lista: %d", lista.size()));
		for (AdCuentasCreadasDTO dto : lista)
			logger.info(String.format("%s", dto));
		
		deleteWrkPlanilla.requestBody(null);
		commit.requestBody(null);
		
		message.setHeader("listaCuentas", lista);
		message.setHeader("countThread", new CountThreads());
		message.setHeader("contadores", new ContadoresActualizaCuentas());
	}
	
	/**
	 * Saca un elemento de la lista y lo coloca en el header para su proceso por el Process
	 * @param exchange
	 */
	public void getCuentaFromListaCuentas(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<AdCuentasCreadasDTO> lista = (List<AdCuentasCreadasDTO>)message.getHeader("listaCuentas");
		AdCuentasCreadasDTO dto = lista.remove(0);
		message.setHeader("AdCuentasCreadasDTO", dto);
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.incCounter();
	}
	
	/**
	 * 
	 * Genera la lista de los registros que tienen fecha_actualizacion en nulo
	 * @param exchange
	 */
	@SuppressWarnings("unchecked")
	public void generaListaXpareo(Exchange exchange) {
		Message message = exchange.getIn();
		this.soloDebug = Boolean.valueOf(getDebug());
		List<BdCuentasSinActualizarDTO> lista = new ArrayList<BdCuentasSinActualizarDTO>();
		OperacionXFecha request = (OperacionXFecha)message.getHeader("request");
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("fechaDesde", request.getTimestampFechaDesde());
		headers.put("fechaHasta", request.getTimestampFechaHasta());
		logger.info(String.format("generaListaXrequest: soloDebug: %b qry: %s\nheaders: %s", 
				soloDebug, qryBdCuentasSinActualizar.getDefaultEndpoint().getEndpointUri(),
					StringUtilities.getInstance().dumpMap(headers)));
		
		List<Map<String, Object>> datos;		
		datos = (List<Map<String, Object>>) qryBdCuentasSinActualizar.requestBodyAndHeaders(null, headers);
		
		if (datos != null && datos.size() > 0) {
			Integer filas = 0;
			for (Map<String, Object> dato : datos) {
				lista.add(new BdCuentasSinActualizarDTO(dato));
				if (request.getMaxResultados() > 0) {
					if (++filas >= request.getMaxResultados())
						break;
				}
			}
		}
		logger.info(String.format("generaListaXrequest: elementos en la lista: %d", lista.size()));
		for (BdCuentasSinActualizarDTO dto : lista)
			logger.info(String.format("%s", dto));
		
		message.setHeader("listaCuentas", lista);
		message.setHeader("countThread", new CountThreads());
		message.setHeader("contadores", new ContadoresActualizaCuentas());
	}
	
	/**
	 * Saca el primer elemento de la lista y lo deja en el header BdCuentasSinActualizarDTO
	 * @param exchange
	 */
	public void getCuentaFromListaSinActualizar(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<BdCuentasSinActualizarDTO> lista = (List<BdCuentasSinActualizarDTO>)message.getHeader("listaCuentas");
		BdCuentasSinActualizarDTO dto = lista.remove(0);
		message.setHeader("BdCuentasSinActualizarDTO", dto);
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.incCounter();
	}
	
	//===========================================================================================================
	/**
	 * Genera la lista de los alumnos de pregrado que hay que actualizar
	 * @param exchange
	 */
	@SuppressWarnings("unchecked")
	public void generaListaPregrado(Exchange exchange) {
		Message message = exchange.getIn();
		this.soloDebug = Boolean.valueOf(getDebug());
		
		List<Map<String, Object>> datos;		
		datos = (List<Map<String, Object>>) corregirPregrado.requestBody(null);
		
		List<CorregirPregradoDTO> lista = new ArrayList<CorregirPregradoDTO>();
		
		if (datos != null && datos.size() > 0) {
			for (Map<String, Object> dato : datos) {
				CorregirPregradoDTO dto = new CorregirPregradoDTO(dato);
				lista.add(dto);
				if (soloDebug)
					logger.info(String.format("CorregirPregradoDTO: %s", dto));
			}
		}
		logger.info(String.format("generaListaPregrado: elementos en la lista: %d", lista.size()));

		message.setHeader("listaCuentas", lista);
		message.setHeader("contadores", new ContadoresActualizaCuentas());
	}
	
	public void getCuentaCorregir(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<CorregirPregradoDTO> lista = (List<CorregirPregradoDTO>)message.getHeader("listaCuentas");
		CorregirPregradoDTO dto = lista.remove(0);
		message.setHeader("CorregirPregradoDTO", dto);
		if (soloDebug)
			logger.info(String.format("procesar CorregirPregradoDTO: %s", dto));
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.incCounter();
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

}
