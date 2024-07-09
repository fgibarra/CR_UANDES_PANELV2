package cl.uandes.panel.comunes.json.batch.crearcuentas;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 6874203393261409799L;
	@JsonProperty("cuenta")
	String cuenta;
	@JsonProperty("password")
	String password;
	@JsonProperty("rama")
	String rama;
	@JsonProperty("rut")
	String rut;
	@JsonProperty("nombre")
	String nombre;
	@JsonProperty("apellidos")
	String apellidos;
	@JsonProperty("correo")
	String correo;
	@JsonProperty("direccion")
	String direccion;
	@JsonProperty("comuna")
	String comuna;
	@JsonProperty("pidm")
	String pidm;
	@JsonProperty("nivel")
	String nivel;
	@JsonProperty("estado-academico")
	String estadoAcademico;

	@JsonCreator
	public Usuario(@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("password")String password, 
			@JsonProperty("rama")String rama, 
			@JsonProperty("rut")String rut, 
			@JsonProperty("nombre")String nombre, 
			@JsonProperty("apellidos")String apellidos,
			@JsonProperty("correo")String correo, 
			@JsonProperty("direccion")String direccion, 
			@JsonProperty("comuna")String comuna, 
			@JsonProperty("pidm")String pidm, 
			@JsonProperty("nivel")String nivel, 
			@JsonProperty("estado-academico")String estadoAcademico) {
		super();
		this.cuenta = cuenta;
		this.password = password;
		this.rama = rama;
		this.rut = rut;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.correo = correo;
		this.direccion = direccion;
		this.comuna = comuna;
		this.pidm = pidm;
		this.nivel = nivel;
		this.estadoAcademico = estadoAcademico;
	}

	@JsonIgnore
	public static Usuario createUsuario4crear (String nombreCuenta, String password, String rama, 
			String rut, String nombre, String apellidos, String pidm, String nivel, String estadoAcademico) {
		return new Usuario(nombreCuenta, password, rama, rut, nombre, apellidos, null, null, null, pidm, nivel, 
				estadoAcademico);
	}

	@JsonIgnore
	public static Usuario createUsuario4validar (String nombreCuenta) {
		return new Usuario(nombreCuenta, null, null, null, null, null, null, null, null, null, 
				null, null);
	}

	//=======================================================================================================
	// Getters y Setters
	//=======================================================================================================
	
	public String getCuenta() {
		return cuenta;
	}

	public String getPassword() {
		return password;
	}

	public String getRama() {
		return rama;
	}

	public String getRut() {
		return rut;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getCorreo() {
		return correo;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getComuna() {
		return comuna;
	}

	public String getPidm() {
		return pidm;
	}

	public String getNivel() {
		return nivel;
	}

	public String getEstadoAcademico() {
		return estadoAcademico;
	}

}
