package cl.uandes.panel.servicio.getSamaccountName.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;

public class GeneraRespuesta implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String msgError = (String) message.getHeader("exception");
		ServiciosLDAPResponse response;
		if (msgError == null) {
			String body = (String) message.getBody();
			response = new ServiciosLDAPResponse(0, body);
		} else
			response = new ServiciosLDAPResponse(-1, msgError);
		
		message.setBody(response);
		logger.info(String.format("GeneraRespuesta: deja en body: %s", message.getBody()));
	}

}
