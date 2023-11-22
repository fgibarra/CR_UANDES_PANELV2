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
 * Request para el Servicio Proceso Diario
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcesoDiarioRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -2081791766388162923L;
	@JsonProperty("funcion")
	private String funcion;
	@JsonProperty("operaciones")
	private String[] operaciones;

	@JsonCreator
	public ProcesoDiarioRequest(
			@JsonProperty("funcion")String funcion, 
			@JsonProperty("operaciones")String[] operaciones) {
		super();
		this.funcion = funcion;
		this.operaciones = operaciones;
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
	
	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public String[] getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(String[] operaciones) {
		this.operaciones = operaciones;
	}

}
