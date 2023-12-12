package cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;

import cl.uandes.panel.comunes.json.sendmail.SendmailRequest;
import cl.uandes.panel.comunes.servicios.dto.SendmailParamsDTO;

public class PreparaDatosMail implements Processor {

	@EndpointInject(uri = "sql:classpath:sql/qryKcoSendmailParams.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryKcoSendmailParams;
	@PropertyInject(value = "se-cuentas-gmail.mailAviso.asunto", defaultValue="Se ha sobrepasado cuota asignada para su cuenta de correo miuandes.cl")
	private String asunto;
	@PropertyInject(value = "serv.mailPanel.mail_from", defaultValue="panel@miuandes.cl")
	private String from;

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String mensaje = (String) message.getBody();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = (List<Map<String, Object>>)qryKcoSendmailParams.requestBody(null);
		SendmailParamsDTO params = new SendmailParamsDTO(lista.get(0));
		
		SendmailRequest sendmailRequest = new SendmailRequest(params.getEmailWebMaster(), 
				params.getEmailsSoporte(), from, null, getAsunto(), mensaje);
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
