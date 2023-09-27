package cl.uandes.panel.comunes.json.sincronizaGrupos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SincronizaGruposRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 2674952206668121520L;
	@JsonProperty("mail-reporte")
	String mailTO;
	@JsonProperty("operacion")
	String operacion;

	@JsonCreator
	public SincronizaGruposRequest(
			@JsonProperty("mail-reporte")String mailTO, 
			@JsonProperty("operacion")String operacion) {
		super();
		this.mailTO = mailTO;
		this.operacion = operacion;
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

	public String getMailTO() {
		return mailTO;
	}

	public void setMailTO(String mailTO) {
		this.mailTO = mailTO;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

}
