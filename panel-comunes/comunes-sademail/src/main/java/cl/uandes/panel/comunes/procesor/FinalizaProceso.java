package cl.uandes.panel.comunes.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
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
	private String keyContador;

	@EndpointInject(uri = "cxfrs:bean:rsFinProceso?timeout=-1")
	private ProducerTemplate finProceso;
	private String templateFinProceso = "%s/schedulerPanel/finProceso";

	private ResultadoFuncion res = null;
	
	private DatosKcoFunciones datos;
	private String operacion;
	Object request;
	private final String operacionesInvocadasScheduler = "suspender_eliminar_cuentas|asignar_owners|crear_cuentas|crear_grupos|grupos_inprogress|grupos_inprogress_postgrado|crear_cuentas_AD|crear_cuentas_AD_postgrado|";
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		try {
			datos = (DatosKcoFunciones)message.getHeader("DatosKcoFunciones");
			request = message.getHeader("request");
			operacion = datos.getFuncion();
			logger.info(String.format("FinalizaProceso: keyContador=%s DatosKcoFunciones datos=%s", getKeyContador(), datos));
			res = (ResultadoFuncion)message.getHeader("ResultadoFuncion");
			
			registrosBD.cierraMiResultados(exchange, getKeyContador());
			
			if (esInvocadoPorScheduler(exchange)) {
				logger.info(String.format("FinalizaProceso: Va a invocar al fin del scheduler: operacion=%s", operacion));
				// Colocar en el body el SchedulerPanelRequest
				Map<String, Object> headers = new HashMap<String, Object>();
				SchedulerPanelRequest request = new SchedulerPanelRequest(operacion, res.getKey());
				headers.clear();
				headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateFinProceso,getPanelServices()));
				headers.put("CamelHttpMethod", "POST");
				
				logger.info(String.format("FinalizaProceso: deje en el body:[%s]", request));
				finProceso.requestBodyAndHeaders(request, headers);
			} else
				logger.info(String.format("FinalizaProceso: operacion %s no esta incluida en operacionesInvocadasScheduler: %s", operacion, operacionesInvocadasScheduler));
		} catch (Exception e) {
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", null, e, null, res.getKey());
		}
	}

	private boolean esInvocadoPorScheduler(Exchange exchange) {
		if ("crear_cuentas_AD".equals(operacion) && request == null)
			return false;
		if (operacionesInvocadasScheduler.matches(String.format("(.*)%s(.*)", operacion)))
			return true;
		
		return false;
	}

	//============================================================================================================
	// Getters y Setters
	//============================================================================================================

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

	public String getKeyContador() {
		return keyContador;
	}

	public void setKeyContador(String keyContador) {
		this.keyContador = keyContador;
	}

}
