package cl.uandes.panel.comunes.json.sendmail;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.JSonUtilities;

/**
 *
 * Requerimiento para el envio de un correo
 * 
 * @author fernando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendmailRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8016731768066284333L;
	@JsonProperty("to")
	private String to;
	@JsonProperty("cc")
	private String cc[];
	@JsonProperty("from")
	private String from;
	@JsonProperty("atachados")
	private Atachados atachados[];
	@JsonProperty("subject")
	private String subject;
	@JsonProperty("mensaje")
	private String mensaje;

	public SendmailRequest() {
		super();
	}

	@JsonCreator
	public SendmailRequest(
			@JsonProperty("to")String to, 
			@JsonProperty("cc")String cc[], 
			@JsonProperty("from")String from, 
			@JsonProperty("atachados")Atachados[] atachados, 
			@JsonProperty("subject")String subject, 
			@JsonProperty("mensaje")String mensaje) {
		super();
		this.to = to;
		this.cc = cc;
		this.from = from;
		this.atachados = atachados;
		this.subject = subject;
		this.mensaje = mensaje;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Atachados[] getAtachados() {
		return atachados;
	}

	public void setAtachados(Atachados[] atachados) {
		this.atachados = atachados;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
