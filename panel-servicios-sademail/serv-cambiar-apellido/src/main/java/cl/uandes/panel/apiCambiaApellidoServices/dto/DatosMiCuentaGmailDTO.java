package cl.uandes.panel.apiCambiaApellidoServices.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosMiCuentaGmailDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -423815667576155887L;
	@JsonProperty("key")
	private Long key;
	@JsonProperty("moodle_id")
	private String moodleId;
	@JsonProperty("banner_pidm")
	private Long bannerPidm;
	@JsonProperty("login_name")
	private String loginName;
	@JsonProperty("nombres")
	private String nombres;
	@JsonProperty("apellidos")
	private String apellidos;
	@JsonProperty("id_gmail")
	private String idGmail;
	@JsonProperty("rowid")
	private String rowid;
	@JsonProperty("goremal_email_address")
	private String goremailEmail;

	public DatosMiCuentaGmailDTO(Map<String, Object> mapDatos) {
		super();
		this.key = ((BigDecimal)mapDatos.get("key")).longValue();
		this.moodleId = (String)mapDatos.get("moodle_id");
		this.bannerPidm = ((BigDecimal)mapDatos.get("banner_pidm")).longValue();
		this.loginName = (String)mapDatos.get("login_name");
		this.nombres = (String)mapDatos.get("nombres");
		this.apellidos = (String)mapDatos.get("apellidos");
		this.idGmail = (String)mapDatos.get("id_gmail");
		this.rowid = (String)mapDatos.get("rowid");
		this.goremailEmail = (String)mapDatos.get("goremal_email_address");
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

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getMoodleId() {
		return moodleId;
	}

	public void setMoodleId(String moodleId) {
		this.moodleId = moodleId;
	}

	public Long getBannerPidm() {
		return bannerPidm;
	}

	public void setBannerPidm(Long bannerPidm) {
		this.bannerPidm = bannerPidm;
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

	public String getIdGmail() {
		return idGmail;
	}

	public void setIdGmail(String idGmail) {
		this.idGmail = idGmail;
	}

	public String getRowid() {
		return rowid;
	}

	public void setRowid(String rowid) {
		this.rowid = rowid;
	}

	public String getGoremailEmail() {
		return goremailEmail;
	}

	public void setGoremailEmail(String goremailEmail) {
		this.goremailEmail = goremailEmail;
	}

}
