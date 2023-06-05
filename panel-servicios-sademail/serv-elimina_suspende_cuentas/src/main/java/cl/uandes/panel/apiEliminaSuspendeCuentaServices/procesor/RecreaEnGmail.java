package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosRecrearDTO;
import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class RecreaEnGmail implements Processor {

	@EndpointInject(uri = "cxfrs:bean:deleteUserGMail") // eliminar en Gmail la cuenta suspendida
	ProducerTemplate apiDeleteUser;
	private final String templateDeleteUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/delete/%s";

	@EndpointInject(uri = "cxfrs:bean:crearUserGMail") // crear en Gmail la cuenta suspendida
	ProducerTemplate apiCrearUser;
	private final String urlCrearUsuario = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/create";

	@EndpointInject(uri = "cxfrs:bean:undeleteUserGMail") // undelete en Gmail la cuenta eliminada
	ProducerTemplate apiUndeleteUser;
	private final String templateUndeleteUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/undelete/%s";

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<DatosLeidosRecrearDTO> lista = (List<DatosLeidosRecrearDTO>) exchange.getIn().getHeader("listaCuentas");
		DatosLeidosRecrearDTO datos = lista.remove(0);
		exchange.getIn().setHeader("datosRecrear", datos);
		String msg_resultado = "OK";
		String resultado = "OK";
		String cuenta = datos.getCuenta();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");

		// eliminar la cuenta
		boolean result = deleteEnGmail(cuenta);
		if (result) {
			// recrear la cuenta con el mismo nombre
			UserResponse response = crearEnGmail(datos);
			if (response.getCodigo() == 0) {
				User user = response.getUser();
				exchange.getIn().setHeader("new_id_gmail", user.getId());
				exchange.getIn().setHeader("loginName", datos.getLoginName());
				msg_resultado = String.format("recrea %s : OK", sdf.format(new java.util.Date()));
			} else {
				resultado = "NOK";
				msg_resultado = String.format("recrea.err %s : No pudo crear cuenta %s en GMail", sdf.format(new java.util.Date()), cuenta);
				undeleteEnGmail(datos.getIdGmail());
			}
		} else {
			resultado = "NOK";
			msg_resultado = String.format("recrea.err %s : No pudo eliminar cuenta %s en GMail", sdf.format(new java.util.Date()), cuenta);
		}
		exchange.getIn().setHeader("key", datos.getKey());
		exchange.getIn().setHeader("resultado", resultado);
		exchange.getIn().setHeader("msg_resultado", msg_resultado);

	}

	private boolean deleteEnGmail(String loginName) {
		String url = String.format(templateDeleteUri, loginName);
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

	private UserResponse crearEnGmail(DatosLeidosRecrearDTO datos) {
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

	private boolean undeleteEnGmail(String idGmail) {
		String url = String.format(templateUndeleteUri, idGmail);
		logger.info(String.format("idGmail: %s url: %s", idGmail, url));
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "POST");
		ResponseImpl response = (ResponseImpl) apiUndeleteUser.requestBodyAndHeaders(null, headers );
		if (response.getStatus() < 300) {
				return true;
		}
		else
		return false;
	}


}
