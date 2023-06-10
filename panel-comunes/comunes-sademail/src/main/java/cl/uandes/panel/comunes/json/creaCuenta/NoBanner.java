package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoBanner implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 2080538338811083566L;
	@JsonProperty("cuentas")
	DatosCrearCuenta[] cuentas;
	@JsonProperty("password")
	private String password;

	@JsonCreator
	public NoBanner(@JsonProperty("cuentas")DatosCrearCuenta[] cuentas, 
			@JsonProperty("password")String password) {
		super();
		this.cuentas = cuentas;
		this.password = password;
	}

	public DatosCrearCuenta[] getCuentas() {
		return cuentas;
	}

	public void setCuentas(DatosCrearCuenta[] cuentas) {
		this.cuentas = cuentas;
	}
	
	public String getPassword() {
		return password != null ? password : "UAndes2023";
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
