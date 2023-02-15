package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.services.admin.directory.model.UserName;

import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse implements Serializable, ResultadoGmail {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 4934104985440701230L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("user")
	private User user;

	@JsonCreator
	public UserResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje,
			@JsonProperty("user")User user) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.user = user;
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

	public User factoryUser(com.google.api.services.admin.directory.model.User usr) {
		UserName name = usr.getName();
		User user = new User(usr.getPrimaryEmail(),name.getFullName(), 
		name.getGivenName(), 
		name.getFamilyName(), 
		usr.getPassword());
		user.setId(usr.getId());
		return user;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
