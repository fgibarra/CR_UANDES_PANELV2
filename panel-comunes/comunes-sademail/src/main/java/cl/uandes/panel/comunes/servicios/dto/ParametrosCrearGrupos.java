package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.CommandLineParser;

/**
 * Parametros incluidos en la entrada crear_grupo de la tabla KCO_FUNCIONES
 * columna prametro.
 * 
 * @author fernando
 *
 */
public class ParametrosCrearGrupos implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -4246576400951408032L;
	@JsonProperty("disabled")
	private Boolean disabled;
	@JsonProperty("year")
	private Integer year;
	@JsonProperty("periodo")
	private Integer periodo;
	@JsonProperty("fuente")
	private String fuente;
	@JsonProperty("force_ad")
	private Boolean forceAD;
	@JsonProperty("tipo_grupo")
	private String tipoGrupo;
	@JsonProperty("uso_alumnos")
	private Long usoAlumnos;
	@JsonProperty("uso_profesores")
	private Long usoProfesores;
	@JsonProperty("uso_generales")
	private Long usoGenerales;

	public ParametrosCrearGrupos(String buffer) {
		super();
		String args[] = buffer.split(" ");
		CommandLineParser parser = new CommandLineParser(args);
		String valor = parser.getValue("disabled");
		if (valor != null)
			this.disabled = Boolean.valueOf(valor);
		else
			this.disabled = Boolean.FALSE;

		valor = parser.getValue("year");
		if (valor != null)
			this.year = Integer.valueOf(valor);

		valor = parser.getValue("periodo");
		if (valor != null)
			this.periodo = Integer.valueOf(valor);

		this.fuente = parser.getValue("fuente");

		valor = parser.getValue("forceAD");
		if (valor != null)
			this.forceAD = Boolean.valueOf(valor);
		else
			this.forceAD = Boolean.FALSE;

		this.tipoGrupo = parser.getValue("tipo_grupo");

		valor = parser.getValue("uso_alumnos");
		if (valor != null)
			this.usoAlumnos = Long.valueOf(valor);
		
		valor = parser.getValue("uso_profesores");
		if (valor != null)
			this.usoProfesores = Long.valueOf(valor);
		
		valor = parser.getValue("uso_generales");
		if (valor != null)
			this.usoGenerales = Long.valueOf(valor);
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

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public String getFuente() {
		return fuente;
	}

	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

	public Boolean getForceAD() {
		return forceAD;
	}

	public void setForceAD(Boolean forceAD) {
		this.forceAD = forceAD;
	}

	public synchronized String getTipoGrupo() {
		return tipoGrupo;
	}

	public synchronized Long getUsoAlumnos() {
		return usoAlumnos;
	}

	public synchronized void setUsoAlumnos(Long usoAlumnos) {
		this.usoAlumnos = usoAlumnos;
	}

	public synchronized Long getUsoProfesores() {
		return usoProfesores;
	}

	public synchronized void setUsoProfesores(Long usoProfesores) {
		this.usoProfesores = usoProfesores;
	}

	public synchronized Long getUsoGenerales() {
		return usoGenerales;
	}

	public synchronized void setUsoGenerales(Long usoGenerales) {
		this.usoGenerales = usoGenerales;
	}

}
