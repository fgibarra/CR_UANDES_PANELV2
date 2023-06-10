package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosCrearCuenta implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 3236616561886720330L;
	@JsonProperty("rut")
	private String rut;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("nombres")
	private String nombreCuenta;
	@JsonProperty("apellidos")
	private String apellidos;

	@JsonCreator
	public DatosCrearCuenta(
			@JsonProperty("rut")String rut, 
			@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("nombres")String nombreCuenta, 
			@JsonProperty("apellidos")String apellidos) {
		super();
		this.rut = rut;
		this.cuenta = cuenta;
		this.nombreCuenta = nombreCuenta;
		this.apellidos = apellidos;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

}
