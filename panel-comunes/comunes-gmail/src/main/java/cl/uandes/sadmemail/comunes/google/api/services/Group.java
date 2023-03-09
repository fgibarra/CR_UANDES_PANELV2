package cl.uandes.sadmemail.comunes.google.api.services;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3628882718878542090L;
	@JsonProperty("description")
	private String description;
	@JsonProperty("email")
	private String email;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("id")
	private String id;
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("name")
	private String name;
	@JsonProperty("aliases")
	private List<String> aliases;
	@JsonProperty("directMembersCount")
	private Long directMembersCount;

	@JsonCreator
	public Group(
			@JsonProperty("description")String description, 
			@JsonProperty("email")String email, 
			@JsonProperty("etag")String etag, 
			@JsonProperty("id")String id, 
			@JsonProperty("kind")String kind, 
			@JsonProperty("name")String name,
			@JsonProperty("aliases")List<String> aliases, 
			@JsonProperty("directMembersCount")Long directMembersCount) {
		super();
		this.description = description;
		this.email = email;
		this.etag = etag;
		this.id = id;
		this.kind = kind;
		this.name = name;
		this.aliases = aliases;
		this.directMembersCount = directMembersCount;
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

	public String getDescription() {
		return description;
	}

	public String getEmail() {
		return email;
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

	public String getName() {
		return name;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public Long getDirectMembersCount() {
		return directMembersCount;
	}

}
