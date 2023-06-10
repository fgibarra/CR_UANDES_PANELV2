package cl.uandes.panel.comunes.json.creaCuenta;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearCuentasResponse implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -4086062791812933776L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("cuentas-creadas-banner")
	Integer cuentasCreadasEnBanner;
	@JsonProperty("cuentas-creadas-no-banner")
	Integer cuentasCreadasEnGmail;
	@JsonProperty("errores")
	Integer errores;

	public CrearCuentasResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("cuentas-creadas-banner")Integer cuentasCreadasEnBanner,
			@JsonProperty("cuentas-creadas-no-banner")Integer cuentasCreadasEnGmail, 
			@JsonProperty("errores")Integer errores) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.cuentasCreadasEnBanner = cuentasCreadasEnBanner;
		this.cuentasCreadasEnGmail = cuentasCreadasEnGmail;
		this.errores = errores;
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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Integer getCuentasCreadasEnBanner() {
		return cuentasCreadasEnBanner;
	}

	public void setCuentasCreadasEnBanner(Integer cuentasCreadasEnBanner) {
		this.cuentasCreadasEnBanner = cuentasCreadasEnBanner;
	}

	public Integer getCuentasCreadasEnGmail() {
		return cuentasCreadasEnGmail;
	}

	public void setCuentasCreadasEnGmail(Integer cuentasCreadasEnGmail) {
		this.cuentasCreadasEnGmail = cuentasCreadasEnGmail;
	}

	public Integer getErrores() {
		return errores;
	}

	public void setErrores(Integer errores) {
		this.errores = errores;
	}

}
