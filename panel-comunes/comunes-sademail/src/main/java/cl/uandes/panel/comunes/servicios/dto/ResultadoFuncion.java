package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Datos para grabar en la tabla MI_RESULTADO
 * @author fernando
 *
 */
public class ResultadoFuncion implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 6000717503748111557L;
	@JsonProperty("KEY")
	private Integer key;
	@JsonProperty("FUNCION")
	private String funcion;
	@JsonProperty("HORA_COMIENZO")
	private Timestamp horaComienzo;
	@JsonProperty("HORA_TERMINO")
	private Timestamp horaTermino;
	@JsonProperty("GRUPOS_SOLICITADOS")
	private Integer gruposSolicitados;
	@JsonProperty("GRUPOS_CREADOS")
	private Integer gruposCreados;
	@JsonProperty("MIEMBROS_SOLICITADOS")
	private Integer miembrosSolicitados;
	@JsonProperty("MIEMBROS_CREADOS")
	private Integer miembrosCreados;
	@JsonProperty("THREADS_ENGENDRADAS")
	private Integer threadsCreadas;
	@JsonProperty("MIN_THREAD")
	private Integer minThreads;
	@JsonProperty("MAX_THREAD")
	private Integer maxThreads;
	@JsonProperty("REC_PROCESAR")
	private Integer recProcesar;
	@JsonProperty("COMENTARIO")
	private String comentario;
	// sin persistencia
	@JsonProperty("GRUPOS_ACTUALIZADOS")
	private Integer gruposActualizados = 0;
	@JsonProperty("MIEMBROS_SACADOS")
	private Integer miembrosSacados = 0;

	public ResultadoFuncion() {
		super();
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

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public Timestamp getHoraComienzo() {
		return horaComienzo;
	}

	public void setHoraComienzo(Timestamp horaComienzo) {
		this.horaComienzo = horaComienzo;
	}

	public Timestamp getHoraTermino() {
		return horaTermino;
	}

	public void setHoraTermino(Timestamp horaTermino) {
		this.horaTermino = horaTermino;
	}

	public Integer getGruposSolicitados() {
		return gruposSolicitados;
	}

	public void setGruposSolicitados(Integer gruposSolicitados) {
		this.gruposSolicitados = gruposSolicitados;
	}

	public Integer getGruposCreados() {
		return gruposCreados;
	}

	public void setGruposCreados(Integer gruposCreados) {
		this.gruposCreados = gruposCreados;
	}

	public Integer getMiembrosSolicitados() {
		return miembrosSolicitados;
	}

	public void setMiembrosSolicitados(Integer miembrosSolicitados) {
		this.miembrosSolicitados = miembrosSolicitados;
	}

	public Integer getMiembrosCreados() {
		return miembrosCreados;
	}

	public void setMiembrosCreados(Integer miembrosCreados) {
		this.miembrosCreados = miembrosCreados;
	}

	public Integer getThreadsCreadas() {
		return threadsCreadas;
	}

	public void setThreadsCreadas(Integer threadsCreadas) {
		this.threadsCreadas = threadsCreadas;
	}

	public Integer getMinThreads() {
		return minThreads;
	}

	public void setMinThreads(Integer minThreads) {
		this.minThreads = minThreads;
	}

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	public Integer getRecProcesar() {
		return recProcesar;
	}

	public void setRecProcesar(Integer recProcesar) {
		this.recProcesar = recProcesar;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Integer getGruposActualizados() {
		return gruposActualizados;
	}

	public void setGruposActualizados(Integer gruposActualizados) {
		this.gruposActualizados = gruposActualizados;
	}

	public Integer getMiembrosSacados() {
		return miembrosSacados;
	}

	public void setMiembrosSacados(Integer miembrosSacados) {
		this.miembrosSacados = miembrosSacados;
	}

}
