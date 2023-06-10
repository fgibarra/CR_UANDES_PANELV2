package cl.uandes.panel.apiCrearCuentaServices.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;

public class DatosMiCuentasGmailDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 6988034339972432274L;
	@JsonProperty("key")
	Integer key;
	@JsonProperty("MOODLE_ID")
	private String moodleId;
	@JsonProperty("BANNER_PIDM")
	private Integer bannerPidm;
	@JsonProperty("LOGIN_NAME")
	private String loginName;
	@JsonProperty("NOMBRES")
	private String nombres;
	@JsonProperty("APELLIDOS")
	private String apellidos;
	@JsonProperty("PASSWORD")
	private String password;
	@JsonProperty("ID_GMAIL")
	private String idGmail;

	public DatosMiCuentasGmailDTO(DatosSpridenDTO dto, String loginName) {
		super();
		setLoginName(loginName);
		setMoodleId(dto.getSpridenId());
		setBannerPidm(dto.getSpridenPidm());
		setNombres(dto.getNombres());
		setApellidos(dto.getLastName());
		if (dto.getSpridenId().startsWith("@")) {
			this.password = "00000000";
		} else {
			if (dto.getSpridenId().length() >= 8)
				this.password = dto.getSpridenId().substring(0, 8);
			else
				this.password = dto.getSpridenId();
		}
	}

	public DatosMiCuentasGmailDTO(Map<String, Object> datos) {
		super();
		this.key = (Integer.valueOf(((java.math.BigDecimal)datos.get("KEY")).intValue()));
		this.moodleId = (String) datos.get("MOODLE_ID");
		this.bannerPidm = (Integer.valueOf(((java.math.BigDecimal) datos.get("BANNER_PIDM")).intValue()));
		this.loginName = (String) datos.get("LOGIN_NAME");
		this.nombres = (String) datos.get("NOMBRES");
		this.apellidos = (String) datos.get("APELLIDOS");
	}

	public DatosMiCuentasGmailDTO(CreaCuentaRequest request) {
		setLoginName(request.getLoginName());
		setMoodleId(request.getRut());
		setNombres(request.getNombreCuenta());
		setApellidos(request.getApellidos());
		this.password = request.getPassword();
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

	public String getMoodleId() {
		return moodleId;
	}

	public void setMoodleId(String moodle_id) {
		this.moodleId = moodle_id;
	}

	public Integer getBannerPidm() {
		return bannerPidm;
	}

	public void setBannerPidm(Integer bannerPidm) {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getIdGmail() {
		return idGmail;
	}

	public void setIdGmail(String idGmail) {
		this.idGmail = idGmail;
	}

}
