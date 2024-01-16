package cl.uandes.panel.comunes.json.serviciosAD;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CuentasADRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3273592524868499843L;
	@JsonProperty("NOMBRES")
	private String nombres;
	@JsonProperty("NOMBRE")
	private String nombre;
	@JsonProperty("MIDDLE_NAME")
	private String middleName;
	@JsonProperty("APELLIDOS")
	private String apellidos;
	@JsonProperty("RUT")
	private String rut;
	@JsonProperty("PASSWORD")
	private String password;
	@JsonProperty("EMAIL")
	private String email;
	@JsonProperty("DIRECCION")
	private String direccion;
	@JsonProperty("COMUNA")
	private String comuna;
	@JsonProperty("RAMA")
	private String rama;
	
	public CuentasADRequest(
			@JsonProperty("NOMBRES")String nombres, 
			@JsonProperty("NOMBRE")String nombre, 
			@JsonProperty("MIDDLE_NAME")String middleName, 
			@JsonProperty("APELLIDOS")String apellidos, 
			@JsonProperty("RUT")String rut,
			@JsonProperty("PASSWORD")String password, 
			@JsonProperty("EMAIL")String email, 
			@JsonProperty("DIRECCION")String direccion, 
			@JsonProperty("COMUNA")String comuna, 
			@JsonProperty("RAMA")String rama) {
		super();
		this.nombres = nombres;
		this.nombre = nombre;
		this.middleName = middleName;
		this.apellidos = apellidos;
		this.rut = rut;
		this.password = password;
		this.email = email;
		this.direccion = direccion;
		this.comuna = comuna;
		this.rama = rama;
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

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getComuna() {
		return comuna;
	}

	public void setComuna(String comuna) {
		this.comuna = comuna;
	}

	public String getRama() {
		return rama;
	}

	public void setRama(String rama) {
		this.rama = rama;
	}
	
}
