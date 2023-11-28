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
 * Usado para colocar en el comentario de la tabla MI_RESULTADO
 * 
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContadoresCrearGrupos implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -4745836312222919119L;
	@JsonProperty("grupos-leidos")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("grupos-agregados-bd")
	private Integer countGruposAgregadosBD;
	@JsonProperty("grupos-agregados-ad")
	private Integer countGruposAgregadosAD;
	@JsonProperty("grupos-sacados-bd")
	private Integer countGruposSacadosBD;
	@JsonProperty("grupos-sacados-ad")
	private Integer countGruposSacadosAD;
	@JsonProperty("miembros-agregados-bd")
	private Integer countMiembrosAgregadosBD;
	@JsonProperty("miembros-agregados-ad")
	private Integer countMiembrosAgregadosAD;
	@JsonProperty("miembros-sacados-bd")
	private Integer countMiembrosSacadosBD;
	@JsonProperty("miembros-sacados-ad")
	private Integer countMiembrosSacadosAD;

	@JsonCreator
	public ContadoresCrearGrupos(@JsonProperty("grupos-leidos") Integer countProcesados,
			@JsonProperty("errores") Integer countErrores,
			@JsonProperty("grupos-agregados-bd") Integer countGruposAgregadosBD,
			@JsonProperty("grupos-agregados-ad") Integer countGruposAgregadosAD,
			@JsonProperty("grupos-sacados-bd") Integer countGruposSacadosBD,
			@JsonProperty("grupos-sacados-ad") Integer countGruposSacadosAD,
			@JsonProperty("miembros-agregados-bd") Integer countMiembrosAgregadosBD,
			@JsonProperty("miembros-agregados-ad") Integer countMiembrosAgregadosAD,
			@JsonProperty("miembros-sacados-bd") Integer countMiembrosSacadosBD,
			@JsonProperty("miembros-sacados-ad") Integer countMiembrosSacadosAD) {
		super();
		this.countProcesados = countProcesados;
		this.countErrores = countErrores;
		this.countGruposAgregadosBD = countGruposAgregadosBD;
		this.countGruposAgregadosAD = countGruposAgregadosAD;
		this.countGruposSacadosBD = countGruposSacadosBD;
		this.countGruposSacadosAD = countGruposSacadosAD;
		this.countMiembrosAgregadosBD = countMiembrosAgregadosBD;
		this.countMiembrosAgregadosAD = countMiembrosAgregadosAD;
		this.countMiembrosSacadosBD = countMiembrosSacadosBD;
		this.countMiembrosSacadosAD = countMiembrosSacadosAD;
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

	public synchronized void incCountGruposAgregadosBD() {
		countGruposAgregadosBD++;
	}

	public synchronized void incCountGruposAgregadosAD() {
		countGruposAgregadosAD++;
	}

	public synchronized void incCountGruposSacadosBD() {
		countGruposSacadosBD++;
	}

	public synchronized void incCountGruposSacadosAD() {
		countGruposSacadosAD++;
	}

	public synchronized void incCountMiembrosAgregadosBD() {
		countMiembrosAgregadosBD++;
	}

	public synchronized void incCountMiembrosAgregadosAD() {
		countMiembrosAgregadosAD++;
	}

	public synchronized void incCountMiembrosSacadosBD() {
		countMiembrosSacadosBD++;
	}

	public synchronized void incCountMiembrosSacadosAD() {
		countMiembrosSacadosAD++;
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

	public synchronized Integer getCountGruposAgregadosBD() {
		return countGruposAgregadosBD;
	}

	public synchronized void setCountGruposAgregadosBD(Integer countGruposAgregadosBD) {
		this.countGruposAgregadosBD = countGruposAgregadosBD;
	}

	public synchronized Integer getCountGruposAgregadosAD() {
		return countGruposAgregadosAD;
	}

	public synchronized void setCountGruposAgregadosAD(Integer countGruposAgregadosAD) {
		this.countGruposAgregadosAD = countGruposAgregadosAD;
	}

	public synchronized Integer getCountMiembrosAgregadosBD() {
		return countMiembrosAgregadosBD;
	}

	public synchronized void setCountMiembrosAgregadosBD(Integer countMiembrosAgregadosBD) {
		this.countMiembrosAgregadosBD = countMiembrosAgregadosBD;
	}

	public synchronized Integer getCountMiembrosAgregadosAD() {
		return countMiembrosAgregadosAD;
	}

	public synchronized void setCountMiembrosAgregadosAD(Integer countMiembrosAgregadosAD) {
		this.countMiembrosAgregadosAD = countMiembrosAgregadosAD;
	}

	public synchronized Integer getCountMiembrosSacadosBD() {
		return countMiembrosSacadosBD;
	}

	public synchronized void setCountMiembrosSacadosBD(Integer countMiembrosSacadosBD) {
		this.countMiembrosSacadosBD = countMiembrosSacadosBD;
	}

	public synchronized Integer getCountMiembrosSacadosAD() {
		return countMiembrosSacadosAD;
	}

	public synchronized void setCountMiembrosSacadosAD(Integer countMiembrosSacadosAD) {
		this.countMiembrosSacadosAD = countMiembrosSacadosAD;
	}

	public synchronized Integer getCountGruposSacadosBD() {
		return countGruposSacadosBD;
	}

	public synchronized void setCountGruposSacadosBD(Integer countGruposSacadosBD) {
		this.countGruposSacadosBD = countGruposSacadosBD;
	}

	public synchronized Integer getCountGruposSacadosAD() {
		return countGruposSacadosAD;
	}

	public synchronized void setCountGruposSacadosAD(Integer countGruposSacadosAD) {
		this.countGruposSacadosAD = countGruposSacadosAD;
	}

}
