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

	public void incCountProcesados() {
		countProcesados++;
	}

	public void incCountErrores() {
		countErrores++;
	}

	public void incCountGruposAgregadosBD() {
		countGruposAgregadosBD++;
	}

	public void incCountGruposAgregadosAD() {
		countGruposAgregadosAD++;
	}

	public void incCountGruposSacadosBD() {
		countGruposSacadosBD++;
	}

	public void incCountGruposSacadosAD() {
		countGruposSacadosAD++;
	}

	public void incCountMiembrosAgregadosBD() {
		countMiembrosAgregadosBD++;
	}

	public void incCountMiembrosAgregadosAD() {
		countMiembrosAgregadosAD++;
	}

	public void incCountMiembrosSacadosBD() {
		countMiembrosSacadosBD++;
	}

	public void incCountMiembrosSacadosAD() {
		countMiembrosSacadosAD++;
	}

	public Integer getCountProcesados() {
		return countProcesados;
	}

	public void setCountProcesados(Integer countProcesados) {
		this.countProcesados = countProcesados;
	}

	public Integer getCountErrores() {
		return countErrores;
	}

	public void setCountErrores(Integer countErrores) {
		this.countErrores = countErrores;
	}

	public Integer getCountGruposAgregadosBD() {
		return countGruposAgregadosBD;
	}

	public void setCountGruposAgregadosBD(Integer countGruposAgregadosBD) {
		this.countGruposAgregadosBD = countGruposAgregadosBD;
	}

	public Integer getCountGruposAgregadosAD() {
		return countGruposAgregadosAD;
	}

	public void setCountGruposAgregadosAD(Integer countGruposAgregadosAD) {
		this.countGruposAgregadosAD = countGruposAgregadosAD;
	}

	public Integer getCountMiembrosAgregadosBD() {
		return countMiembrosAgregadosBD;
	}

	public void setCountMiembrosAgregadosBD(Integer countMiembrosAgregadosBD) {
		this.countMiembrosAgregadosBD = countMiembrosAgregadosBD;
	}

	public Integer getCountMiembrosAgregadosAD() {
		return countMiembrosAgregadosAD;
	}

	public void setCountMiembrosAgregadosAD(Integer countMiembrosAgregadosAD) {
		this.countMiembrosAgregadosAD = countMiembrosAgregadosAD;
	}

	public Integer getCountMiembrosSacadosBD() {
		return countMiembrosSacadosBD;
	}

	public void setCountMiembrosSacadosBD(Integer countMiembrosSacadosBD) {
		this.countMiembrosSacadosBD = countMiembrosSacadosBD;
	}

	public Integer getCountMiembrosSacadosAD() {
		return countMiembrosSacadosAD;
	}

	public void setCountMiembrosSacadosAD(Integer countMiembrosSacadosAD) {
		this.countMiembrosSacadosAD = countMiembrosSacadosAD;
	}

	public Integer getCountGruposSacadosBD() {
		return countGruposSacadosBD;
	}

	public void setCountGruposSacadosBD(Integer countGruposSacadosBD) {
		this.countGruposSacadosBD = countGruposSacadosBD;
	}

	public Integer getCountGruposSacadosAD() {
		return countGruposSacadosAD;
	}

	public void setCountGruposSacadosAD(Integer countGruposSacadosAD) {
		this.countGruposSacadosAD = countGruposSacadosAD;
	}

}
