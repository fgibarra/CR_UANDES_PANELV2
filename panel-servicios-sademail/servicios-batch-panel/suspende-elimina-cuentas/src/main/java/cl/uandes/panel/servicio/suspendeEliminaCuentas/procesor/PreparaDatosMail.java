package cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.sendmail.SendmailRequest;
import cl.uandes.sadmemail.comunes.google.api.services.User;

public class PreparaDatosMail implements Processor {

	@PropertyInject(value = "se-cuentas-gmail.mailAviso.asunto", defaultValue="Se ha sobrepasado cuota asignada para su cuenta de correo miuandes.cl")
	private String asunto;
	@PropertyInject(value = "serv.mailPanel.mail_from", defaultValue="panel@miuandes.cl")
	private String from;
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String mensaje = (String) message.getBody();
		User user = (User)message.getHeader("user");
		logger.info(String.format("PreparaDatosMail: recupera user: %s", user));
		
		SendmailRequest sendmailRequest = new SendmailRequest(user.getEmail(), 
				null, from, null, getAsunto(), mensaje);
		message.setBody(sendmailRequest);
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
