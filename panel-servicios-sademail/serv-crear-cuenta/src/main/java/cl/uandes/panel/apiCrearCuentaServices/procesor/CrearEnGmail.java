package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;
import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class CrearEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:crearUserGMail") // crear en Gmail la cuenta suspendida
	ProducerTemplate apiCrearUser;
	private final String urlCrearUsuario = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/create";

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		DatosMiCuentasGmailDTO datos = (DatosMiCuentasGmailDTO)exchange.getIn().getHeader("datosMiCuentasGmail");
		UserResponse response = crearEnGmail(datos);
		if (response.getCodigo() == 0) {
			datos.setIdGmail(response.getUser().getId());
			logger.info(String.format("CrearEnGmail: datosMiCuentasGmail: %s", response.getUser(), datos));
		}
		logger.info(String.format("CrearEnGmail: UserResponse: %s", response));
		exchange.getIn().setHeader("cuentaCreadaGmail", response);
	}

	private UserResponse crearEnGmail(DatosMiCuentasGmailDTO datos) {
		User user = new User(null, datos.getLoginName(), datos.getNombres(), datos.getApellidos(), datos.getPassword());
		UserRequest request = new UserRequest(user);
		
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, urlCrearUsuario);
		headers.put("CamelHttpMethod", "POST");
		ResponseImpl responseImpl = (ResponseImpl) apiCrearUser.requestBodyAndHeaders(request, headers );
		if (responseImpl.getStatus() < 300) {
			try {
				StringBuffer sb = new StringBuffer();
				java.io.SequenceInputStream in = (java.io.SequenceInputStream) responseImpl.getEntity();
				int incc;
				while ((incc=in.read()) != -1)
					sb.append((char)incc);
				String jsonString =  sb.toString();
				return (UserResponse)JSonUtilities.getInstance().json2java(jsonString, UserResponse.class, false);
			} catch (Exception e) {
				logger.error("crearEnGmail", e);
				return new UserResponse(-1, e.getMessage(), null);
			}
		}
		return new UserResponse(-1, String.format("HTTP Status: %d", responseImpl.getStatus()), null);
	}

}
