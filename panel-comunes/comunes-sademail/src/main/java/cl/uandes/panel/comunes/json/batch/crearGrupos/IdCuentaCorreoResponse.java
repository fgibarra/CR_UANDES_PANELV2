package cl.uandes.panel.comunes.json.batch.crearGrupos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.JSonUtilities;

/**
 * Respuesta para consulta por ID de una cuenta email
 * 
 * @author fernando
 *
 */
public class IdCuentaCorreoResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -1738652712534077087L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("idCuenta")
	String idCuenta;

	public IdCuentaCorreoResponse(Integer codigo, String mensaje, String idCuenta) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.idCuenta = idCuenta;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
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

	public String getIdCuenta() {
		return idCuenta;
	}

	public void setIdCuenta(String idCuenta) {
		this.idCuenta = idCuenta;
	}

}
