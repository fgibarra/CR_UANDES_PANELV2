package cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosReactivarBannerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4040115810983848919L;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("key")
	private Integer key;
	@JsonProperty("status")
	private String status;
	@JsonProperty("id_gmail")
	String idGmail;
	@JsonProperty("id_gmail_nueva")
	String idGmailNueva;

	public DatosReactivarBannerDTO(Map<String, Object> mapDatos) {
		super();
		this.cuenta = (String)mapDatos.get("CUENTA");
		this.key = (Integer.valueOf(((BigDecimal)mapDatos.get("KEY")).intValue()));
		this.status = (String)mapDatos.get("STATUS");
		this.idGmail = (String)mapDatos.get("ID_GMAIL");
		this.idGmailNueva = (String)mapDatos.get("ID_GMAIL_NUEVA");
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	@JsonIgnore
	public String getLoginName() {
		if (this.cuenta != null) {
			int index = cuenta.indexOf('@');
			if (index >= 0)
				return this.cuenta.substring(0, index);
		}
		return cuenta;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdGmail() {
		return idGmail;
	}

	public void setIdGmail(String idGmail) {
		this.idGmail = idGmail;
	}

	public String getIdGmailNueva() {
		return idGmailNueva;
	}

	public void setIdGmailNueva(String idGmailNueva) {
		this.idGmailNueva = idGmailNueva;
	}

}
