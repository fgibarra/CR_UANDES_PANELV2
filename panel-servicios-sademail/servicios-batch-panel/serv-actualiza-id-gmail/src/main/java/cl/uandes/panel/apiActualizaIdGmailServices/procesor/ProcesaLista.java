package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import java.io.SequenceInputStream;
import java.math.BigDecimal;
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

public class ProcesaLista implements Processor {

	@EndpointInject(uri = "cxfrs:bean:consultaGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiRetrieveUser;

	ProducerTemplate apiUpdate;
	
	private Logger logger = Logger.getLogger(getClass());
	private final String uriRetrieveUser = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/retrieve/%s";
	private final String updateUri = "sql:update mi_cuentas_gmail set id_gmail=:#${header.idGmail} where key=:#{header.key}?dataSource=#bannerDataSource";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// viene en el body el resultado del query
		apiUpdate = exchange.getContext().createProducerTemplate();
		final Map<String,Object> headers = new HashMap<String,Object>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		int cuentasActualizadas = 0;
		
		for (Map<String, Object> map : resultados) {
			BigDecimal key = (BigDecimal) map.get("KEY");
			String loginName = (String)map.get("LOGIN_NAME");
			String url = String.format(uriRetrieveUser, loginName);
			logger.info(String.format("key: %s loginName: %s url: %s", key, loginName, url));
			
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
				logger.info(String.format("process: leyo: %s", jsonString));
				UserResponse userResponse = (UserResponse) JSonUtilities.getInstance().json2java(String.format("{\"UserResponse\":%s}", jsonString), UserResponse.class);
				logger.info(String.format("userResponse: %s", userResponse==null?"VIENE NULO":userResponse.toString()));
				
				if (userResponse != null && userResponse.getCodigo() == 0) {
					// actualizarlo en la BD
					headers.clear();
					headers.put("key",key);
					headers.put("idGmail", userResponse.getUser().getId());
					logger.info(String.format("sql:update key: %s idGmail: %s", key, userResponse.getUser().getId()));
					Object resp = apiUpdate.requestBodyAndHeaders(updateUri, exchange.getIn().getBody(), headers);
					String clase = resp == null ? "NULL" : resp.getClass().getName();
					String valor = resp == null ? "NULL" : resp.toString();
					logger.info(String.format("resp.class: %s resp: %s", clase, valor));
					/*
					apiUpdate.request(updateUri, exchangeBD -> {
						exchangeBD.getIn().setHeaders(headers);
					});
					*/
					cuentasActualizadas++;
				}
			}
		}
	}

}
