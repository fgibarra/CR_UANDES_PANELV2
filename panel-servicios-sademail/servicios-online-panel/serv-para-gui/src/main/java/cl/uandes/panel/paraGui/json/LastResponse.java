package cl.uandes.panel.paraGui.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastResponse extends GuiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4342638817400863936L;
	@JsonProperty("ultimoUso")
	private String ultimoUso;

	public LastResponse(Integer codigo, String mensaje, String ultimoUso) {
		super(codigo, mensaje);
		this.ultimoUso = ultimoUso;
	}

	public String getUltimoUso() {
		return ultimoUso;
	}

	public void setUltimoUso(String ultimoUso) {
		this.ultimoUso = ultimoUso;
	}
	
}
