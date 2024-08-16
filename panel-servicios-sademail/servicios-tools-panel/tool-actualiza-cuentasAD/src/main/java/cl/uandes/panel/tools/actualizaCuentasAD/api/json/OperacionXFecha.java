package cl.uandes.panel.tools.actualizaCuentasAD.api.json;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacionXFecha implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2189135196162173845L;
	@JsonProperty("fecha_desde")
	private String fechaDesde;
	@JsonProperty("fecha_hasta")
	private String fechaHasta;
	@JsonProperty("max_resultados")
	private Integer maxResultados;

	@JsonCreator
	public OperacionXFecha(
			@JsonProperty("fecha_desde")String fechaDesde, 
			@JsonProperty("fecha_hasta")String fechaHasta, 
			@JsonProperty("max_resultados")Integer maxResultados) {
		super();
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.maxResultados = maxResultados;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public String getFechaDesde() {
		return fechaDesde;
	}

	public String getFechaHasta() {
		return fechaHasta;
	}

	public Integer getMaxResultados() {
		return maxResultados;
	}

	@JsonIgnore
	public Timestamp getTimestampFechaDesde() {
		if (fechaDesde == null)
			return null;
		return toTimestamp(String.format("%s000000",fechaDesde));
	}

	@JsonIgnore
	public Timestamp getTimestampFechaHasta() {
		if (fechaHasta == null)
			return null;
		return toTimestamp(String.format("%s235959",fechaHasta));
	}

	private Timestamp toTimestamp(String fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyhhmmss", new java.util.Locale("es", "CL"));
		try {
			Date f = sdf.parse(fecha);
			return new Timestamp(f.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
