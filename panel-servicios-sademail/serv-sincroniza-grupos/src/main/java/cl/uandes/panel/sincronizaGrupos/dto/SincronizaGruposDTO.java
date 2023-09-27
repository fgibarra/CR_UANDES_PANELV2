package cl.uandes.panel.sincronizaGrupos.dto;

import java.io.Serializable;
import java.util.Map;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SincronizaGruposDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3718374167429259205L;
	@JsonProperty("key")
	Integer key;
	@JsonProperty("group_name")
	String groupName;
	@JsonProperty("login_name")
	String loginName;
	@JsonProperty("actualizado")
	Boolean actualizado;

	public SincronizaGruposDTO(Map<String, Object> mapDatos) {
		super();
		this.key = (Integer.valueOf(((BigDecimal)mapDatos.get("KEY")).intValue()));
		this.groupName = (String)mapDatos.get("GROUP_NAME");
		this.loginName = (String)mapDatos.get("LOGIN_NAME");
		this.actualizado = Boolean.FALSE;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Boolean getActualizado() {
		return actualizado;
	}

	public void setActualizado(Boolean actualizado) {
		this.actualizado = actualizado;
	}

}
