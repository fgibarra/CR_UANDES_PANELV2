package cl.uandes.panel.comunes.json.cambiacuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosCuenta implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -1760561789278101353L;
	@JsonProperty("rut")
	String rut;
	@JsonProperty("spriden_pidm")
	Long spridenPidm;
	@JsonProperty("cuenta")
	String cuenta;
	@JsonProperty("nombres")
	String nombres;
	@JsonProperty("apellidos")
	String apellidos;
	@JsonProperty("id_gmail")
	String idGmail;

	@JsonCreator
	public DatosCuenta(
			@JsonProperty("rut")String rut, 
			@JsonProperty("spriden_pidm")Long spridenPidm, 
			@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("nombres")String nombres, 
			@JsonProperty("apellidos")String apellidos, 
			@JsonProperty("id_gmail")String idGmail) {
		super();
		this.rut = rut;
		this.spridenPidm = spridenPidm;
		this.cuenta = cuenta;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.idGmail = idGmail;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public Long getSpridenPidm() {
		return spridenPidm;
	}

	public void setSpridenPidm(Long spridenPidm) {
		this.spridenPidm = spridenPidm;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getIdGmail() {
		return idGmail;
	}

	public void setIdGmail(String idGmail) {
		this.idGmail = idGmail;
	}

}
