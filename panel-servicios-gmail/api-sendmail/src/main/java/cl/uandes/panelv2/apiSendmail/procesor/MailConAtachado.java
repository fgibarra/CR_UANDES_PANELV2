package cl.uandes.panelv2.apiSendmail.procesor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.gmail.json.Atachados;
import cl.uandes.sadmemail.comunes.gmail.json.SendmailRequest;
import cl.uandes.sadmemail.comunes.gmail.json.SendmailResponse;
import cl.uandes.panelv2.apiSendmail.procesor.sendmail.SendMail;
import cl.uandes.panelv2.apiSendmail.procesor.sendmail.SendMailDTO;

/**
 * Action<br>
 * Este Action es invocado por la ruta con-atachadoRoute cuando hay que enviar un correo con archivos adjuntos.<br>
 * Recupera desde los parametros en cl.uandes.panelv2.cfg las siguientes propiedades:<br>
 * <ul>
 * <li>serv.mailPanel.mail_smtp</li>
 * <li>serv.mailPanel.mail_username</li>
 * <li>serv.mailPanel.mail_password</li>
 * <li>serv.mailPanel.port</li>
 * <li>serv.mailPanel.tls</li>
 * <li>erv.mailPanel.mail_from</li>
 * </ul>
 * En el blueprint.xml se puede habilitar la opcion debug del api javax.mail<br>
 * @author fernando
 *
 */
public class MailConAtachado implements Processor {

    @PropertyInject(value = "serv.mailPanel.mail_smtp", defaultValue="smtp.outlook.com")
	private String smtp;
    @PropertyInject(value = "serv.mailPanel.mail_username", defaultValue="fgibarra@2z1g4q.onmicrosoft.com")
	private String username;
    @PropertyInject(value = "serv.mailPanel.mail_password", defaultValue="F1b4rr4PirqueP#64")
	private String password;
    @PropertyInject(value = "serv.mailPanel.port", defaultValue="587")
	private String port;
    @PropertyInject(value = "serv.mailPanel.tls", defaultValue="true")
	private String tls;
    @PropertyInject(value = "serv.mailPanel.mail_from", defaultValue="panel-noreply@2z1g4q.onmicrosoft.com")
	private String fromDefault;
    private String debugSMTP;
    
    Logger logger = Logger.getLogger(getClass());
    SendMail sendMail;
    
	public MailConAtachado(String debugSMTP) {
		super();
		this.debugSMTP = debugSMTP;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer codigo = 0;
		String msg = "OK";
		init();
		SendmailRequest request = (SendmailRequest)exchange.getIn().getBody();
		logger.info(String.format("recibe: %s", request));
		logger.info(String.format("sendMail es %s NULO", sendMail!=null?"NO":""));
		
		SendMailDTO dto = new SendMailDTO();
		dto.setTo(request.getTo());
		if (request.getFrom() == null)
			dto.setFrom(getFromDefault());
		else
			dto.setFrom(request.getFrom());
		dto.setCc(request.getCc());
		dto.setMensaje(request.getMensaje());
		dto.setSubject(request.getSubject());
		List<String> attachments = new ArrayList<String>();
		dto.setAttachments(attachments);
		for (Atachados atachado: request.getAtachados()) {
			attachments.add(atachado.getFileName());
		}
		logger.info(String.format("dto: %s", dto.toString()));
		
		dto = sendMail.send(dto);
		if (!dto.isExitoso()) {
			codigo = -1;
			msg = dto.getMsgError();
		}
			
		SendmailResponse response = new SendmailResponse(codigo, msg);
		exchange.getIn().setBody(response);
	}

	private void init() {
		Properties smtpProp = new Properties();
		smtpProp.put(SendMail.SMTP_SERVER, getSmtp());
		smtpProp.put(SendMail.SMTP_USER, getUsername());
		smtpProp.put(SendMail.SMTP_PASSWORD, getPassword());
		smtpProp.put(SendMail.SMPT_PORT, getPort());
		smtpProp.put(SendMail.SMTP_TLS, getTls());
		smtpProp.put(SendMail.SMTP_DEBUG, getDebugSMTP());
		this.sendMail = SendMail.getInstance(smtpProp);
	}

	public String getSmtp() {
		return smtp;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
	}

	public String getTls() {
		return tls;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setTls(String tls) {
		this.tls = tls;
	}

	public String getFromDefault() {
		return fromDefault;
	}

	public void setFromDefault(String fromDefault) {
		this.fromDefault = fromDefault;
	}

	public String getDebugSMTP() {
		return debugSMTP;
	}

}
