package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4174052371638491L;
	@JsonProperty("groupName")
	private String groupName;
	@JsonProperty("descripcion")
	private String descripcion;
	@JsonProperty("emailPermission")
	private String emailPermission;

	@JsonCreator
	public GroupRequest(
			@JsonProperty("groupName")String groupName, 
			@JsonProperty("descripcion")String descripcion, 
			@JsonProperty("emailPermission")String emailPermission) {
		super();
		this.groupName = groupName;
		this.descripcion = descripcion;
		this.emailPermission = emailPermission;
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

	public String getGroupName() {
		return groupName;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getEmailPermission() {
		return emailPermission;
	}

}
