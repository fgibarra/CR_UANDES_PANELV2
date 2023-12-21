package cl.uandes.panel.servicio.owners.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NapGrupoOwnerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2460262871844426223L;
	@JsonProperty("KEY")
	private Integer key;
	@JsonProperty("GROUP_NAME")
	private String groupName;
	@JsonProperty("OWNER_EMAIL")
	private String ownerEmail;
	@JsonProperty("ACTIVO")
	private Boolean activo;
	@JsonProperty("CREADO_GMAIL")
	private Boolean creadoGmail;
	@JsonProperty("FECHA_VALIDACION")
	private Timestamp fechaValidacion;

	public NapGrupoOwnerDTO(Map<String, Object> datos) {
		super();
		this.key = Integer.valueOf(((BigDecimal)datos.get("KEY")).intValue());
		this.groupName = (String)datos.get("GROUP_NAME");
		this.ownerEmail = (String)datos.get("OWNER_EMAIL");
		Integer valor;
		Object obj = datos.get("ACTIVO");
		if (obj != null) {
			valor = Integer.valueOf(((BigDecimal)datos.get("ACTIVO")).intValue());
			this.activo = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		obj = datos.get("CREADO_GMAIL");
		if (obj != null) {
			valor = Integer.valueOf(((BigDecimal)datos.get("CREADO_GMAIL")).intValue());
			this.creadoGmail = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		this.fechaValidacion = (Timestamp)datos.get("FECHA_VALIDACION");
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

	public synchronized Integer getKey() {
		return key;
	}

	public synchronized void setKey(Integer key) {
		this.key = key;
	}

	public synchronized String getGroupName() {
		return groupName;
	}

	public synchronized void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public synchronized Boolean getActivo() {
		return activo;
	}

	public synchronized void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public synchronized Boolean getCreadoGmail() {
		return creadoGmail;
	}

	public synchronized void setCreadoGmail(Boolean creadoGmail) {
		this.creadoGmail = creadoGmail;
	}

	public synchronized Timestamp getFechaValidacion() {
		return fechaValidacion;
	}

	public synchronized void setFechaValidacion(Timestamp fechaValidacion) {
		this.fechaValidacion = fechaValidacion;
	}

	public synchronized String getOwnerEmail() {
		return ownerEmail;
	}

	public synchronized void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	
}
