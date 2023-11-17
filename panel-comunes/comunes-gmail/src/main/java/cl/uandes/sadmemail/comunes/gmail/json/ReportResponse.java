package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 340783263044988508L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("reporte")
	Report reporte;

	public ReportResponse(@JsonProperty("codigo") Integer codigo, @JsonProperty("mensaje") String mensaje,
			@JsonProperty("reporte") Report reporte) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.reporte = reporte;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Report getReporte() {
		return reporte;
	}

	public void setReporte(Report reporte) {
		this.reporte = reporte;
	}

}
