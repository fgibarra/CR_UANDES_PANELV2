package cl.uandes.panel.comunes.json.batch.crearGrupos;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.JSonUtilities;

/**
 *
 * Requerimiento para member
 * 
 * @author fernando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberRequest implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -113369445284225882L;
	@JsonProperty("groupId")
	private String groupId;
	@JsonProperty("userId")
	private String userId;

	@JsonCreator
	public MemberRequest(
			@JsonProperty("groupId")String groupId, 
			@JsonProperty("userId")String userId) {
		super();
		this.groupId = groupId;
		this.userId = userId;
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

	public String getGroupId() {
		return groupId;
	}

	public String getUserId() {
		return userId;
	}
}
