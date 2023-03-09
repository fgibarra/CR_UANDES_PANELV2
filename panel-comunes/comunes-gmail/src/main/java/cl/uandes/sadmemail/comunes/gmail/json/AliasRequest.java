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
public class AliasRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8465786973995422330L;
	@JsonProperty("user-name")
	private String userName;
	@JsonProperty("alias-name")
	private String aliasName;

	@JsonCreator
	public AliasRequest(
			@JsonProperty("user-name")String userName, 
			@JsonProperty("alias-name")String aliasName) {
		super();
		this.userName = userName;
		this.aliasName = aliasName;
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

	public String getUserName() {
		return userName;
	}

	public String getAliasName() {
		return aliasName;
	}

}
