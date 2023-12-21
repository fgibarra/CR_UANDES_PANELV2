package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosBannerDTO;

public class SuspendeEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:suspendeUserGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiSuspendeUser;
	private final String templateUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/suspend/%s";

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		DatosLeidosBannerDTO datos = (DatosLeidosBannerDTO)exchange.getIn().getHeader("datosCuenta");
		String loginName = datos.getLoginName();
		logger.info(String.format("eliminar a %s", loginName));
		boolean result = false;
		if (datos.getIdGmail() != null) {
			result = suspendeEnGmail(loginName);
		}
		logger.info(String.format("suspender a %s resultado en gmail %b", loginName, result));
	}

	private boolean suspendeEnGmail(String loginName) {
		String url = String.format(templateUri, loginName);
		logger.info(String.format("loginName: %s url: %s", loginName, url));
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "PUT");
		ResponseImpl response;
		try {
			response = (ResponseImpl) apiSuspendeUser.requestBodyAndHeaders(null, headers );
			if (response.getStatus() < 300) {
				return true;
		}
		else
			return false;
		} catch (CamelExecutionException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

}
