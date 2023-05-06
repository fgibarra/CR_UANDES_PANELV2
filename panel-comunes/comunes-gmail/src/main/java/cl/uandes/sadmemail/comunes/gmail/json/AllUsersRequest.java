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
public class AllUsersRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -4818887747764319153L;
	@JsonProperty("nextToken")
	private String pageToken;
	@JsonProperty("showDeleted")
	private Boolean showDeleted;

	@JsonCreator
	public AllUsersRequest(
			@JsonProperty("nextToken")String pageToken, 
			@JsonProperty("showDeleted")Boolean showDeleted) {
		super();
		this.pageToken = pageToken;
		this.showDeleted = showDeleted;
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

	public String getPageToken() {
		return pageToken;
	}

	public Boolean getShowDeleted() {
		return showDeleted;
	}

}
