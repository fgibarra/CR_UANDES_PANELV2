package cl.uandes.sadmemail.comunes.google.api.services;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1056652185849102433L;
	@JsonProperty("user_name")
	String username;
	@JsonProperty("given_name")
	String givenName;
	@JsonProperty("family_name")
	String familyName;
	@JsonProperty("password")
	String password;
	@JsonProperty("id")
	String id;
	@JsonProperty("email")
	String email;
	
	@JsonCreator
	public User(
			@JsonProperty("email")String email,
			@JsonProperty("user_name")String username, 
			@JsonProperty("given_name")String givenName, 
			@JsonProperty("family_name")String familyName, 
			@JsonProperty("password")String password) {
		super();
		this.email = email;
		this.username = username;
		this.givenName = givenName;
		this.familyName = familyName;
		this.password = password;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
