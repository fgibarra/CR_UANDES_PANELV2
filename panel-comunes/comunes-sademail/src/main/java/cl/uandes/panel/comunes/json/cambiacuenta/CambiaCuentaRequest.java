package cl.uandes.panel.comunes.json.cambiacuenta;

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
public class CambiaCuentaRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -8205241458158590641L;
	@JsonProperty("rut")
	private String rut;
	@JsonProperty("nuevo-nombre-cuenta")
	private String nuevoNombreCuenta;
	@JsonProperty("envia-mail")
	private Boolean enviaMail;
	@JsonProperty("cuentas-envio")
	private String cuentasEnvio;

	@JsonCreator
	public CambiaCuentaRequest(
			@JsonProperty("rut")String rut, 
			@JsonProperty("nuevo-nombre-cuenta")String nuevoNombreCuenta,
			@JsonProperty("envia-mail")Boolean enviaMail,
			@JsonProperty("cuentas-envio")String cuentasEnvio) {
		super();
		this.rut = rut;
		this.nuevoNombreCuenta = nuevoNombreCuenta;
		this.enviaMail = enviaMail;
		this.cuentasEnvio = cuentasEnvio;
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

	public String getRut() {
		return rut;
	}

	public String getNuevoNombreCuenta() {
		return nuevoNombreCuenta;
	}

	public Boolean getEnviaMail() {
		return enviaMail;
	}

	public String getCuentasEnvio() {
		return cuentasEnvio;
	}

}
