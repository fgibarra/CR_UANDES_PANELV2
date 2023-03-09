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
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8595914223482713942L;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("email")
	private String email;
	@JsonProperty("id")
	private String id;
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("role")
	private String role;
	@JsonProperty("type")
	private String type;

	@JsonCreator
	public Member(
			@JsonProperty("etag")String etag, 
			@JsonProperty("email")String email, 
			@JsonProperty("id")String id, 
			@JsonProperty("kind")String kind, 
			@JsonProperty("role")String role,
			@JsonProperty("type")String type) {
		super();
		this.etag = etag;
		this.email = email;
		this.id = id;
		this.kind = kind;
		this.role = role;
		this.type = type;
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

	public String getEtag() {
		return etag;
	}

	public String getEmail() {
		return email;
	}

	public String getId() {
		return id;
	}

	public String getKind() {
		return kind;
	}

	public String getRole() {
		return role;
	}

	public String getType() {
		return type;
	}

}
