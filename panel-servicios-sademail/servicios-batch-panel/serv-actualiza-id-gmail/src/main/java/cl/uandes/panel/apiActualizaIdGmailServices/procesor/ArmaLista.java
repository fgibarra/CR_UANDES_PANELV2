package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import java.io.SequenceInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class ArmaLista implements Processor {

	@EndpointInject(uri = "cxfrs:bean:consultaGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiRetrieveUser;
	private Logger logger = Logger.getLogger(getClass());
	private final String uriRetrieveUser = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/retrieve?loginName=%s";

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		Integer cuentasActualizadas = 0;
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		for (Map<String, Object> map : resultados) {
			//BigDecimal key = (BigDecimal) map.get("KEY");
			String loginName = URLEncoder.encode(((String)map.get("LOGIN_NAME")).trim(), java.nio.charset.StandardCharsets.UTF_8.toString());
			String url = String.format(uriRetrieveUser, loginName);
			logger.info(String.format("loginName: %s url: %s", loginName, url));
			
			// consultarlo a gmail
			headers.clear();
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
			headers.put("CamelHttpMethod", "GET");
			ResponseImpl response = (ResponseImpl) apiRetrieveUser.requestBodyAndHeaders(null, headers );
			if (response.getStatus() < 300) {
				StringBuffer sb = new StringBuffer();
				java.io.SequenceInputStream in = (SequenceInputStream) response.getEntity();
				int incc;
				while ((incc=in.read()) != -1)
					sb.append((char)incc);
				String jsonString = new String(sb.toString().getBytes("ISO-8859-1"), "UTF-8");
				//logger.info(String.format("process: leyo: %s", jsonString));
				UserResponse userResponse = (UserResponse) JSonUtilities.getInstance().json2java(String.format("{\"UserResponse\":%s}", jsonString), UserResponse.class);
				//logger.info(String.format("userResponse: %s", userResponse==null?"VIENE NULO":userResponse.toString()));
				
				if (userResponse != null && userResponse.getCodigo() == 0) {
					map.put("ID_GMAIL", userResponse.getUser().getId());
				}
			}
		}
		
		exchange.getIn().setHeader("listaCuentas", resultados);
		exchange.getIn().setHeader("cuentasActualizadas", cuentasActualizadas);
		logger.info(String.format("Deja en header.listaCuentas lista size=%d", resultados.size()));
	}

}
