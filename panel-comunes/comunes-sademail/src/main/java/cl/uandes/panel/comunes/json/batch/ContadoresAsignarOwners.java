package cl.uandes.panel.comunes.json.batch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContadoresAsignarOwners implements Contadores {

	/**
	 * 
	 */
	private static final long serialVersionUID = -939527098090369498L;
	@JsonProperty("grupos-leidos")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("owners-agregados-bd")
	private Integer countAgregadosBD;
	@JsonProperty("owners-agregadas-gmail")
	private Integer countAgregadosGMAIL;
	@JsonProperty("owners-sacados-bd")
	private Integer countSacadosBD;
	@JsonProperty("owners-sacados-gmail")
	private Integer countSacadosGMAIL;

	
	public ContadoresAsignarOwners(
			@JsonProperty("grupos-leidos")Integer countProcesados, 
			@JsonProperty("errores")Integer countErrores, 
			@JsonProperty("owners-agregados-bd")Integer countAgregadosBD,
			@JsonProperty("owners-agregadas-gmail")Integer countAgregadosGMAIL, 
			@JsonProperty("owners-sacados-bd")Integer countSacadosBD, 
			@JsonProperty("owners-sacados-gmail")Integer countSacadosGMAIL) {
		super();
		this.countProcesados = countProcesados;
		this.countErrores = countErrores;
		this.countAgregadosBD = countAgregadosBD;
		this.countAgregadosGMAIL = countAgregadosGMAIL;
		this.countSacadosBD = countSacadosBD;
		this.countSacadosGMAIL = countSacadosGMAIL;
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

	public synchronized void incCountProcesados() {
		countProcesados++;
	}

	public synchronized void incCountErrores() {
		countErrores++;
	}

	public synchronized void incCountAgregadosBD() {
		countAgregadosBD++;
	}

	public synchronized void incCountAgregadosGMAIL() {
		countAgregadosGMAIL++;
	}

	public synchronized void incCountSacadosBD() {
		countSacadosBD++;
	}

	public synchronized void incCountSacadosGMAIL() {
		countSacadosGMAIL++;
	}

	@Override
	public Integer getCountProcesados() {
		return countProcesados;
	}

	@Override
	public Integer getCountErrores() {
		return countErrores;
	}

	@Override
	public Integer getCount1() {
		return countAgregadosBD;
	}

	@Override
	public Integer getCount2() {
		return countAgregadosGMAIL;
	}

	public synchronized Integer getCountAgregadosBD() {
		return countAgregadosBD;
	}

	public synchronized Integer getCountAgregadosGMAIL() {
		return countAgregadosGMAIL;
	}

	public synchronized Integer getCountSacadosBD() {
		return countSacadosBD;
	}

	public synchronized Integer getCountSacadosGMAIL() {
		return countSacadosGMAIL;
	}

}
