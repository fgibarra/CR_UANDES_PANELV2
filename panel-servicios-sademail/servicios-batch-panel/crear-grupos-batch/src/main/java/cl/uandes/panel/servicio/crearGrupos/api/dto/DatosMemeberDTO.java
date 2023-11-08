package cl.uandes.panel.servicio.crearGrupos.api.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberRequest;
import cl.uandes.panel.comunes.servicios.dto.GrupoMiembro;

/**
 * Datos que se transportan dentro del Message entre distintas rutas del servicio.
 * @author fernando
 *
 */
public class DatosMemeberDTO implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 3110532803159679667L;
	@JsonProperty("request")
	private MemberRequest request;
	@JsonProperty("keyGrupoMiembro")
	private GrupoMiembro keyGrupoMiembro;
	@JsonProperty("loginName")
	private String loginName;
	@JsonProperty("msgError")
	private String msgError;
	
	public DatosMemeberDTO(Map<String, Object> datos) {
		super();
		String groupId = (String) datos.get("GROUP_ID");
		String userId = (String) datos.get("ID_AZURE");
		this.request = new MemberRequest(groupId, userId);
		this.keyGrupoMiembro = new GrupoMiembro(datos);
		this.loginName = (String)datos.get("LOGIN_NAME");
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

	public MemberRequest getRequest() {
		return request;
	}

	public void setRequest(MemberRequest request) {
		this.request = request;
	}

	public GrupoMiembro getKeyGrupoMiembro() {
		return keyGrupoMiembro;
	}

	public void setKeyGrupoMiembro(GrupoMiembro keyGrupoMiembro) {
		this.keyGrupoMiembro = keyGrupoMiembro;
	}

	public boolean hayIdGmail() {
		return request.getUserId() != null;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

}
