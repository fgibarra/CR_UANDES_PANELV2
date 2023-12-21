package cl.uandes.panel.servicio.owners.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WrkOwnersGruposDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1460857333861356142L;
	@JsonProperty("CORREO")
	private String correo;
	@JsonProperty("LISTA")
	private String lista;
	@JsonProperty("FUNCION")
	private String funcion;
	@JsonProperty("ROWID")
	private oracle.sql.ROWID rowid;

	public WrkOwnersGruposDTO(Map<String, Object> datos) {
		super();
		this.correo = (String) datos.get("CORREO");
		this.lista = (String) datos.get("LISTA");
		this.funcion = (String) datos.get("FUNCION");
		this.rowid = (oracle.sql.ROWID) datos.get("ROWID");
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

	@JsonIgnore
	public Object getCorreoSinDominio() {
		String valor = getCorreo();
		if (valor!=null) {
			int pos = valor.indexOf('@');
			if (pos  >= 0)
				valor = valor.substring(0, pos);
		}
		return valor;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}

	public oracle.sql.ROWID getRowid() {
		return rowid;
	}

	public void setRowid(oracle.sql.ROWID rowid) {
		this.rowid = rowid;
	}

}
