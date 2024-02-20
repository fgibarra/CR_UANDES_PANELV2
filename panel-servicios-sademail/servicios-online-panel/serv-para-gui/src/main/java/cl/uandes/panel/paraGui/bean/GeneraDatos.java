package cl.uandes.panel.paraGui.bean;

import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.utils.StringUtilities;
import cl.uandes.panel.paraGui.json.LastResponse;

public class GeneraDatos {

	private Logger logger = Logger.getLogger(getClass());

	@EndpointInject(uri = "sql:classpath:sql/getKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKcoFunciones;

	public void ultimaEjecucion(Exchange exchange) {
		Message message = exchange.getIn();
		String proceso = (String) message.getBody();
		Integer codigo = 0;
		String mensaje = "OK";
		String ultimoUso = "No se registra";
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = (List<Map<String, Object>>) getKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
		if (lista != null && lista.size() > 0) {
			DatosKcoFunciones dto = new DatosKcoFunciones(lista.get(0));
			if (dto.getUltimaEjecucion() != null)
				ultimoUso = StringUtilities.getInstance().toString(dto.getUltimaEjecucion());
			else {
				mensaje = "NOK";
			}
		}
		LastResponse response = new LastResponse(codigo, mensaje, ultimoUso);
		logger.info(String.format("ultimaEjecucion: response = %s", response));
		message.setBody(response);
	}
}
