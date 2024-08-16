package cl.uandes.panel.tools.actualizaCuentasAD.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.StringUtilities;

public class CorregirPregradoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037432561690472654L;
	@JsonProperty("PIDM")
	private String pidm;
	@JsonProperty("RUT")
	private String rut;
	@JsonProperty("NOMBRES")
	private String nombres;
	@JsonProperty("APELLIDOS")
	private String apellidos;
	@JsonProperty("LOGIN_NAME")
	private String sammacountName;
	@JsonProperty("NIVEL")
	private String nivel;
	@JsonProperty("ESTADO")
	private String estado;

	public CorregirPregradoDTO(Map<String, Object> map) {
		super();
		this.rut = (String)map.get("RUT");
		this.pidm = StringUtilities.getInstance().toString((BigDecimal)map.get("PIDM"));
		this.sammacountName = (String)map.get("LOGIN_NAME");
		this.nombres = (String)map.get("NOMBRES");
		this.apellidos = (String)map.get("APELLIDOS");
		this.nivel = (String)map.get("NIVEL");
		this.estado = (String)map.get("ESTADO");
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

	public String getEmployeeId() {
		if (rut != null && rut.startsWith("@"))
			return rut.substring(1);
		else
			return rut;
	}

	public String getCorreo() {
		return String.format("%s@miuandes.cl", sammacountName);
	}
	
	public String getPidm() {
		return pidm;
	}

	public void setPidm(String pidm) {
		this.pidm = pidm;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
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

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getSammacountName() {
		return sammacountName;
	}

	public void setSammacountName(String sammacountName) {
		this.sammacountName = sammacountName;
	}

	public String getPassword() {
		if (rut != null && rut.startsWith("@"))
			return rut.substring(1);
		
		return rut;
	}

}
