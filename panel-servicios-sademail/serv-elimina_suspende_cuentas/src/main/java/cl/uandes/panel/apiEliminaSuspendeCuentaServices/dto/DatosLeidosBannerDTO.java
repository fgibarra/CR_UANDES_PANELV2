package cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosLeidosBannerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8482472246465069881L;
	@JsonProperty("login_name")
	String loginName;
	@JsonProperty("spridenId")
	String spridenId;
	@JsonProperty("spridenPidm")
	Integer spridenPidm;
	@JsonProperty("usado")
	Integer usado;
	@JsonProperty("funcion")
	String funcion;

	public DatosLeidosBannerDTO(Map<String, Object> mapDatos, Integer maxUsado) {
		super();
		this.loginName = (String)mapDatos.get("cuenta");
		if (this.loginName != null) {
			int index = loginName.indexOf('@');
			if (index >= 0)
				this.loginName = this.loginName.substring(0, index);
		}
		this.spridenId = (String)mapDatos.get("spriden_id");
		this.usado = Integer.valueOf(((BigDecimal)mapDatos.get("usado")).intValue());
		String estado = (String)mapDatos.get("estado");
		if (estado != null) {
			if (esElimina(estado))
				this.funcion = "elimina";
			else if (esSuspende(maxUsado))
				this.funcion = "suspende";
			else
				this.funcion = null;
		} else
			this.funcion = null;
	}
	
	private boolean esElimina(String estado) {
		String [] estadosElimina = {"ABANDONO","ELIMINADO","DESISTIDO","FALLECIDO","RETIRADO"};
		for (String es : estadosElimina)
			if (estado.equals(es))
				return true;
		return false;
	}

	private boolean esSuspende(Integer maxUsado) {
		if (getUsado() > maxUsado)
			return true;
		return false;
	}

	@JsonIgnore
	public boolean esProcesable() {
		return this.funcion != null;
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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getSpridenId() {
		return spridenId;
	}

	public void setSpridenId(String spridenId) {
		this.spridenId = spridenId;
	}

	public Integer getSpridenPidm() {
		return spridenPidm;
	}

	public void setSpridenPidm(Integer spridenPidm) {
		this.spridenPidm = spridenPidm;
	}

	public Integer getUsado() {
		return usado;
	}

	public void setUsado(Integer usado) {
		this.usado = usado;
	}

	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

}
