package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.ObjectFactory;

/**
 * Datos de la tabla MI_CUENTAS_AZURE
 * @author fernando
 *
 */
public class CuentasMiUandes implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3341398292071002137L;
	@JsonProperty("KEY")
	private Integer key;
	@JsonProperty("BANNER_PIDM")
	private Integer pidm;
	@JsonProperty("MOODLE_ID")
	private String moodleId;
	@JsonProperty("LOGIN_NAME")
	private String loginName;
	@JsonProperty("NOMBRES")
	private String nombres;
	@JsonProperty("APELLIDOS")
	private String apellidos;
	
	public CuentasMiUandes(Map<String, Object> datos) {
		this.key = ObjectFactory.toInteger((BigDecimal)datos.get("KEY"));
		this.pidm = ObjectFactory.toInteger((BigDecimal)datos.get("BANNER_PIDM"));
		this.moodleId = (String)datos.get("MOODLE_ID");
		this.loginName = (String)datos.get("LOGIN_NAME");
		this.nombres = (String)datos.get("NOMBRES");
		this.apellidos = (String)datos.get("APELLIDOS");
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

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public Integer getPidm() {
		return pidm;
	}

	public void setPidm(Integer pidm) {
		this.pidm = pidm;
	}

	public String getMoodleId() {
		return moodleId;
	}

	public void setMoodleId(String moodleId) {
		this.moodleId = moodleId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

}
