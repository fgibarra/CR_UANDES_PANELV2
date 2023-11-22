package cl.uandes.panel.comunes.json.batch.crearcuentas;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 * Respuesta a la consulta por RUT<br>
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultaXrutResponse extends BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2378734236943005463L;
	@JsonProperty("cantidad")
	Integer cantidad;
	@JsonProperty("rut")
	String rut;
	@JsonProperty("usuario")
	String usuario;
	@JsonProperty("employeeid")
	String employeeid;
	@JsonProperty("dn")
	String dn;
	@JsonProperty("ou")
	String ou;
	@JsonProperty("correo")
	String correo;
	@JsonProperty("activa")
	String activa;
	@JsonProperty("bloqueada")
	String bloqueada;
	@JsonProperty("estado")
	String estado;
	
	@JsonCreator
	public ConsultaXrutResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("cantidad")Integer cantidad, 
			@JsonProperty("rut")String rut, 
			@JsonProperty("usuario")String usuario,
			@JsonProperty("employeeid")String employeeid, 
			@JsonProperty("dn")String dn, 
			@JsonProperty("ou")String ou, 
			@JsonProperty("correo")String correo, 
			@JsonProperty("activa")String activa, 
			@JsonProperty("bloqueada")String bloqueada, 
			@JsonProperty("estado")String estado) {
		super(codigo, mensaje);
		this.cantidad = cantidad;
		this.rut = rut;
		this.usuario = usuario;
		this.employeeid = employeeid;
		this.dn = dn;
		this.ou = ou;
		this.correo = correo;
		this.activa = activa;
		this.bloqueada = bloqueada;
		this.estado = estado;
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

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getActiva() {
		return activa;
	}

	public void setActiva(String activa) {
		this.activa = activa;
	}

	public String getBloqueada() {
		return bloqueada;
	}

	public void setBloqueada(String bloqueada) {
		this.bloqueada = bloqueada;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

}
