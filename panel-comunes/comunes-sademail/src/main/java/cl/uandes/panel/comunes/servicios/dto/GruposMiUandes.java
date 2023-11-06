package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;

/**
 * Datos recuperados de la tabla MI_GRUPOS_AZURE
 * @author fernando
 *
 */
public class GruposMiUandes implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -6277971423044652396L;
	@JsonProperty("KEY")
	private Integer key;
	@JsonProperty("KEY_TIPO")
	private Integer tipoGrupo;
	@JsonProperty("GROUP_ID")
	private String groupId;
	@JsonProperty("GROUP_NAME")
	private String groupName;
	@JsonProperty("GROUP_DESCRIPTION")
	private String groupDescription;
	@JsonProperty("EMAILPERMISSION")
	private String emailPermission;
	@JsonProperty("PERIODO")
	private String periodo;
	@JsonProperty("ORIGEN")
	private String origen;
	@JsonProperty("ACTIVO")
	private Boolean activo;
	@JsonProperty("CREADO_AZURE")
	private Boolean creadoAzure;
	@JsonProperty("FECHA_VALIDACION")
	private Timestamp fechaValidacion;
	@JsonProperty("SELECCION_ESPECIAL")
	private String seleccionEspecial;
	@JsonProperty("FECHA_CREACION")
	private Date fechaCreacion;

	public GruposMiUandes(Map<String, Object> datos) {
		super();
		this.key = Integer.valueOf(((BigDecimal)datos.get("KEY")).intValue());
		this.tipoGrupo = Integer.valueOf(((BigDecimal)datos.get("KEY_TIPO")).intValue());
		this.fechaValidacion = (Timestamp)datos.get("FECHA_VALIDACION");
		this.groupId = (String)datos.get("GROUP_ID");
		this.groupName = (String)datos.get("GROUP_NAME");
		this.groupDescription = (String)datos.get("GROUP_DESCRIPTION");
		this.emailPermission = (String)datos.get("EMAILPERMISSION");
		this.periodo = (String)datos.get("PERIODO");
		this.origen = (String)datos.get("ORIGEN");
		this.seleccionEspecial = (String)datos.get("SELECCION_ESPECIAL");
		this.fechaCreacion =(Date)datos.get("FECHA_CREACION");
		Integer valor = Integer.valueOf(((BigDecimal)datos.get("ACTIVO")).intValue());
		this.activo = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
		valor = Integer.valueOf(((BigDecimal)datos.get("CREADO_AZURE")).intValue());
		this.creadoAzure = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
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

	public Integer getTipoGrupo() {
		return tipoGrupo;
	}

	public void setTipoGrupo(Integer tipoGrupo) {
		this.tipoGrupo = tipoGrupo;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public String getEmailPermission() {
		return emailPermission;
	}

	public void setEmailPermission(String emailPermission) {
		this.emailPermission = emailPermission;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getCreadoAzure() {
		return creadoAzure;
	}

	public void setCreadoAzure(Boolean creadoAzure) {
		this.creadoAzure = creadoAzure;
	}

	public Timestamp getFechaValidacion() {
		return fechaValidacion;
	}

	public void setFechaValidacion(Timestamp fechaValidacion) {
		this.fechaValidacion = fechaValidacion;
	}

	public String getSeleccionEspecial() {
		return seleccionEspecial;
	}

	public void setSeleccionEspecial(String seleccionEspecial) {
		this.seleccionEspecial = seleccionEspecial;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

}
