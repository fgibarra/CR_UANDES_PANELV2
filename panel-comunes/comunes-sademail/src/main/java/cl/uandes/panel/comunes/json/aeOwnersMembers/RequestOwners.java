package cl.uandes.panel.comunes.json.aeOwnersMembers;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestOwners implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6930050682031304855L;

	@JsonProperty("funcion")
	private String funcion;

	@JsonProperty("programas")
	private List<String> programas;
	@JsonProperty("cuentas")
	private List<String> cuentas;

	@JsonCreator
	public RequestOwners(
			@JsonProperty("funcion")String funcion, 
			@JsonProperty("programas")List<String> programas, 
			@JsonProperty("cuentas")List<String> cuentas) {
		super();
		this.funcion = funcion;
		this.programas = programas;
		this.cuentas = cuentas;
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

	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public List<String> getProgramas() {
		return programas;
	}

	public void setProgramas(List<String> programas) {
		this.programas = programas;
	}

	public List<String> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<String> cuentas) {
		this.cuentas = cuentas;
	}
/*
	public static void main (String args[]) {
		String[] pr = new String[] {"uno", "dos", "tres"};
		String[] cu = new String[] {"c1@miuandes.cl", "c2@miuandes.cl"};
		
		RequestOwners req = new RequestOwners("ADD", java.util.Arrays.asList(pr), java.util.Arrays.asList(cu));
		System.out.println(req.toString());
	}
	*/
}
