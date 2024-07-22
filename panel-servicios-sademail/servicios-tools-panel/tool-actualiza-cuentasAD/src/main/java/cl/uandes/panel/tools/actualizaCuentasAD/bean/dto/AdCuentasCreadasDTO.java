package cl.uandes.panel.tools.actualizaCuentasAD.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.StringUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdCuentasCreadasDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6012482859114557266L;
	@JsonProperty("RUT")
	private String rut;
	@JsonProperty("PIDM")
	private String pidm;
	@JsonProperty("SAMACCOUNT_NAME")
	private String sammacountName;
	@JsonProperty("NIVEL")
	private String nivel;
	@JsonProperty("ESTADO")
	private String estado;
	
	public AdCuentasCreadasDTO(Map<String, Object> map) {
		this.rut = (String)map.get("RUT");
		this.pidm = StringUtilities.getInstance().toString((BigDecimal)map.get("PIDM"));
		this.sammacountName = (String)map.get("SAMACCOUNT_NAME");
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

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getPidm() {
		return pidm;
	}

	public void setPidm(String pidm) {
		this.pidm = pidm;
	}

	public String getSammacountName() {
		return sammacountName;
	}

	public void setSammacountName(String sammacountName) {
		this.sammacountName = sammacountName;
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

	public String getPassword() {
		if (rut != null && rut.startsWith("@"))
			return rut.substring(1);
		
		return rut;
	}
}
