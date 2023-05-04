package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosReactivarBannerDTO;

public class ReactivaEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:reactivarUserGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiReactivaUser;
	private final String templateUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/reactivar/%s";

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<DatosReactivarBannerDTO> lista = (List<DatosReactivarBannerDTO>) exchange.getIn().getHeader("listaCuentas");
		DatosReactivarBannerDTO datos = lista.remove(0);
		String loginName = datos.getLoginName();
		logger.info(String.format("reactivar a %s", loginName));
		boolean result = reactivaEnGmail(loginName);
		logger.info(String.format("reactivar a %s resultado en gmail %b cuenta: %s",
				loginName, result, datos.getCuenta()));
		if (result) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			exchange.getIn().setHeader("resultado", String.format("reactiva A LAS %s", sdf.format(new java.util.Date())));
			exchange.getIn().setHeader("cuenta", datos.getCuenta());
		}
	}

	private boolean reactivaEnGmail(String loginName) {
		String url = String.format(templateUri, loginName);
		logger.info(String.format("loginName: %s url: %s", loginName, url));
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "PUT");
		ResponseImpl response = (ResponseImpl) apiReactivaUser.requestBodyAndHeaders(null, headers );
		if (response.getStatus() < 300) {
				return true;
		}
		else
			return false;
	}

}
