package cl.uandes.panel.comunes.json.batch.crearGrupos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.JSonUtilities;

/**
 *
 * Respuesta al requerimiento de todos los grupos
 * 
 * @author fernando
 */
public class GroupsResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -1782079884986169228L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("grupos")
	Grupo grupo[];

	public GroupsResponse(Integer codigo, String mensaje) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
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

	public Grupo[] getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo[] grupo) {
		this.grupo = grupo;
	}

}
