package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllUsersResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3242793111457263290L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("nextToken")
	String nextToken;
	@JsonProperty("cantidad-recuperados")
	Integer cantidadRecuperados;
	@JsonProperty("lista-users")
	List<User> listaUsuarios;

	public AllUsersResponse(
			@JsonProperty("codigo") Integer codigo,
			@JsonProperty("mensaje") String mensaje,
			@JsonProperty("nextToken")String nextToken) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.nextToken = nextToken;
		
	}
	
	public List<User> factoryListaUsuarios(java.util.List<com.google.api.services.admin.directory.model.User> listaIn) {
		List<User> listaOut = new ArrayList<User>();
		if (listaIn != null && listaIn.size() > 0)
			for (com.google.api.services.admin.directory.model.User us : listaIn) {
				User usuario = new User(us.getPrimaryEmail(), us.getCustomerId(), us.getName().getGivenName(), us.getName().getFamilyName(), us.getPassword()); 
				usuario.setId(us.getId());
				listaOut.add(usuario);
			}
		return listaOut;
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

	public String getNextToken() {
		return nextToken;
	}

	public void setNextToken(String nextToken) {
		this.nextToken = nextToken;
	}

	public List<User> getListaUsuarios() {
		return listaUsuarios;
	}

	public void setListaUsuarios(List<User> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	public Integer getCantidadRecuperados() {
		return cantidadRecuperados;
	}

	public void setCantidadRecuperados(Integer cantidadRecuperados) {
		this.cantidadRecuperados = cantidadRecuperados;
	}
}
