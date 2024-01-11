package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.StringUtilities;

public class CuentasADDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8810317914858920957L;
	@JsonProperty("NOMBRES")
	private String nombres;
	@JsonProperty("APELLIDOS")
	private String apellidos;
	@JsonProperty("RUT")
	private String rut;
	@JsonProperty("PASSWORD")
	private String password;
	@JsonProperty("EMAIL")
	private String email;
	@JsonProperty("DIRECCION")
	private String direccion;
	@JsonProperty("COMUNA")
	private String comuna;
	@JsonProperty("RAMA")
	private String rama;
	@JsonProperty("LOGIN_NAME")
	private String loginName;
	@JsonProperty("LOGIN_NAME_0")
	private String loginName0;

	private Integer seq = 1;
	
	public CuentasADDTO(String titulos, String linea) {
		super();
		if (linea != null && linea.length() > 0 && titulos != null && titulos.length() > 0) { 
			String valoresT[] = titulos.split("\u0003");
			String valoresD[] = linea.split("\u0003");
			
			if (valoresT.length >= valoresD.length) {
				String metodos[] = new String[valoresT.length];
				int i = 0;
				for (String valor : valoresT) {
					valor = valor.toLowerCase();
					metodos[i++] = String.format("set%s", String.format("%s%s", valor.substring(0, 1).toUpperCase(), valor.substring(1)));
				}
				
				i = 0;
				for (String dato : valoresD) {
					try {
						java.lang.reflect.Method metodo = getClass().getMethod(metodos[i++], String.class);
						metodo.invoke(this, dato);
					} catch (Exception e) {
						;
					}
				}
				
			}
		}
		
	}

	public synchronized void incSeq() {
		this.seq++;
	}
	
	public synchronized String setLastLoginName() {
		this.loginName = String.format("%s%d", loginName0, seq);
		return this.loginName;
	}
	
	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public String getSamaccountName() {
		return getLoginName();
	}
	
	public String getEmployeeId() {
		if (rut != null && rut.charAt(0) == '@')
			return rut.substring(1);
		
		return getRut();
	}
	
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getComuna() {
		return comuna;
	}

	public void setComuna(String comuna) {
		this.comuna = comuna;
	}

	public String getRama() {
		return rama;
	}

	public void setRama(String rama) {
		this.rama = rama;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setSeq(String valor) {
		this.seq = StringUtilities.getInstance().toInteger(valor);
	}

	public String getLoginName0() {
		return loginName0;
	}

	public void setLoginName0(String loginName0) {
		this.loginName0 = loginName0;
	}

}
