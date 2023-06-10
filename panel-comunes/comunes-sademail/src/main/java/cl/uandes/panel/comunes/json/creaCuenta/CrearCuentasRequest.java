package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearCuentasRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -6927667999493880805L;
	@JsonProperty("esta_en_banner")
	private EnBanner enBanner;
	@JsonProperty("no_esta_en_banner")
	private NoBanner noBanner;
	@JsonProperty("email_informe")
	private String emailInforme;

	@JsonCreator
	public CrearCuentasRequest(
			@JsonProperty("esta_en_banner")EnBanner enBanner, 
			@JsonProperty("no_esta_en_banner")NoBanner noBanner, 
			@JsonProperty("email_informe")String emailInforme) {
		super();
		this.enBanner = enBanner;
		this.noBanner = noBanner;
		this.emailInforme = emailInforme;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public EnBanner getEnBanner() {
		return enBanner;
	}

	public NoBanner getNoBanner() {
		return noBanner;
	}

	public String getEmailInforme() {
		return emailInforme;
	}

}
