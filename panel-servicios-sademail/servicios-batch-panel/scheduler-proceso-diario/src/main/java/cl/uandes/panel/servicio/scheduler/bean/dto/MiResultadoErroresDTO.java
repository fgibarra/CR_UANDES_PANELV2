package cl.uandes.panel.servicio.scheduler.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.ObjectFactory;

public class MiResultadoErroresDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 4883978306236093734L;
	@JsonProperty("ID_USUARIO")
	private String idUsuario;
	@JsonProperty("TIPO")
	private String tipo;
	@JsonProperty("CAUSA")
	private String causa;
	@JsonProperty("KEY_GRUPO")
	private Integer keyGrupo;
	
	public MiResultadoErroresDTO(Map<String, Object> datos) {
		this.idUsuario = (String)datos.get("ID_USUARIO");
		this.tipo = (String)datos.get("TIPO");
		this.causa = (String)datos.get("CAUSA");
		this.keyGrupo = ObjectFactory.toInteger((BigDecimal)datos.get("KEY_GRUPO"));
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}		
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public String getTipo() {
		return tipo;
	}

	public String getCausa() {
		return causa;
	}

	public Integer getKeyGrupo() {
		return keyGrupo;
	}

}
