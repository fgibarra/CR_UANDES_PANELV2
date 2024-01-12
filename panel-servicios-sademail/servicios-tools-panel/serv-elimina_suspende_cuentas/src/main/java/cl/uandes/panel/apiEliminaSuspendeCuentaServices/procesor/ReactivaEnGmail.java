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

	@EndpointInject(uri = "cxfrs:bean:reactivarUserGMail") // reactiva en Gmail la cuenta suspendida
	ProducerTemplate apiReactivaUser;
	private final String templateReactivarUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/reactivar/%s";

	@EndpointInject(uri = "cxfrs:bean:undeleteUserGMail") // undelete en Gmail la cuenta eliminada
	ProducerTemplate apiUndeleteUser;
	private final String templateUndeleteUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/undelete/%s";

	@EndpointInject(uri = "cxfrs:bean:deleteUserGMail") // eliminar en Gmail la cuenta suspendida
	ProducerTemplate apiDeleteUser;
	private final String templateDeleteUri = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/delete/%s";

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<DatosReactivarBannerDTO> lista = (List<DatosReactivarBannerDTO>) exchange.getIn().getHeader("listaCuentas");
		DatosReactivarBannerDTO datos = lista.remove(0);
		String loginName = datos.getLoginName();
		String status = datos.getStatus();
		String idGmail = datos.getIdGmail();
		Integer key = datos.getKey();
		String accion = "";
		logger.info(String.format("reactivar a %s status=%s key=%d idGmail=%s", loginName, status, key, idGmail));
		boolean result = false;
		if ("Suspended".equalsIgnoreCase(status)) {
			result = reactivaEnGmail(loginName);
			accion = "reactiva";
			logger.info(String.format("reactivar a %s resultado en gmail %b cuenta: %s",
					loginName, result, datos.getCuenta()));
		} else if ("Deleted".equalsIgnoreCase(status)) {
			accion = "undelete";
			result = undeleteEnGmail(idGmail);
		} else if ("Recreada".equalsIgnoreCase(status)) {
			accion = "restituye";
			result = restituyeEnGmail(datos);
		}
		
		if (result) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			exchange.getIn().setHeader("resultado", String.format("%s A LAS %s",accion, sdf.format(new java.util.Date())));
			exchange.getIn().setHeader("cuenta", datos);
			exchange.getIn().setHeader("key", key);
			exchange.getIn().setHeader("datos", datos);
		}
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

	private boolean reactivaEnGmail(String loginName) {
		String url = String.format(templateReactivarUri, loginName);
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

	private boolean restituyeEnGmail(DatosReactivarBannerDTO datos) {
		// borrar la cuenta nueva y undelete la antigua
		logger.info(String.format("restituyeEnGmail: borra cuenta %s old_id: %s new_id: %s", 
				datos.getLoginName(),  datos.getIdGmail(), datos.getIdGmailNueva()));
		boolean result = deleteEnGmail(datos.getLoginName());
		logger.info(String.format("restituyeEnGmail: result=%b",result));
		if (result) {
			logger.info(String.format("restituyeEnGmail: undelete id: %s", datos.getIdGmail()));
			result = undeleteEnGmail(datos.getIdGmail());
			logger.info(String.format("restituyeEnGmail: result=%b",result));
			// pasar de suspendida a activa
			result = reactivaEnGmail(datos.getLoginName());
		}
		return result;
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
}
