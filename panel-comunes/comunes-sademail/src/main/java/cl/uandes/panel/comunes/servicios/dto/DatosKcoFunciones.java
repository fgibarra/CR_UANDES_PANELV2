package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Datos recuperados desde la tabla KCO_FUNCIONES
 * @author fernando
 *
 */
public class DatosKcoFunciones implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -3975951435260515426L;
	@JsonProperty("KEY")
	private Integer key;
	@JsonProperty("FUNCION")
	private String funcion;
	@JsonProperty("MIN_THREAD")
	private Integer minThread;
	@JsonProperty("MAX_THREAD")
	private Integer maxThread;
	@JsonProperty("PARAMETRO")
	private String bufferParametros;
	@JsonProperty("COMENTARIO")
    private String comentario;
	@JsonProperty("ULTIMA_EJECUCION")
	private Timestamp ultimaEjecucion;
	@JsonProperty("parametros_inicializacion")
	private ParametrosCrearGrupos parametros;
	
	public DatosKcoFunciones(Map<String, Object> datos) {
		super();
		this.key = Integer.valueOf(((BigDecimal)datos.get("KEY")).intValue());
		this.funcion = (String)datos.get("FUNCION");
		this.minThread = Integer.valueOf(((BigDecimal)datos.get("MIN_THREAD")).intValue());
		this.maxThread = Integer.valueOf(((BigDecimal)datos.get("MAX_THREAD")).intValue());
		this.bufferParametros = (String)datos.get("PARAMETRO");
		this.comentario = (String)datos.get("COMENTARIO");
		this.ultimaEjecucion = (Timestamp)datos.get("ULTIMA_EJECUCION");
		if (this.bufferParametros != null)
			this.parametros = new ParametrosCrearGrupos(bufferParametros);
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

	public String getPeriodo() {
		String valor = String.format("%d", parametros.getPeriodo());
		if (valor.length() < 6) {
			if (parametros.getYear() != null)
				return String.format("%d%d", parametros.getYear(),parametros.getPeriodo());
			else if (valor.length() == 4)
				return String.format("%s10,%s20,%s90", valor,valor,valor);
			else
				return null;
		} else
			return valor;
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

	public Integer getMinThread() {
		return minThread;
	}

	public void setMinThread(Integer minThread) {
		this.minThread = minThread;
	}

	public Integer getMaxThread() {
		return maxThread;
	}

	public void setMaxThread(Integer maxThread) {
		this.maxThread = maxThread;
	}

	public ParametrosCrearGrupos getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosCrearGrupos parametros) {
		this.parametros = parametros;
	}

	public Timestamp getUltimaEjecucion() {
		return ultimaEjecucion;
	}

	public void setUltimaEjecucion(Timestamp ultimaEjecucion) {
		this.ultimaEjecucion = ultimaEjecucion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getBufferParametros() {
		return bufferParametros;
	}

	public void setBufferParametros(String bufferParametros) {
		this.bufferParametros = bufferParametros;
	}

}
