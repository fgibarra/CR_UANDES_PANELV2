package cl.uandes.panel.comunes.json.cambiacuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EliminaSuspendeCuentaResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -6135556011247622312L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("numero_eliminados")
	Integer countEliminados;
	@JsonProperty("numero_suspendidos")
	Integer countSuspendidos;

	public EliminaSuspendeCuentaResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("numero_eliminados")Integer countEliminados,
			@JsonProperty("numero_suspendidos")Integer countSuspendidos) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.countEliminados = countEliminados;
		this.countSuspendidos = countSuspendidos;
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

	public Integer getCountEliminados() {
		return countEliminados;
	}

	public void setCountEliminados(Integer countEliminados) {
		this.countEliminados = countEliminados;
	}

	public Integer getCountSuspendidos() {
		return countSuspendidos;
	}

	public void setCountSuspendidos(Integer countSuspendidos) {
		this.countSuspendidos = countSuspendidos;
	}

}
