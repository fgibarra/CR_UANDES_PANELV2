package cl.uandes.panel.tools.actualizaCuentasAD.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.StringUtilities;

public class BdCuentasSinActualizarDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3016253951385658213L;
	@JsonProperty("SAMACCOUNT_NAME")
	private String sammacountName;
	@JsonProperty("RUT")
	private String rut;
	@JsonProperty("KEY")
	private Integer key;

	public BdCuentasSinActualizarDTO(Map<String, Object> map) {
		super();
		this.sammacountName = (String) map.get("SAMACCOUNT_NAME");
		this.rut = (String) map.get("RUT");
		this.key = StringUtilities.getInstance().toInteger((BigDecimal) map.get("KEY"));
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

	public String getSammacountName() {
		return sammacountName;
	}

	public void setSammacountName(String sammacountName) {
		this.sammacountName = sammacountName;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

}
