package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.util.Map;

import javax.sql.rowset.serial.SerialClob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KcoLogErrorDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735594138261224911L;
	@JsonProperty("KEY")
	Integer key;
	@JsonProperty("LOG_CLASE")
	String logClase;
	@JsonProperty("LOG_METODO")
	String logMetodo;
	@JsonProperty("LOG_EXCEPTION")
	String logException;
	@JsonProperty("LOG_MESSAGE")
	String logMessage;
	@JsonProperty("LOG_APOYO")
	String logApoyo;
	@JsonProperty("LOG_FECHA")
	String logFecha;
	@JsonProperty("STACKTRACE")
	SerialClob stacktrace;
	@JsonProperty("LOG_APLICACION")
	String logAplicacion;
	@JsonProperty("KEY_MI_RESULTADO_ERRORES")
	Integer keyMiResultadoErrores;

	public KcoLogErrorDTO() {
		super();
	}

	public KcoLogErrorDTO(Map<String, Object> datos) {
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

	public String getLogClase() {
		return logClase;
	}

	public void setLogClase(String logClase) {
		this.logClase = logClase;
	}

	public String getLogMetodo() {
		return logMetodo;
	}

	public void setLogMetodo(String logMetodo) {
		this.logMetodo = logMetodo;
	}

	public String getLogException() {
		return logException;
	}

	public void setLogException(String logException) {
		this.logException = logException;
	}

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public String getLogApoyo() {
		return logApoyo;
	}

	public void setLogApoyo(String logApoyo) {
		this.logApoyo = logApoyo;
	}

	public SerialClob getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(SerialClob stacktrace) {
		this.stacktrace = stacktrace;
	}

	public String getLogAplicacion() {
		return logAplicacion;
	}

	public void setLogAplicacion(String logAplicacion) {
		this.logAplicacion = logAplicacion;
	}

	public Integer getKeyMiResultadoErrores() {
		return keyMiResultadoErrores;
	}

	public void setKeyMiResultadoErrores(Integer keyMiResultadoErrores) {
		this.keyMiResultadoErrores = keyMiResultadoErrores;
	}

}
