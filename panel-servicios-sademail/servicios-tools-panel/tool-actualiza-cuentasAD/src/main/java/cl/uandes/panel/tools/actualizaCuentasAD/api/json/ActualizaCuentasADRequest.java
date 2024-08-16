package cl.uandes.panel.tools.actualizaCuentasAD.api.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActualizaCuentasADRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2617833104842000010L;
	@JsonProperty("operacion")
	private String operacion;
	@JsonProperty("soloDebug")
	private Boolean soloDebug;
	@JsonProperty("datos-servicio")
	private OperacionXFecha datos;

	@JsonCreator
	public ActualizaCuentasADRequest(
			@JsonProperty("operacion") String operacion,
			@JsonProperty("soloDebug") Boolean soloDebug,
			@JsonProperty("datos-servicio") OperacionXFecha datos) {
		super();
		this.operacion = operacion;
		this.soloDebug = soloDebug;
		this.datos = datos;
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

	public String getOperacion() {
		return operacion;
	}

	public OperacionXFecha getDatos() {
		return datos;
	}

	public Boolean getSoloDebug() {
		return soloDebug;
	}

	public void setSoloDebug(Boolean soloDebug) {
		this.soloDebug = soloDebug;
	}

}
