package cl.uandes.panel.servicio.owners.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;

public class FinalizaProceso implements Processor {

    @PropertyInject(value = "scheduler.uri-serviciosBatch", defaultValue="http://localhost:8181/cxf/ESB/panel/servicioBatch")
	private String panelServices;
	private RegistrosComunes registrosBD;

	@EndpointInject(uri = "cxfrs:bean:rsFinProceso?timeout=-1")
	ProducerTemplate finProceso;
	String templateFinProceso = "%s/schedulerPanel/finProceso";

	ResultadoFuncion res = null;
	
	private Logger logger = Logger.getLogger(getClass());
	private final String keyContador = "contadoresAsignarOwners";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			DatosKcoFunciones datos = (DatosKcoFunciones)exchange.getIn().getHeader("DatosKcoFunciones");
			String operacion = datos.getFuncion();
			logger.info(String.format("ActualizaMiResultados: DatosKcoFunciones datos=%s", datos));
			res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
			
			registrosBD.cierraMiResultados(exchange, keyContador);
			
			// Colocar en el body el SchedulerPanelRequest
			Map<String, Object> headers = new HashMap<String, Object>();
			SchedulerPanelRequest request = new SchedulerPanelRequest(operacion, res.getKey());
			headers.clear();
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateFinProceso,getPanelServices()));
			headers.put("CamelHttpMethod", "POST");
			
			logger.info(String.format("deje en el body:|%s|", request));
			finProceso.requestBodyAndHeaders(request, headers);
		} catch (Exception e) {
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", null, e, null, res.getKey());
		}

	}

	public synchronized String getPanelServices() {
		return panelServices;
	}

	public synchronized void setPanelServices(String panelServices) {
		this.panelServices = panelServices;
	}

	public synchronized RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public synchronized void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

}
