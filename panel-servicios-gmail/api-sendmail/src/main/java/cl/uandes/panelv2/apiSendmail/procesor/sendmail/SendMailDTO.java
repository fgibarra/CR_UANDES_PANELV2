package cl.uandes.panelv2.apiSendmail.procesor.sendmail;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

/**
 * Datos para la funcion de envio de cortreos mediante el api javax.mail
 * 
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMailDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -5306747683532857887L;
	@JsonProperty("to")
	private String to;
	@JsonProperty("cc")
	private String cc[];
	@JsonProperty("from")
	private String from;
	@JsonProperty("fileName")
	private String fileName;
	@JsonIgnore
	private InputStream inputStream;
	@JsonIgnore
	private Object attachments;
	@JsonProperty("subject")
	private String subject;
	@JsonProperty("mensaje")
	private String mensaje;
	@JsonProperty("exitoso")
	private boolean exitoso;
	@JsonProperty("msgError")
	private String msgError;

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setExitoso(boolean exitoso) {
		this.exitoso = exitoso;
	}

	public boolean isExitoso() {
		return exitoso;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public String getMsgError() {
		return msgError;
	}

	public String getNombreArchivo() {
		File file = new File(fileName);
		return file.getName();
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Object getAttachments() {
		return attachments;
	}

	public void setAttachments(Object attachments) {
		this.attachments = attachments;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}
}
