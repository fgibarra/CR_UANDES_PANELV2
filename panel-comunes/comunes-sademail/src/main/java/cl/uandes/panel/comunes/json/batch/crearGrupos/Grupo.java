package cl.uandes.panel.comunes.json.batch.crearGrupos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.JSonUtilities;

/**
 *
 * Datos de un grupo
 * 
 * @author fernando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Grupo implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -1855707961156346813L;
	@JsonProperty("id")
	private String id;
	@JsonProperty("createdDateTime")
	private String createdDateTime;
	@JsonProperty("expirationDateTime")
	private String expirationDateTime;
	@JsonProperty("description")
	private String description;
	@JsonProperty("displayName")
	private String displayName;
	@JsonProperty("groupTypes")
	private List<String> groupTypes;
	@JsonProperty("mail")
	private String mail;
	
	/*
	public Grupo(Group az_group) {
		this.id = az_group.id;
		this.createdDateTime = az_group.createdDateTime!=null? az_group.createdDateTime.format(DateTimeFormatter.BASIC_ISO_DATE) : null;
		this.description = az_group.description;
		this.displayName = az_group.displayName;
		this.groupTypes = az_group.groupTypes;
		this.mail = az_group.mail;
		this.expirationDateTime = az_group.expirationDateTime!=null?az_group.expirationDateTime.format(DateTimeFormatter.BASIC_ISO_DATE): null;
	}
*/
	public Grupo() {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getGroupTypes() {
		return groupTypes;
	}

	public void setGroupTypes(List<String> groupTypes) {
		this.groupTypes = groupTypes;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getExpirationDateTime() {
		return expirationDateTime;
	}

	public void setExpirationDateTime(String expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

}
