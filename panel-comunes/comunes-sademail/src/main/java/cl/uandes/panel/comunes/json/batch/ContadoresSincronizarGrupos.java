package cl.uandes.panel.comunes.json.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContadoresSincronizarGrupos implements Contadores {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4762657042442362654L;
	@JsonProperty("proceso")
	private String proceso;
	@JsonProperty("grupos-leidos")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("miembros-sacados")
	private Integer countSacados;

	@JsonCreator
	public ContadoresSincronizarGrupos(
			@JsonProperty("proceso")String proceso, 
			@JsonProperty("grupos-leidos")Integer countProcesados, 
			@JsonProperty("errores")Integer countErrores,
			@JsonProperty("miembros-sacados")Integer countSacados) {
		super();
		this.proceso = proceso;
		this.countProcesados = countProcesados;
		this.countErrores = countErrores;
		this.countSacados = countSacados;
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

	public synchronized void incSacados() {
		countSacados++;
	}

	public synchronized String getProceso() {
		return proceso;
	}

	public synchronized void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public synchronized Integer getCountProcesados() {
		return countProcesados;
	}

	public synchronized void setCountProcesados(Integer countProcesados) {
		this.countProcesados = countProcesados;
	}

	public synchronized Integer getCountErrores() {
		return countErrores;
	}

	public synchronized void setCountErrores(Integer countErrores) {
		this.countErrores = countErrores;
	}

	public synchronized Integer getCountSacados() {
		return countSacados;
	}

	public synchronized void setCountSacados(Integer countSacados) {
		this.countSacados = countSacados;
	}

	@Override
	public Integer getCount1() {
		return getCountSacados();
	}

	@Override
	public Integer getCount2() {
		return null;
	}
}
