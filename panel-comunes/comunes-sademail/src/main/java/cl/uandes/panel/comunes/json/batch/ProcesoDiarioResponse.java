package cl.uandes.panel.comunes.json.batch;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Response con el resultado del proceso diario
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcesoDiarioResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1558079255504352378L;
	@JsonProperty("codigo")
	private Integer codigo;
	@JsonProperty("proceso")
	private String proceso;
	@JsonProperty("mensaje")
	private String mensaje;
	@JsonProperty("keyResultado")
	private Integer keyResultado;

	@JsonCreator
	public ProcesoDiarioResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("proceso")String proceso,
		@JsonProperty("mensaje")String mensaje,
		@JsonProperty("keyResultado")Integer keyResultado) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.proceso = proceso;
		this.keyResultado = keyResultado;
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

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public Integer getKeyResultado() {
		return keyResultado;
	}

	public void setKeyResultado(Integer keyResultado) {
		this.keyResultado = keyResultado;
	}

}
