package cl.uandes.panel.comunes.json.cambiacuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EliminaSuspendeCuentaRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 2122324180953791416L;
	@JsonProperty("funcion")
	private String funcion;
	@JsonProperty("uso")
	private Integer uso;
	@JsonProperty("ruts")
	private String[] listaRuts;
	@JsonProperty("correo-informe")
	private String correoInforme;

	@JsonCreator
	public EliminaSuspendeCuentaRequest(
			@JsonProperty("funcion")String funcion, 
			@JsonProperty("uso")Integer uso,
			@JsonProperty("ruts")String[] listaRuts,
			@JsonProperty("correo-informe")String correoInforme) {
		super();
		this.funcion = funcion;
		this.uso = uso;
		this.listaRuts = listaRuts;
		this.correoInforme = correoInforme;
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

	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public Integer getUso() {
		return uso;
	}

	public void setUso(Integer uso) {
		this.uso = uso;
	}

	public String[] getListaRuts() {
		return listaRuts;
	}

	public void setListaRuts(String[] listaRuts) {
		this.listaRuts = listaRuts;
	}

	public String getCorreoInforme() {
		return correoInforme;
	}

	public void setCorreoInforme(String correoInforme) {
		this.correoInforme = correoInforme;
	}

}
