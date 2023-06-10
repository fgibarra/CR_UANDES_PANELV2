package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnBanner implements Serializable {
	
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 7913620013534419143L;
	@JsonProperty("ruts")
	private String[] ruts;

	@JsonCreator
	public EnBanner(@JsonProperty("ruts")String[] ruts) {
		super();
		this.ruts = ruts;
	}

	public String[] getRuts() {
		return ruts;
	}

	public void setRuts(String[] ruts) {
		this.ruts = ruts;
	}

}
