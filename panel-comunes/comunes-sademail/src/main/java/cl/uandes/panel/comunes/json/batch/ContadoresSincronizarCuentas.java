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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContadoresSincronizarCuentas implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2306313006805693246L;
	@JsonProperty("proceso")
	private String proceso;
	@JsonProperty("cuentas-leidos")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("cuentas-registradas")
	private Integer countRegistrados;
	@JsonProperty("cuentas-no-registradas")
	private Integer countNoRegistrados;

	public ContadoresSincronizarCuentas(String proceso) {
		super();
		this.proceso = proceso;
		this.countProcesados = 0;
		this.countErrores = 0;
		this.countRegistrados = 0;
		this.countNoRegistrados = 0;
	}

	@JsonCreator
	public ContadoresSincronizarCuentas(
			@JsonProperty("proceso")String proceso, 
			@JsonProperty("cuentas-leidos")Integer countProcesados, 
			@JsonProperty("errores")Integer countErrores,
			@JsonProperty("cuentas-registradas")Integer countRegistrados, 
			@JsonProperty("cuentas-no-registradas")Integer countNoRegistrados) {
		super();
		this.proceso = proceso;
		this.countProcesados = countProcesados;
		this.countErrores = countErrores;
		this.countRegistrados = countRegistrados;
		this.countNoRegistrados = countNoRegistrados;
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

	public synchronized void incProcesados() {
		countProcesados++;
	}

	public synchronized void incErrores() {
		countErrores++;
	}

	public synchronized void incRegistrados() {
		countRegistrados++;
	}

	public synchronized void incNoRegistrados() {
		countNoRegistrados++;
	}

	public synchronized String getProceso() {
		return proceso;
	}

	public synchronized Integer getCountProcesados() {
		return countProcesados;
	}

	public synchronized Integer getCountErrores() {
		return countErrores;
	}

	public synchronized Integer getCountRegistrados() {
		return countRegistrados;
	}

	public synchronized Integer getCountNoRegistrados() {
		return countNoRegistrados;
	}

}
