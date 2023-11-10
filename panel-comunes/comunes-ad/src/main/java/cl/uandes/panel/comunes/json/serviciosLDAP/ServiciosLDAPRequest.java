package cl.uandes.panel.comunes.json.serviciosLDAP;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request para uso del WS LDAPServiceb.wsdl<br>
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiciosLDAPRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -56184207768287443L;
	@JsonProperty("servicio")
	String servicio;
	@JsonProperty("activar-desactivar")
	Boolean activar;
	@JsonProperty("datos-servicio")
	Usuario usuario;
	
	@JsonCreator
	public ServiciosLDAPRequest(
			@JsonProperty("servicio")String servicio, 
			@JsonProperty("activar-desactivar")Boolean activar, 
			@JsonProperty("datos-servicio")Usuario usuario) {
		super();
		this.servicio = servicio;
		this.activar = activar;
		this.usuario = usuario;
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

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public Boolean getActivar() {
		return activar;
	}

	public void setActivar(Boolean activar) {
		this.activar = activar;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
