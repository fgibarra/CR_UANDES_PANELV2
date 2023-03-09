package cl.uandes.sadmemail.comunes.gmail.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.google.api.services.Alias;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliasesResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2959302954201759657L;
	@JsonProperty("codigo")
	Integer codigo;
	@JsonProperty("mensaje")
	String mensaje;
	@JsonProperty("lista-alias")
	List<Alias> listaAlias;

	@JsonCreator
	public AliasesResponse(
			@JsonProperty("codigo")Integer codigo, 
			@JsonProperty("mensaje")String mensaje, 
			@JsonProperty("lista-alias")List<Alias> listaAlias) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.listaAlias = listaAlias;
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

	public String getMensaje() {
		return mensaje;
	}

	public List<Alias> getListaAlias() {
		return listaAlias;
	}

}
