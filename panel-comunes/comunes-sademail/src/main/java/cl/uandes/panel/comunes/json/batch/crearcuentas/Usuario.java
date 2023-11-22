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
	@JsonProperty("telefono")
	String telefono;
	@JsonProperty("direccion")
	String direccion;
	@JsonProperty("comuna")
	String comuna;
	@JsonProperty("cargo")
	String cargo;
	@JsonProperty("departamento")
	String departamento;
	@JsonProperty("jefatura")
	String jefatura;
	@JsonProperty("compania")
	String compania;

	@JsonCreator
	public Usuario(@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("password")String password, 
			@JsonProperty("rama")String rama, 
			@JsonProperty("rut")String rut, 
			@JsonProperty("nombre")String nombre, 
			@JsonProperty("apellidos")String apellidos,
			@JsonProperty("correo")String correo, 
			@JsonProperty("telefono")String telefono, 
			@JsonProperty("direccion")String direccion, 
			@JsonProperty("comuna")String comuna, 
			@JsonProperty("cargo")String cargo, 
			@JsonProperty("departamento")String departamento, 
			@JsonProperty("jefatura")String jefatura,
			@JsonProperty("compania")String compania) {
		super();
		this.cuenta = cuenta;
		this.password = password;
		this.rama = rama;
		this.rut = rut;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.correo = correo;
		this.telefono = telefono;
		this.direccion = direccion;
		this.comuna = comuna;
		this.cargo = cargo;
		this.departamento = departamento;
		this.jefatura = jefatura;
		this.compania = compania;
	}

	@JsonIgnore
	public static Usuario createUsuario4validar (String nombreCuenta) {
		return new Usuario(nombreCuenta, null, null, null, null, null, null, null, null, null, 
				null, null, null, null);
	}

	@JsonIgnore
	public static Usuario createUsuario4crear (String nombreCuenta, String password, String rama, 
			String rut, String nombre, String apellidos) {
		return new Usuario(nombreCuenta, password, rama, rut, nombre, apellidos, null, null, null, 
				null, null, null, null, null);
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

	public String getTelefono() {
		return telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getComuna() {
		return comuna;
	}

	public String getCargo() {
		return cargo;
	}

	public String getDepartamento() {
		return departamento;
	}

	public String getJefatura() {
		return jefatura;
	}

	public String getCompania() {
		return compania;
	}

}
