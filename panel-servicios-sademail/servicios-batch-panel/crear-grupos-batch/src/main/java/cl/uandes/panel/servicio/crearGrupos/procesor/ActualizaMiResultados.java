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
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.json.batch.SchedulerPanelRequest;
import cl.uandes.panel.comunes.utils.ObjectFactory;

public class ActualizaMiResultados implements Processor {

    @PropertyInject(value = "crear-grupos-azure.uri-serviciosPanel", defaultValue="http://localhost:8181/cxf/ESB/panel/servicio/")
	private String panelServices;
	
	@EndpointInject(uri = "sql:classpath:sql/updateMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateMiResultado;
	@EndpointInject(uri = "cxfrs:bean:rsFinProceso?timeout=-1")
	ProducerTemplate finProceso;
	String templateFinProceso = "%s/schedulerPanel/finProceso";

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// recuperar desde el header data relevante
		Message message = exchange.getIn();
		DatosKcoFunciones datos = (DatosKcoFunciones)exchange.getIn().getHeader("DatosKcoFunciones");
		String operacion = datos.getFuncion();
		ResultadoFuncion res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
		ContadoresCrearGrupos contadoresCrearGrupos = (ContadoresCrearGrupos)message.getHeader("contadoresCrearGrupos");
		logger.info(String.format("Contadores: %s", message.getHeader("contadoresCrearGrupos")));
		
		// actualizar MI_RESULTADOS
		String resultado = String.format("OK:%s", contadoresCrearGrupos.toString());
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("keyResultado", ObjectFactory.toBigDecimal(res.getKey()));
		headers.put("countGruposAgregadosBD",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountGruposAgregadosBD()));
		headers.put("countMiembrosAgregadosBD",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountMiembrosAgregadosBD()));
		headers.put("countProcesados",  ObjectFactory.toBigDecimal(contadoresCrearGrupos.getCountProcesados()));
		headers.put("resultado", resultado);
		
		updateMiResultado.requestBodyAndHeaders(null, headers);
		
		// Colocar en el body el SchedulerPanelRequest
		SchedulerPanelRequest request = new SchedulerPanelRequest(operacion, res.getKey());
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateFinProceso,getPanelServices()));
		headers.put("CamelHttpMethod", "POST");
		
		logger.info(String.format("deje en el body:|%s|", request));
		finProceso.requestBodyAndHeaders(request, headers);
	}

	public String getPanelServices() {
		return panelServices;
	}

	public void setPanelServices(String panelServices) {
		this.panelServices = panelServices;
	}

}
