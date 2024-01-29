package cl.uandes.panel.servicio.crearGrupos.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarGrupos;
import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;
import cl.uandes.panel.comunes.utils.ObjectFactory;

public class ActualizaMiResultados implements Processor {

    @PropertyInject(value = "scheduler.uri-serviciosBatch", defaultValue="http://localhost:8181/cxf/ESB/panel/servicioBatch")
	private String panelServices;
	
	private RegistrosComunes registrosBD;
		
	@EndpointInject(uri = "sql:classpath:sql/updateMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateMiResultado;
	@EndpointInject(uri = "cxfrs:bean:rsFinProceso?timeout=-1")
	ProducerTemplate finProceso;
	String templateFinProceso = "%s/schedulerPanel/finProceso";

	ResultadoFuncion res = null;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			// recuperar desde el header data relevante
			Message message = exchange.getIn();
			DatosKcoFunciones datos = (DatosKcoFunciones)exchange.getIn().getHeader("DatosKcoFunciones");
			String operacion = datos.getFuncion();
			logger.info(String.format("ActualizaMiResultados: DatosKcoFunciones datos=%s", datos));
			res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
			String resultado = null;
			Map<String, Object> headers = new HashMap<String, Object>();
			if (operacionEsCreacion(operacion)) {
				ContadoresCrearGrupos contadoresCrearGrupos = (ContadoresCrearGrupos)message.getHeader("contadoresCrearGrupos");
				logger.info(String.format("Contadores: %s", message.getHeader("contadoresCrearGrupos")));
				
				// actualizar MI_RESULTADOS
				resultado = String.format("OK:%s", contadoresCrearGrupos.toString());
				headers.put("countGruposAgregadosBD",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountGruposAgregadosBD()));
				headers.put("countMiembrosAgregadosBD",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountMiembrosAgregadosBD()));
				headers.put("countProcesados",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountProcesados()));
			} else {
				ContadoresSincronizarGrupos contadores = (ContadoresSincronizarGrupos)message.getHeader("contadoresSincronizarGrupos");
				resultado = String.format("OK:%s", contadores.toString());
				headers.put("countGruposAgregadosBD",  ObjectFactory.toBigDecimal(contadores.getCountProcesados()));
				headers.put("countMiembrosAgregadosBD",  ObjectFactory.toBigDecimal(contadores.getCountSacados()));
				headers.put("countProcesados",  ObjectFactory.toBigDecimal(contadores.getCountProcesados()));
			}
			headers.put("keyResultado", ObjectFactory.toBigDecimal(res.getKey()));
			headers.put("resultado", resultado);
			
			updateMiResultado.requestBodyAndHeaders(null, headers);
			
			// Colocar en el body el SchedulerPanelRequest
			SchedulerPanelRequest request = new SchedulerPanelRequest(operacion, res.getKey());
			headers.clear();
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateFinProceso,getPanelServices()));
			headers.put("CamelHttpMethod", "POST");
			
			logger.info(String.format("ActualizaMiResultado: deje en el body:[%s] URL: %s", request, String.format(templateFinProceso,getPanelServices())));
			finProceso.requestBodyAndHeaders(request, headers);
		} catch (Exception e) {
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", null, e, null, res.getKey());
		}
	}

	private boolean operacionEsCreacion(String operacion) {
		return !operacion.startsWith("sinc_grupos");
	}

	public String getPanelServices() {
		return panelServices;
	}

	public void setPanelServices(String panelServices) {
		this.panelServices = panelServices;
	}

	public RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

}
