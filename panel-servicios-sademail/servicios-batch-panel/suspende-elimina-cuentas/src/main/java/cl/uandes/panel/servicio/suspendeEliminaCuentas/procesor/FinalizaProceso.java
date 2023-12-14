package cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarCuentas;
import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;
import cl.uandes.panel.comunes.utils.ObjectFactory;

public class FinalizaProceso implements Processor {

    @PropertyInject(value = "scheduler.uri-serviciosBatch", defaultValue="http://localhost:8181/cxf/ESB/panel/servicioBatch")
	private String panelServices;

    @EndpointInject(uri = "sql:classpath:sql/actualizarMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarMiResultado;

	@EndpointInject(uri = "cxfrs:bean:rsFinProceso?timeout=-1")
	ProducerTemplate finProceso;
	String templateFinProceso = "%s/schedulerPanel/finProceso";

	ContadoresSincronizarCuentas contadores = null;
	Integer keyResultado;
	String proceso;
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		contadores = (ContadoresSincronizarCuentas) message.getHeader("contadoresSincronizarCuentas");
		keyResultado = (Integer)message.getHeader("keyResultado");
		proceso = (String)message.getHeader("proceso");
		
		logger.info(String.format("FinalizaProceso: proceso=%s keyResultado=%d contadores=%s",
				proceso, keyResultado, contadores));
		resultadosABd(message);
		procesoDiarioInformaTermino(message);
	}

	private void resultadosABd(Message message) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("countProcesados", contadores.getCountProcesados());
		headers.put("countErrores", contadores.getCountErrores());
		headers.put("countRegistrados", contadores.getCountRegistrados());
		headers.put("countNoRegistrados", contadores.getCountNoRegistrados());
		headers.put("jsonResultado", contadores.toString());
		headers.put("keyResultado", ObjectFactory.toBigDecimal(keyResultado));
		
		actualizarMiResultado.requestBodyAndHeaders(null, headers);
	}

	private void procesoDiarioInformaTermino(Message message) {
		Map<String, Object> headers = new HashMap<String, Object>();
		SchedulerPanelRequest request = new SchedulerPanelRequest(proceso, keyResultado);
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateFinProceso,getPanelServices()));
		headers.put("CamelHttpMethod", "POST");
		
		logger.info(String.format("deje en el body:|%s|", request));
		finProceso.requestBodyAndHeaders(request, headers);
	}


	
	
	//============================================================================================================
	// Getters y Setters
	//============================================================================================================
	
	public synchronized String getPanelServices() {
		return panelServices;
	}

	public synchronized void setPanelServices(String panelServices) {
		this.panelServices = panelServices;
	}

}
