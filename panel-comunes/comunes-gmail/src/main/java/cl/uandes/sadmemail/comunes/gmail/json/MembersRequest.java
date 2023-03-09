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
public class MembersRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794187662437609192L;
	@JsonProperty("groupName")
	private String groupName;
	@JsonProperty("token")
	private String token;

	@JsonCreator
	public MembersRequest(
			@JsonProperty("groupName")String groupName, 
			@JsonProperty("token")String token) {
		super();
		this.groupName = groupName;
		this.token = token;
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

	public String getToken() {
		return token;
	}

}
