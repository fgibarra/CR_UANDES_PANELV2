package cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosReactivarBannerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4040115810983848919L;
	@JsonProperty("cuenta")
	private String cuenta;
	@JsonProperty("login_name")
	private String loginName;

	public DatosReactivarBannerDTO(Map<String, Object> mapDatos) {
		super();
		this.cuenta = (String)mapDatos.get("cuenta");
		if (this.cuenta != null) {
			int index = cuenta.indexOf('@');
			if (index >= 0)
				this.loginName = this.cuenta.substring(0, index);
		}
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

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}
