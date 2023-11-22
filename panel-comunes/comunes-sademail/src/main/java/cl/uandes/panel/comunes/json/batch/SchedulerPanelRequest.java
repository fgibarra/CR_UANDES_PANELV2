package cl.uandes.panel.comunes.json.batch;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Informa al Scheduler que operacion termino y la key donde quedo el resultado en la tabla MI_RESULTADOS
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchedulerPanelRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 5801381093579327886L;
	@JsonProperty("operacion")
	private String operacion;
	@JsonProperty("keyResultado")
	private Integer keyResultado;

	@JsonCreator
	public SchedulerPanelRequest(@JsonProperty("operacion") String operacion,
			@JsonProperty("keyResultado") Integer keyResultado) {
		super();
		this.operacion = operacion;
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
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public Integer getKeyResultado() {
		return keyResultado;
	}

	public void setKeyResultado(Integer keyResultado) {
		this.keyResultado = keyResultado;
	}

}
