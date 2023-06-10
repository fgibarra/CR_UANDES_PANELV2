package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.sadmemail.comunes.utils.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreaCuentaRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -8564977336572059496L;
	@JsonProperty("esta_en_banner")
	private Boolean enBanner;
	@JsonProperty("rut")
	private String rut;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("nombres")
	private String nombreCuenta;
	@JsonProperty("apellidos")
	private String apellidos;
	@JsonProperty("password")
	private String password;
	
	@JsonCreator
	public CreaCuentaRequest(@JsonProperty("esta_en_banner")Boolean enBanner, 
			@JsonProperty("rut")String rut, 
			@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("nombres")String nombreCuenta,
			@JsonProperty("apellidos")String apellidos,
			@JsonProperty("password")String password) {
		super();
		this.enBanner = enBanner;
		this.rut = StringUtils.desformateaRut(rut);
		this.cuenta = cuenta;
		this.nombreCuenta = nombreCuenta;
		this.apellidos = apellidos;
		this.password = password;
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

	@JsonIgnore
	public String getLoginName() {
		if (this.cuenta != null) {
			int endIndex = cuenta.indexOf('@');
			if (endIndex >= 0)
				return this.cuenta.substring(0, endIndex);
		}
		return this.cuenta;
	}

	public Boolean getEnBanner() {
		return enBanner;
	}

	public String getRut() {
		return rut;
	}

	public String getCuenta() {
		return cuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getPassword() {
		return password;
	}

}
