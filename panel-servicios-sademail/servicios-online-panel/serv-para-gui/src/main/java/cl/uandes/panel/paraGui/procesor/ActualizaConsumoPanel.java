package cl.uandes.panel.paraGui.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.paraGui.json.ActualizaConsumoPanelRequest;
import cl.uandes.panel.paraGui.json.GuiResponse;

public class ActualizaConsumoPanel implements Processor {

    @PropertyInject(value = "uri.webappUandes-apiServices", defaultValue="http://localhost:8000/api/v1")
	private String webappsUandesServices;

	// SQLs
//	@EndpointInject(uri = "sql:classpath:sql/getConsumoEspacioPanel.sql?dataSource=#bannerDataSource")
	@EndpointInject(uri = "sql:classpath:sql/dummy.sql?dataSource=#bannerDataSource")
	ProducerTemplate getConsumoEspacioPanel;

	// APIREST
	@EndpointInject(uri = "cxfrs:bean:rsActualizaTHPanel?continuationTimeout=-1")
	ProducerTemplate actualizaTHPanel;
	String templateActualizaTHPanel = "%s/th_panel/1/";

	private Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		GuiResponse guiResponse = null;
		List<Map<String,Object>> lista = null;
		ActualizaConsumoPanelRequest request = null;
		
		try {
			lista = (List<Map<String, Object>>) getConsumoEspacioPanel.requestBody(null);
			if (lista != null && lista.size() > 0) {
				request = new ActualizaConsumoPanelRequest(lista.get(0));
				logger.info(String.format("ActualizaConsumoPanel: request(%s)", request));
				
				Map<String, Object> headers = new HashMap<String, Object>();
				headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateActualizaTHPanel,getWebappsUandesServices()));
				headers.put("CamelHttpMethod", "PUT");
				headers.put("Authorization", "ApiKey fuse_user:f4s3_4s3r");
				Response response = (Response) actualizaTHPanel.requestBodyAndHeaders(request, headers);
				logger.info(String.format("ActualizaConsumoPanel: response %d", response.getStatus()));
				if (response.getStatus() < 300) {
					guiResponse = new GuiResponse(0, "OK");
				} else {
					guiResponse = new GuiResponse(-1, response.getStatusInfo().getReasonPhrase());
				}
			} else {
				guiResponse = new GuiResponse(-1, "Qry no trajo resultados");
			}
		} catch (Exception e) {
			guiResponse = new GuiResponse(-1, e.getMessage());
			String msg;
			if (lista == null) {
				msg = String.format("No pudo ejecutar el sql %s", "getConsumoEspacioPanel.sql");
			} else {
				msg = String.format("no pudo ejecutar el request %s", request);
			}
			logger.error(String.format("ActualizaConsumoPanel: %s", msg));
		}
		
		logger.info(String.format("ActualizaConsumoPanel: response %s", guiResponse));
		message.setBody(guiResponse);
	}

	public String getWebappsUandesServices() {
		return webappsUandesServices;
	}

	public void setWebappsUandesServices(String webappsUandesServices) {
		this.webappsUandesServices = webappsUandesServices;
	}

}
