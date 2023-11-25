package cl.uandes.panel.servicio.scheduler.bean.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.StringUtilities;

public class MiResultadosDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1070662266935366152L;
	@JsonProperty("HORA_COMIENZO")
	private String horaComienzo;
	@JsonProperty("HORA_TERMINO")
	private String horaTermino;
	@JsonProperty("COMENTARIO")
	private String comentario;
	@JsonProperty("json-contadores")
	private String jsonContadores;

	public MiResultadosDTO(Map<String, Object> datos) {
		java.sql.Timestamp date = (java.sql.Timestamp)datos.get("HORA_COMIENZO");
		this.horaComienzo = StringUtilities.getInstance().toString(date);
		date = (java.sql.Timestamp)datos.get("HORA_TERMINO");
		this.horaTermino = StringUtilities.getInstance().toString(date);
		String valor = (String)datos.get("COMENTARIO");
		if (valor.startsWith("OK")) {
			int index = valor.indexOf(':');
			if (index >= 0) {
				this.comentario = valor.substring(0, index);
				this.jsonContadores = valor.substring(index+1);
			} else {
				this.comentario = valor;
			}
		} else {
			this.jsonContadores = valor;
		}
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

	public String getHoraComienzo() {
		return horaComienzo;
	}

	public String getHoraTermino() {
		return horaTermino;
	}

	public String getComentario() {
		return comentario;
	}

	public String getJsonContadores() {
		return jsonContadores;
	}

}
