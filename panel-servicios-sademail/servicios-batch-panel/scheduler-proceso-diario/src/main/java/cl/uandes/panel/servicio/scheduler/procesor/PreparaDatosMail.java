package cl.uandes.panel.servicio.scheduler.procesor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.sendmail.SendmailRequest;
import cl.uandes.panel.comunes.servicios.dto.SendmailParamsDTO;

/**
 * Coloca el en Body y los headers necesarios para enviar un correo
 * @author fernando
 *
 */
public class PreparaDatosMail implements Processor {

	@EndpointInject(uri = "sql:classpath:sql/qryKcoSendmailParams.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryKcoSendmailParams;
	@PropertyInject(value = "scheduler.mail.asunto", defaultValue="Reporte proceso diario")
	private String asunto;
	@PropertyInject(value = "serv.mailPanel.mail_from", defaultValue="panel@miuandes.cl")
	private String from;
	@PropertyInject(value = "serv.mailPanel.mail_ccxservicio", defaultValue="")
	private String mailCcxservicio;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String mensaje = (String) message.getBody();
		String operacion = (String)message.getHeader("operacion");
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = (List<Map<String, Object>>)qryKcoSendmailParams.requestBody(null);
		SendmailParamsDTO params = new SendmailParamsDTO(lista.get(0));
		
		// agregar al cc los mails especificos
		String copias[] = params.getEmailsSoporte();
		if ("crear_cuentas_AD_postgrado".equals(operacion)) {
			copias = agregarCopias(copias, "crear_cuentas_AD_postgrado");
		}
		SendmailRequest sendmailRequest = new SendmailRequest(params.getEmailWebMaster(), 
				copias, from, null, getAsunto(), mensaje);
		logger.info(String.format("PreparaDatosMail: sendmailRequest.copias: %s", dumpCopias(copias)));
		message.setBody(sendmailRequest);
	}

	private String dumpCopias(String[] copias) {
		StringBuffer sb = new StringBuffer("[");
		for (String s : copias)
			sb.append(s).append(',');
		if (sb.length() > 1)
			sb.setLength(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}

	private String[] agregarCopias(String[] copias, String operacion) {
		if (getMailCcxservicio() != null && getMailCcxservicio().length() > 0) {
			Map<String,String> d = new HashMap<String,String>();
			StringTokenizer st = new StringTokenizer(getMailCcxservicio(), "|");
			while (st.hasMoreTokens()) {
				String valor = st.nextToken();
				String valores[] = valor.split(",");
				d.put(valores[0], valores[1]);
			}
			String mails = d.get(operacion);
			if (mails != null) {
				String m[] = mails.split(";");
				List<String> l = new ArrayList<String>();
				for (String mm : copias) {
					l.add(mm);
				}
				if (m != null && m.length > 0)
					for (String mm : m) {
						l.add(mm);
					}
				return l.toArray(new String[0]);
			} else
				return copias;
		}
		else
			return copias;
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

	public String getMailCcxservicio() {
		return mailCcxservicio;
	}

	public void setMailCcxservicio(String mailCcxservicio) {
		this.mailCcxservicio = mailCcxservicio;
	}

}
