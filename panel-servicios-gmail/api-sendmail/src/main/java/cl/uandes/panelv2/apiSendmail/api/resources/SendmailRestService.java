package cl.uandes.panelv2.apiSendmail.api.resources;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.gmail.json.SendmailRequest;
import cl.uandes.sadmemail.comunes.gmail.json.SendmailResponse;


/**
 * URI : cxf/ESB/panelV3/sendMail/<br><br>
 * Funcion documentacion: /help GET<br>
 * Funcion envio de mail: /send POST<br>
 * Los datos del mail deben incluirse en SendmailRequest
 * Recupera desde los parametros en cl.uandes.panelv3.cfg las siguientes propiedades:<br>
 * <ul>
 * <li>serv.mailPanel.mail_smtp</li>
 * <li>serv.mailPanel.mail_cc</li>
 * <li>serv.mailPanel.mail_username</li>
 * <li>serv.mailPanel.mail_password</li>
 * <li>erv.mailPanel.mail_from</li>
 * </ul>
 * Si el correo no contiene adjuntos lo envia usando la componente SMTP de Camel
 * @author fernando
 *
 */
@Path("/")
public class SendmailRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	String templateSmtpsURI = "smtps://%s?username=%s&password=%s&from=%s&contentType=text/html;charset=ISO-8859-1";
	
    @PropertyInject(value = "serv.mailPanel.mail_smtp", defaultValue="smtp.gmail.com")
	private String smtp;
    @PropertyInject(value = "serv.mailPanel.mail_cc", defaultValue="fgibarra@firsa.cl")
	private String ccDefault;
    @PropertyInject(value = "serv.mailPanel.mail_username", defaultValue="fgibarra@2z1g4q.onmicrosoft.com")
	private String username;
    @PropertyInject(value = "serv.mailPanel.mail_password", defaultValue="F1b4rr4PirqueP#64")
	private String password;
    @PropertyInject(value = "serv.mailPanel.mail_from", defaultValue="panel-noreply@2z1g4q.onmicrosoft.com")
	private String fromDefault;
	
	Logger logger = Logger.getLogger(getClass());

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/help")
	public Response getDocumentacion() {
		String type = "application/pdf";
		java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("documentacion.pdf");		
		Response response = Response.ok(is, type).build();
		return response;
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/send")
	public SendmailResponse sendmail(SendmailRequest request) {
		Integer codigo = 0;
		String msg = "OK";
		logger.info(String.format("request: %s", request.toString()));
		if (request.getAtachados() != null) {
			// Hay que hacerlo por API
			return (SendmailResponse)producer.requestBodyAndHeader(request, "con-atachado", "SI");
		}
		
		// sin atachados, se usa el api de camel
		try {
			CamelContext context = producer.getCamelContext();
			logger.info(String.format("template=%s", templateSmtpsURI));
			String uri = String.format(templateSmtpsURI, 
					getSmtp(), getUsername(), getPassword(), 
					request.getFrom() != null ? request.getFrom() : getFromDefault());
			logger.info(String.format("uri=%s", uri));
			Endpoint endpoint = context.getEndpoint(uri);
			
			Exchange exchange = endpoint.createExchange();
			Message in = exchange.getIn();
			
			Map<String,Object> headers = new HashMap<String,Object>();
			StringBuffer cc = new StringBuffer();
			if (request.getCc() != null) {
				for (String c : request.getCc())
					cc.append(c).append(';');
				cc.setLength(cc.length() - 1);
			} else
				cc.append(getCcDefault());
			headers.put("cc", cc.toString());
			headers.put("contentType", "text/html;charset=ISO-8859-1");
			headers.put("Subject", request.getSubject());
			headers.put("To", request.getTo());
			headers.put("cc", cc.toString());
			
			in.setHeaders(headers);
			in.setBody(toIso88591(request.getMensaje()));
			
			Producer producer = endpoint.createProducer();
			producer.start();
			producer.process(exchange);
		} catch (Exception e) {
			codigo = -1;
			msg = e.getMessage();
			logger.error("sendmail", e);
		}
		SendmailResponse response = new SendmailResponse(codigo, msg);
		return response;
	}

	Charset utf8charset = Charset.forName("UTF-8");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	private String toIso88591(String input) {
		ByteBuffer inputBuffer = ByteBuffer.wrap(input.getBytes());
		CharBuffer data = utf8charset.decode(inputBuffer);
		ByteBuffer outputBuffer = iso88591charset.encode(data);
		return new String(outputBuffer.array());
	}

	public ProducerTemplate getProducer() {
		return producer;
	}

	public void setProducer(ProducerTemplate producer) {
		this.producer = producer;
	}

	public String getCcDefault() {
		return ccDefault;
	}

	public void setCcDefault(String ccDefault) {
		this.ccDefault = ccDefault;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFromDefault() {
		return fromDefault;
	}

	public void setFromDefault(String fromDefault) {
		this.fromDefault = fromDefault;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}
}
