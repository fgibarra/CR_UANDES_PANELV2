package cl.uandes.panel.servicio.scheduler.bean.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendmailParamsDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -7469394586841654507L;
	@JsonProperty("EMAIL_WEBMASTER")
	private String emailWebMaster;
	@JsonProperty("EMAIL_SUPPORT")
	private String[] emailsSoporte;
	@JsonProperty("AZURE_DOMINIO")
	private String dominioAzure;
	
	public SendmailParamsDTO(Map<String, Object> datos) {
		this.emailWebMaster = (String)datos.get("EMAIL_WEBMASTER");
		this.dominioAzure = (String)datos.get("AZURE_DOMINIO");
		String valor = (String)datos.get("EMAIL_SUPPORT");
		if (valor != null)
			this.emailsSoporte = valor.split(";");
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}		
	}

	public String getEmailWebMaster() {
		return emailWebMaster;
	}

	public String[] getEmailsSoporte() {
		return emailsSoporte;
	}

	public String getDominioAzure() {
		return dominioAzure;
	}

}
