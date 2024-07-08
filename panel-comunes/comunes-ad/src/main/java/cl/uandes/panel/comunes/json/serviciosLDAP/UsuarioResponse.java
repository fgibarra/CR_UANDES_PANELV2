package cl.uandes.panel.comunes.json.serviciosLDAP;

public class UsuarioResponse extends Usuario {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213001075291748450L;
	private String distinguishedName;
	private String grupo;
	private String manager;
	private String userPrincipalName;
	
	public UsuarioResponse(String cuenta, String password, String rama, String rut, String nombre, String apellidos,
			String correo, String telefono, String direccion, String comuna, String cargo, String departamento,
			String jefatura, String compania, String pidm, String nivel, String estadoAcademico,
			String distinguishedName, String grupo, String manager, String userPrincipalName) {
		super(cuenta, password, rama, rut, nombre, apellidos, correo, direccion, comuna, 
				pidm, nivel, estadoAcademico
				/*,telefono, cargo, departamento, jefatura, compania, */);
		
		this.distinguishedName = distinguishedName;
		this.grupo = grupo;
		this.manager = manager;
		this.userPrincipalName = userPrincipalName;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

}
