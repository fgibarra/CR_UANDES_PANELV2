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
	@JsonProperty("NOMBRE")
	private String nombre;
	@JsonProperty("MIDDLE_NAME")
	private String middleName;
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

	private Integer seq = 0;
	
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
	/*
	public static void main(String args[]) {
		String titulos = "NOMBRES\u0003PASSWORD\u0003RAMA\u0003RUT\u0003APELLIDOS\u0003COMUNA\u0003EMAIL\u0003DIRECCION";
		String datos = "JIMENA ANDREA\u000313901950\u0003BibliotecaUA\u0003139019504\u0003SÍLVA YAÑEZ\u0003PROVIDENCIA\u0003JSY.SILVA@GMAIL.COM\u0003DARIO URZUA 1910, D 203";
		CuentasADDTO dto = new CuentasADDTO(titulos, datos);
		System.out.println(String.format("samaccountName=%s dt=%s",dto.getSamaccountName(), dto));
	}
	*/
	public String getEmployeeId() {
		if (rut != null && rut.charAt(0) == '@')
			return rut.substring(1);
		
		return getRut();
	}
	
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		if (nombres != null) {
			String partes[] = nombres.split(" ");
			if (partes.length == 2) {
				setNombre(partes[0]);
				setMiddleName(partes[1]);
			} else
				setNombre(nombres);
		}
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
		if (loginName0 == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(this.nombre.toLowerCase().charAt(0));
			if (this.middleName != null)
				sb.append(this.middleName.toLowerCase().charAt(0));
			String ap[] = apellidos.split(" ");
			sb.append(StringUtilities.getInstance().toLetrasAscii(ap[0].toLowerCase()));
			loginName0 = sb.toString();
		}
		return loginName0;
	}

	public void setLoginName0(String loginName0) {
		this.loginName0 = loginName0;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
