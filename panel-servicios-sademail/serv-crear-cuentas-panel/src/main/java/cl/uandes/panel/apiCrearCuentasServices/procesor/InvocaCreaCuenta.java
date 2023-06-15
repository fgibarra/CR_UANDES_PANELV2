package cl.uandes.panel.apiCrearCuentasServices.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class InvocaCreaCuenta implements Processor {

	@EndpointInject(uri = "cxfrs:bean:creaCuentaPanel") // crear en Gmail la cuenta suspendida
	ProducerTemplate apiCrearCuenta;
	private final String urlCrearCuenta = "http://localhost:8181/cxf/ESB/panel/panelToolCreaCuenta/creaCuenta";

	private Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		Integer countEnBanner = (Integer)exchange.getIn().getHeader("countEnBanner");
		Integer countNoBanner = (Integer)exchange.getIn().getHeader("countNoBanner");
		Integer errores = (Integer)exchange.getIn().getHeader("errores");
		
		List<CreaCuentaRequest> lista = (List<CreaCuentaRequest>) exchange.getIn().getHeader("crearCuentas");
		List<CreaCuentaResponse> listaResponse = (List<CreaCuentaResponse>) exchange.getIn().getHeader("crearCuentasResponses");
		CreaCuentaRequest request = lista.remove(0);
		logger.info(String.format("InvocaCreaCuenta: request: %s", request));
		CreaCuentaResponse response = crearEnPanel(request);
		logger.info(String.format("InvocaCreaCuenta: response: %s", response));
		if (response.getCodigo() == 0) {
			if (request.getEnBanner())
				countEnBanner++;
			else
				countNoBanner++;
		} else {
			errores++;
		}
		listaResponse.add(response);
		exchange.getIn().setHeader("countEnBanner", countEnBanner);
		exchange.getIn().setHeader("countNoBanner", countNoBanner);
		exchange.getIn().setHeader("errores", errores);
	}

	private CreaCuentaResponse crearEnPanel(CreaCuentaRequest request) {
		
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// crearlo en el panel
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, urlCrearCuenta);
		headers.put("CamelHttpMethod", "POST");
		ResponseImpl responseImpl = (ResponseImpl) apiCrearCuenta.requestBodyAndHeaders(request, headers );
		if (responseImpl.getStatus() < 300) {
			try {
				StringBuffer sb = new StringBuffer();
				java.io.SequenceInputStream in = (java.io.SequenceInputStream) responseImpl.getEntity();
				int incc;
				while ((incc=in.read()) != -1)
					sb.append((char)incc);
				String jsonString =  sb.toString();
				return (CreaCuentaResponse)JSonUtilities.getInstance().json2java(jsonString, CreaCuentaResponse.class, false);
			} catch (Exception e) {
				logger.error("crearEnGmail", e);
				return new CreaCuentaResponse(-1, e.getMessage(), null, null, null, null, null, null);
			}
		}
		return new CreaCuentaResponse(-1, String.format("HTTP Status: %d", responseImpl.getStatus()), null, null, null, null, null, null);
	}

}
