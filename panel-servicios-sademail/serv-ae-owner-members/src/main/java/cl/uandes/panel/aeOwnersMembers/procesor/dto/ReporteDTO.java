package cl.uandes.panel.aeOwnersMembers.procesor.dto;

import java.io.Serializable;
import java.util.Map;

public class ReporteDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2272912670722072034L;
	private String correo;
	private String lista;
	private String funcion;
	private String fecha;
	private String resultado;

	public ReporteDTO(Map<String, Object> map) {
		super();
		this.setCorreo((String) map.get("correo"));
		this.setLista((String)map.get("lista"));
		this.setFuncion((String)map.get("funcion"));
		this.setFecha((String)map.get("fecha"));
		this.setResultado((String)map.get("resultado"));
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

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

}
