package cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosLeidosBannerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8482472246465069881L;
	@JsonProperty("key")
	Integer key;
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
	@JsonProperty("id_gmail")
	String idGmail;
	@JsonProperty("last_login")
	String lastLogin;

	private Logger logger = Logger.getLogger(getClass());
	
	public DatosLeidosBannerDTO(Map<String, Object> mapDatos, Integer maxUsado) {
		super();
		try {
			this.loginName = (String)mapDatos.get("CUENTA");
			if (this.loginName != null) {
				int index = loginName.indexOf('@');
				if (index >= 0)
					this.loginName = this.loginName.substring(0, index);
			}
			this.key = (Integer.valueOf(((BigDecimal)mapDatos.get("KEY")).intValue()));
			this.lastLogin = (String)mapDatos.get("LAST_LOGIN");
			this.spridenId = (String)mapDatos.get("SPRIDEN_ID");
			this.idGmail = (String)mapDatos.get("ID_GMAIL");
			if (mapDatos.get("USADO") != null)
				this.usado = Integer.valueOf(((BigDecimal)mapDatos.get("USADO")).intValue());
			String estado = (String)mapDatos.get("ESTADO_ACADEMICO");
			if (estado != null) {
				if (esElimina(estado))
					this.funcion = "elimina";
				else if (!esIntocable(estado)) {
					if (esSuspende())
						this.funcion = "suspende";
					else if (esSuspende1(estado))
						this.funcion = "suspende";
					else if (esSuspende2(getUsado()))
						this.funcion = "suspende";	
				}
				else
					this.funcion = "ignore";
			} else
				this.funcion = "ignore";
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			for (String key : mapDatos.keySet()) {
				sb.append(String.format("%s = %s\n", key, mapDatos.get(key)));
			}
			logger.error(String.format("DatosLeidosBannerDTO: %s\n%s", e.getCause(), sb.toString()), e);
		}
	}
	
	private boolean esElimina(String estado) {
		String [] estadosElimina = {"ABANDONO","ELIMINADO","DESISTIDO","FALLECIDO","RETIRADO"};
		for (String es : estadosElimina)
			if (estado.equals(es)) {
				if (getLastLogin().toLowerCase().indexOf("never") >= 0)
					return true;
			}
		return false;
	}

	private boolean esIntocable(String estado) {
		String [] estadosElimina = {"PROFESOR","NO EXISTE EN BANNER"};
		for (String es : estadosElimina)
			if (estado.equals(es))
				return true;
		return false;
	}

	private boolean esSuspende() {
		return true;
	}
	
	@SuppressWarnings("unused")
	private boolean esSuspende1(String estado) {
		String [] estadosElimina = {"ABANDONO","ELIMINADO","DESISTIDO","FALLECIDO","RETIRADO"};
		for (String es : estadosElimina)
			if (estado.equals(es))
				return true;
		return false;
	}

	@SuppressWarnings("unused")
	private boolean esSuspende2(Integer maxUsado) {
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

	public Integer getKey() {
		return key;
	}

	public String getIdGmail() {
		return idGmail;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

}
