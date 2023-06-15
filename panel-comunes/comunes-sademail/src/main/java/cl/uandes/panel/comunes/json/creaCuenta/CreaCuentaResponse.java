package cl.uandes.panel.comunes.json.creaCuenta;

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
public class CreaCuentaResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 2068113352477940934L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("rut")
	private String rut;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("nombres")
	private String nombreCuenta;
	@JsonProperty("apellidos")
	private String apellidos;
	@JsonProperty("id")
	private String id;
	@JsonProperty("password")
	private String password;
	
	@JsonCreator
	public CreaCuentaResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("rut")String rut,
			@JsonProperty("cuenta")String cuenta,
			@JsonProperty("nombres")String nombreCuenta,
			@JsonProperty("apellidos")String apellidos,
			@JsonProperty("id")String id,
			@JsonProperty("password")String password) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.rut = rut;
		this.cuenta = cuenta;
		this.nombreCuenta = nombreCuenta;
		this.apellidos = apellidos;
		this.id = id;
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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
