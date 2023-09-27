package cl.uandes.panel.comunes.json.sincronizaGrupos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SincronizaGruposResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -3366379989789487090L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;

	@JsonCreator
	public SincronizaGruposResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje
			) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
