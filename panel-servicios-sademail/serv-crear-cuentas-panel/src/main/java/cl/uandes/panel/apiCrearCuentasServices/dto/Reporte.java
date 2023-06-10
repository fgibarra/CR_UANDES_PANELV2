package cl.uandes.panel.apiCrearCuentasServices.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reporte implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 7519263235529400212L;
	@JsonProperty("rut")
	private String rut;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("nombres")
	private String nombres;
	@JsonProperty("apellidos")
	private String apellidos;
	@JsonProperty("idGmail")
	private String idGmail;
	@JsonProperty("password")
	private String password;
	@JsonProperty("mensaje")
	private String mensaje;

	public Reporte(CreaCuentaResponse response) {
		super();
		this.rut = StringUtils.formateaRut(response.getRut());
		this.cuenta = response.getCuenta();
		this.nombres = response.getNombreCuenta();
		this.apellidos = response.getApellidos();
		this.idGmail = response.getId();
		this.password = response.getPassword();
		this.mensaje = generaMensaje(response.getMensaje());
	}

	private String generaMensaje(String mensaje2) {
		if (mensaje2 != null) {
			if ("OK".equalsIgnoreCase(mensaje2))
				return "Creada";
			else
				return String.format("No creada: %s", mensaje2);
		}
		return null;
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

	public void setRut(String rut) {
		this.rut = rut;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
