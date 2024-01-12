package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;

public class ArmaDatosPrdCreaEnBD implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		DatosMiCuentasGmailDTO datos = (DatosMiCuentasGmailDTO)exchange.getIn().getHeader("datosMiCuentasGmail");
		Map<String,Object> headers = exchange.getIn().getHeaders();
		headers.put("moodle_id", datos.getMoodleId());
		headers.put("banner_pidm", datos.getBannerPidm());
		headers.put("login_name", datos.getLoginName());
		headers.put("nombres", datos.getNombres());
		headers.put("apellidos", datos.getApellidos());
		headers.put("id_gmail", datos.getIdGmail());
		headers.put("funcion", "DO");
		exchange.getIn().setHeaders(headers);
	}

}
