package cl.uandes.panel.comunes.json.serviciosLDAP;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioResponse extends Usuario {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213001075291748450L;
	@JsonProperty("distinguishedName")
	private String distinguishedName;
	@JsonProperty("userPrincipalName")
	private String userPrincipalName;
	
	@JsonCreator
	public UsuarioResponse(@JsonProperty("cuenta")String cuenta, 
			@JsonProperty("rama")String rama, 
			@JsonProperty("rut")String rut, 
			@JsonProperty("nombre")String nombre, 
			@JsonProperty("apellidos")String apellidos,
			@JsonProperty("correo")String correo, 
			//String telefono, 
			@JsonProperty("direccion")String direccion, 
			@JsonProperty("comuna")String comuna, 
			//String cargo, String departamento,
			//String jefatura, String compania, 
			@JsonProperty("pidm")String pidm, 
			@JsonProperty("nivel")String nivel, 
			@JsonProperty("estado-academico")String estadoAcademico,
			@JsonProperty("distinguishedName")String distinguishedName, 
			//String grupo, String manager, 
			@JsonProperty("userPrincipalName")String userPrincipalName) {
		super(cuenta, null, rama, rut, nombre, apellidos, correo, direccion, comuna, 
				pidm, nivel, estadoAcademico
				/*,telefono, cargo, departamento, jefatura, compania, */);
		
		this.distinguishedName = distinguishedName;
		this.userPrincipalName = userPrincipalName;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

}
