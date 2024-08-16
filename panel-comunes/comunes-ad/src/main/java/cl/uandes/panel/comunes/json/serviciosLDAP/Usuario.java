package cl.uandes.panel.comunes.json.serviciosLDAP;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

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
/*
	@JsonProperty("telefono")
	String telefono;
	@JsonProperty("cargo")
	String cargo;
	@JsonProperty("departamento")
	String departamento;
	@JsonProperty("jefatura")
	String jefatura;
	@JsonProperty("compania")
	String compania;
	
 */
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
			@JsonProperty("estado-academico")String estadoAcademico
			/*,@JsonProperty("telefono")String telefono, 
			@JsonProperty("cargo")String cargo, 
			@JsonProperty("departamento")String departamento, 
			@JsonProperty("jefatura")String jefatura,
			@JsonProperty("compania")String compania,
			 */
			) {
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
		/*
		this.telefono = telefono;
		this.cargo = cargo;
		this.departamento = departamento;
		this.jefatura = jefatura;
		this.compania = compania;
		 */
	}

	@JsonIgnore
	public static Usuario createUsuario4validar (String nombreCuenta) {
		return new Usuario(nombreCuenta, null, null, null, null, null, null, null, null, null, 
				null, null);
	}

	@JsonIgnore
	public static Usuario createUsuario4crear (String nombreCuenta, String password, String rama, 
			String rut, String nombre, String apellidos) {
		return new Usuario(nombreCuenta, password, rama, rut, nombre, apellidos, null, null, null, 
				null, null, null);
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}		
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

	public void setPidm(String pidm) {
		this.pidm = pidm;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getEstadoAcademico() {
		return estadoAcademico;
	}

	public void setEstadoAcademico(String estadoAcademico) {
		this.estadoAcademico = estadoAcademico;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRama(String rama) {
		this.rama = rama;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setComuna(String comuna) {
		this.comuna = comuna;
	}

	/*
	public String getTelefono() {
		return telefono;
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
	 */
}
