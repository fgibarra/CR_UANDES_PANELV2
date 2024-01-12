package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosBannerDTO;

public class EliminaEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:deleteUserGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiDeleteUser;
	private final String templateUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/delete/%s";

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		DatosLeidosBannerDTO datos = (DatosLeidosBannerDTO)exchange.getIn().getHeader("datosCuenta");
		String loginName = datos.getLoginName();
		logger.info(String.format("eliminar a %s id_gmail: %s", loginName, datos.getIdGmail()));
		boolean result = false;
		if (datos.getIdGmail() != null) {
			result = deleteEnGmail(loginName);
		}
		logger.info(String.format("eliminar a %s resultado en gmail %b", loginName, result));
	}
	
	private boolean deleteEnGmail(String loginName) {
		String url = String.format(templateUri, loginName);
		logger.info(String.format("loginName: %s url: %s", loginName, url));
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "DELETE");
		ResponseImpl response = (ResponseImpl) apiDeleteUser.requestBodyAndHeaders(null, headers );
		if (response.getStatus() < 300) {
				return true;
		}
		else
			return false;
	}

}
