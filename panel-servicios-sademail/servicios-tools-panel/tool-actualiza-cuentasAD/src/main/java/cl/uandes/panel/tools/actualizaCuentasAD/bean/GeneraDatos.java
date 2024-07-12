package cl.uandes.panel.tools.actualizaCuentasAD.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.tools.actualizaCuentasAD.api.json.ActualizaCuentasADRequest;
import cl.uandes.panel.tools.actualizaCuentasAD.bean.dto.AdCuentasCreadasDTO;

public class GeneraDatos {

	@EndpointInject(uri = "sql:classpath:sql/qryAdCuentasCreadas.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryAdCuentasCreadas;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Genera lista para crear cuentas AD segun el request recibido
	 * 
	 * @param exchange
	 */
	public void generaListaXrequest(Exchange exchange) {
		Message message = exchange.getIn();
		List<AdCuentasCreadasDTO> lista = new ArrayList<AdCuentasCreadasDTO>();
		ActualizaCuentasADRequest request = (ActualizaCuentasADRequest)message.getHeader("request");
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("fechaDesde", request.getTimestampFechaDesde());
		headers.put("fechaHasta", request.getTimestampFechaHasta());
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryAdCuentasCreadas.requestBodyAndHeaders(null, headers);
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
		
	}
}
