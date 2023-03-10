package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.google.api.services.Alias;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliasResponse implements Serializable, ResultadoGmail {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -7581275675446890747L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("hay_alias")
	Boolean hayAlias;
	@JsonProperty("alias")
	Alias alias;

	@JsonCreator
	public AliasResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje,
			@JsonProperty("hay_alias")Boolean hayAlias) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.hayAlias = hayAlias;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
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

	public Boolean getHayAlias() {
		return hayAlias;
	}

	public void setHayAlias(Boolean hayAlias) {
		this.hayAlias = hayAlias;
	}

	public Alias getAlias() {
		return alias;
	}

	public void setAlias(Alias alias) {
		this.alias = alias;
	}

}
