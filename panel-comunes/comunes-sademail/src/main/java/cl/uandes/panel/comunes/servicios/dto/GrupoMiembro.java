package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Datos recuperados desde la tabla NAP_GRUPO_MIEMBRO_AZURE
 * @author fernando
 *
 */
public class GrupoMiembro implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1779965497726130865L;
	@JsonProperty("KEY_GRUPO")
	Integer keyGrupo;
	@JsonProperty("ID_MIEMBRO")
	String idMiembro;
	@JsonProperty("ACTIVO")
	Boolean activo;
	@JsonProperty("CREADO_AZURE")
	Boolean creadoAzure;
	@JsonProperty("FECHA_VALIDACION")
	Timestamp fechaValidacion;

	public GrupoMiembro(Map<String, Object> datos) {
		super();
		this.keyGrupo = Integer.valueOf(((BigDecimal)datos.get("KEY_GRUPO")).intValue());
		this.idMiembro = (String)datos.get("ID_MIEMBRO");
		if (datos.get("ACTIVO") != null) {
			Integer valor = Integer.valueOf(((BigDecimal)datos.get("ACTIVO")).intValue());
			this.activo = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		if (datos.get("CREADO_AZURE") != null) {
			Integer valor = Integer.valueOf(((BigDecimal)datos.get("CREADO_AZURE")).intValue());
			this.creadoAzure = valor == 1 ? Boolean.TRUE : Boolean.FALSE;
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

	public Integer getKeyGrupo() {
		return keyGrupo;
	}

	public void setKeyGrupo(Integer keyGrupo) {
		this.keyGrupo = keyGrupo;
	}

	public String getIdMiembro() {
		return idMiembro;
	}

	public void setIdMiembro(String idMiembro) {
		this.idMiembro = idMiembro;
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

}
