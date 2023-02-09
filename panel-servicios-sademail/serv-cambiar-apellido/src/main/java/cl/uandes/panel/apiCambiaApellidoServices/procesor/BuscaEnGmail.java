package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCambiaApellidoServices.procesor.exceptions.NoPudoAccederGmailException;
import cl.uandes.sadmemail.comunes.gmail.json.AliasResponse;
import cl.uandes.sadmemail.comunes.gmail.json.ResultadoGmail;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

/**
 * @author fernando
 *
 * Busca en GMail si login_name esta ocupado o disponible.
 * Recupera el login_name propuesto desde el ${header.new_login_name}
 * Si ya existe secuencia, la base se encuentra en header.new_login_name_base y la secuencia 
 * en header.new_login_name_seq
 * con el loginName valida existencia user y nickname. Si cualquiera de los 2 esta ocupado incrementa la sequencia en uno;
 * si ambos estan desocupados cambia header.foundName a 0
 */
public class BuscaEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:consultaUserGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiRetrieveUser;
	private final String templateUriUser = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/retrieve/%s";

	@EndpointInject(uri = "cxfrs:bean:consultaAliasGMail") // recupera de Gmail los datos de la cuenta
	ProducerTemplate apiRetrieveNickname;
	private final String templateUriNickname = "http://localhost:8181/cxf/ESB/panel/gmailServices/nickName/retrieve/%s";

	private Logger logger = Logger.getLogger(getClass());
	String loginName;
	String base;
	Integer seq;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		loginName = (String)exchange.getIn().getHeader("new_login_name");
		base = (String)exchange.getIn().getHeader("new_login_name_base");
		seq = (Integer)exchange.getIn().getHeader("new_login_name_seq");

		if (base == null) base = loginName;
		if (seq == null) seq = Integer.valueOf(0);
		
		try {
			// buscar cuenta en GMail
			boolean estaEnGmail = buscaEnGmail(loginName, templateUriUser, apiRetrieveUser, true);
			if (estaEnGmail) 
				preparaSiguienteIteracion(exchange);
			else {
				// buscar nickname
				estaEnGmail = buscaEnGmail(loginName, templateUriNickname, apiRetrieveNickname, false);
				if (estaEnGmail)
					preparaSiguienteIteracion(exchange);
				else {
					// FOUND
					exchange.getIn().setHeader("foundName", Integer.valueOf(0));
				}
			}
		} catch (Exception e) {
			logger.error("process", e);
			throw new NoPudoAccederGmailException(e.getMessage());
		}
		
	}

	private void preparaSiguienteIteracion(Exchange exchange) {
		// incrementar la secuencia, actualizar el header
		// formar el nuevo loginName
		seq++;
		loginName = String.format("%s%d", base, seq);
		exchange.getIn().setHeader("new_login_name_seq", seq);
		exchange.getIn().setHeader("new_login_name", loginName);
	}

	private boolean buscaEnGmail(String loginName, String templateUri, ProducerTemplate api, boolean isUser) throws Exception {
		String url = String.format(templateUri, loginName);
		logger.info(String.format("loginName: %s url: %s", loginName, url));
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "GET");
		ResponseImpl response = (ResponseImpl) api.requestBodyAndHeaders(null, headers );
		if (response.getStatus() < 300) {
			StringBuffer sb = new StringBuffer();
			java.io.SequenceInputStream in = (SequenceInputStream) response.getEntity();
			int incc;
			while ((incc=in.read()) != -1)
				sb.append((char)incc);
			String jsonString = new String(sb.toString().getBytes("ISO-8859-1"), "UTF-8");
			logger.info(String.format("process: leyo: %s", jsonString));
			ResultadoGmail apiResponse = null;
			if (isUser) {
				apiResponse = (UserResponse) JSonUtilities.getInstance().json2java(String.format("{\"UserResponse\":%s}", jsonString), UserResponse.class);
				logger.info(String.format("userResponse: %s", apiResponse==null?"VIENE NULO":apiResponse.toString()));
			} else {
				apiResponse = (AliasResponse) JSonUtilities.getInstance().json2java(String.format("{\"AliasResponse\":%s}", jsonString), AliasResponse.class);
				logger.info(String.format("userResponse: %s", apiResponse==null?"VIENE NULO":apiResponse.toString()));
			}
			if (apiResponse.getCodigo() == 0)
				return true;
			else
				return false;
		}
		throw new RuntimeException(String.format("status: %d", response.getStatus()));
	
	}
}
