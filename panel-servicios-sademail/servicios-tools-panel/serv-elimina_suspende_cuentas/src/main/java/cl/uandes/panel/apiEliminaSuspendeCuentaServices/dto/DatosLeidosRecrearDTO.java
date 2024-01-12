package cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DatosLeidosRecrearDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7881332435346563313L;
	@JsonProperty("key")
	Integer key;
	@JsonProperty("cuenta")
	String cuenta;
	@JsonProperty("id_gmail")
	String idGmail;
	@JsonProperty("nombres")
	String nombres;
	@JsonProperty("apellidos")
	String apellidos;
	@JsonProperty("spriden_id")
	String spridenId;

	private Logger logger = Logger.getLogger(getClass());

	public DatosLeidosRecrearDTO(Map<String, Object> mapDatos) {
		try {
			this.key = (Integer.valueOf(((BigDecimal)mapDatos.get("KEY")).intValue()));
			this.cuenta = (String)mapDatos.get("CUENTA");
			this.idGmail = (String)mapDatos.get("ID_GMAIL");
			this.nombres = (String)mapDatos.get("NOMBRES");
			this.apellidos = (String)mapDatos.get("APELLIDOS");
			this.spridenId = (String)mapDatos.get("SPRIDEN_ID");
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			for (String key : mapDatos.keySet()) {
				sb.append(String.format("%s = %s\n", key, mapDatos.get(key)));
			}
			logger.error(String.format("DatosLeidosRecrearDTO: %s\n%s", e.getCause(), sb.toString()));
		}
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	@JsonIgnore
	public String getLoginName() {
		if (this.cuenta != null) {
			int index = cuenta.indexOf('@');
			if (index >= 0)
				return this.cuenta.substring(0, index);
		}
		return cuenta;
	}

	@JsonIgnore
	public String getPassword() {
		if (this.spridenId == null)
			return "00000000";
        if (this.spridenId.startsWith("@"))
        	this.spridenId = "00000000";
        
        return this.spridenId.substring(0, 8);
	}
	
	public String getIdGmail() {
		return idGmail;
	}

	public void setIdGmail(String idGmail) {
		this.idGmail = idGmail;
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

	public String getSpridenId() {
		return spridenId;
	}

	public void setSpridenId(String spridenId) {
		this.spridenId = spridenId;
	}
}
