package cl.uandes.panel.comunes.json.aeOwnersMembers;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AeOwnersMembersRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1208757370508173469L;

	@JsonProperty("cuentas-envio")
	private String cuentasEnvio;

	@JsonProperty("servicio")
	private String servicio;
	@JsonProperty("criterio")
	private RequestOwners criterio;

	@JsonCreator
	public AeOwnersMembersRequest(
			@JsonProperty("cuentas-envio")String cuentasEnvio, 
			@JsonProperty("servicio")String servicio, 
			@JsonProperty("criterio")RequestOwners criterio) {
		super();
		this.cuentasEnvio = cuentasEnvio;
		this.servicio = servicio;
		this.criterio = criterio;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public String getCuentasEnvio() {
		return cuentasEnvio;
	}

	public String getServicio() {
		return servicio;
	}

	public RequestOwners getCriterio() {
		return criterio;
	}

}
