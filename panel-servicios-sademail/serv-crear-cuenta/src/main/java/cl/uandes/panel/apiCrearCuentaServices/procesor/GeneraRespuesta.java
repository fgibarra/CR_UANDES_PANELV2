package cl.uandes.panel.apiCrearCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;
import cl.uandes.sadmemail.comunes.google.api.services.User;

public class GeneraRespuesta implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		CreaCuentaResponse response = null;
		CreaCuentaRequest request = (CreaCuentaRequest) exchange.getIn().getHeader("request");
		String tipoRespuesta = (String) exchange.getIn().getHeader("tipoRespuesta");
		if (tipoRespuesta != null) {
			Boolean existeEnMicuentasGmail = (Boolean) exchange.getIn().getHeader("existeEnMicuentasGmail");
			if (existeEnMicuentasGmail != null && existeEnMicuentasGmail) {
				DatosMiCuentasGmailDTO datos = (DatosMiCuentasGmailDTO) exchange.getIn().getHeader("datosMiCuentasGmail");
				if (datos != null) {
					User user = new User(null, datos.getLoginName(), datos.getNombres(), datos.getApellidos(), null);
					response = new CreaCuentaResponse(-1, tipoRespuesta, request.getRut(), datos.getLoginName(), datos.getNombres(), datos.getApellidos(), null, null);
				}
			} else {
				Boolean existeEnSpriden = (Boolean) exchange.getIn().getHeader("existeEnSpriden");
				if (existeEnSpriden != null && !existeEnSpriden) {
					response = new CreaCuentaResponse(-1, tipoRespuesta, request.getRut(), null, null, null, null, null);
				} else {
					String out_resultado = (String) exchange.getIn().getHeader("out_resultado");
					if (!"OK".equals(out_resultado)) {
						response = new CreaCuentaResponse(-1, tipoRespuesta, request.getRut(), null, null, null, null, null);
					}
				}
			}
		} else {
			// termino OK
			DatosMiCuentasGmailDTO datos = (DatosMiCuentasGmailDTO)exchange.getIn().getHeader("datosMiCuentasGmail");
			User user = new User(String.format("%s@miuandes.cl",datos.getLoginName()), datos.getLoginName(), datos.getNombres(), datos.getApellidos(), datos.getPassword());
			user.setId(datos.getIdGmail());
			response = new CreaCuentaResponse(0, "OK", datos.getMoodleId(), null, null, null, null, null); 
		}
		
		logger.info(String.format("GeneraRespuesta: %s", response));
		exchange.getIn().setBody(response);
	}

}
