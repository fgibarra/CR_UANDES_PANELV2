package cl.uandes.panel.comunes.json.updateidgmail;

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
public class UpdateIdGmailResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -4569006478583261275L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("cuentas_actualizadas")
	Integer cuentasActualizadas;
	@JsonProperty("hora_comienzo")
	String horaComienzo;
	@JsonProperty("hora_termino")
	String horaTermino;
	@JsonProperty("duracion")
	String duracion;
	
	@JsonCreator
	public UpdateIdGmailResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("cuentas_actualizadas")Integer cuentasActualizadas,
			@JsonProperty("hora_comienzo")String horaComienzo,
			@JsonProperty("hora_termino")String horaTermino, 
			@JsonProperty("duracion")String duracion) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.cuentasActualizadas = cuentasActualizadas;
		this.horaComienzo = horaComienzo;
		this.horaTermino = horaTermino;
		this.duracion = duracion;
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

	public Integer getCuentasActualizadas() {
		return cuentasActualizadas;
	}

	public void setCuentasActualizadas(Integer cuentasActualizadas) {
		this.cuentasActualizadas = cuentasActualizadas;
	}

	public String getHoraComienzo() {
		return horaComienzo;
	}

	public void setHoraComienzo(String horaComienzo) {
		this.horaComienzo = horaComienzo;
	}

	public String getHoraTermino() {
		return horaTermino;
	}

	public void setHoraTermino(String horaTermino) {
		this.horaTermino = horaTermino;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

}
