package cl.uandes.panel.apiCrearCuentaServices.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutResponse;

public class DatosCuentaAdDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 6421452770816487068L;
	@JsonProperty("samaccountName")
	private String samaccountName;
	@JsonProperty("employeeId")
	private String employeeId;
	@JsonProperty("rama")
	private String rama;
	@JsonProperty("correo")
	private String correo;
	@JsonProperty("dn")
	private String dn[];
	
	public DatosCuentaAdDTO(ConsultaXrutResponse consultaXrutResponse) {
		this.samaccountName = consultaXrutResponse.getUsuario();
		this.employeeId = consultaXrutResponse.getEmployeeid();
		this.correo = consultaXrutResponse.getCorreo();
		String valores[] = consultaXrutResponse.getDn().split(",");
		this.dn = valores;
		this.rama = valores[1].substring(3);
	}

	public boolean isInRamaPermitida() {
		if ("AlumnosUA".equals(this.rama) || "ProfesoresUA".equals(this.rama))
			return true;
		return false;
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

	public String getSamaccountName() {
		return samaccountName;
	}

	public void setSamaccountName(String samaccountName) {
		this.samaccountName = samaccountName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getRama() {
		return rama;
	}

	public void setRama(String rama) {
		this.rama = rama;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String[] getDn() {
		return dn;
	}

	public void setDn(String[] dn) {
		this.dn = dn;
	}

	@JsonProperty("loginName")
	public String getLoginName() {
		String loginName = null;
		if (correo != null) {
			int endIndex = correo.indexOf('@');
			loginName = endIndex >= 0 ? correo.substring(0, endIndex) : correo;
		}
		return loginName;
	}

	public void setLoginName(String loginName) {
		if (loginName.indexOf('@') < 0)
			setCorreo(String.format("%s@miuandes.cl", loginName));
		else
			setCorreo(loginName);
	}
}
