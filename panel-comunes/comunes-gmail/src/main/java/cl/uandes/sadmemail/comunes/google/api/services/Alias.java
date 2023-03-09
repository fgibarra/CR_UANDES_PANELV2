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
public class Alias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4290504279030654221L;
	@JsonProperty("alias")
	private String alias;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("id")
	private String id;
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("primary_email")
	private String primaryEmail;

	@JsonCreator
	public Alias(
			@JsonProperty("alias")String alias, 
			@JsonProperty("etag")String etag, 
			@JsonProperty("id")String id, 
			@JsonProperty("kind")String kind, 
			@JsonProperty("primary_email")String primaryEmail) {
		super();
		this.alias = alias;
		this.etag = etag;
		this.id = id;
		this.kind = kind;
		this.primaryEmail = primaryEmail;
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

	public String getAlias() {
		return alias;
	}

	public String getEtag() {
		return etag;
	}

	public String getId() {
		return id;
	}

	public String getKind() {
		return kind;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

}
