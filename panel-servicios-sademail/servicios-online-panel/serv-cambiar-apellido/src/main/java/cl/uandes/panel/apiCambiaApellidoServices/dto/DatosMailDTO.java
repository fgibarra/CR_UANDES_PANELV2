package cl.uandes.panel.apiCambiaApellidoServices.dto;

import java.io.Serializable;

public class DatosMailDTO implements Serializable {

	private String caption;
	private String rut;
	private Long pidm;
	private String cuenta;
	private String nombres;
	private String apellidos;

	public DatosMailDTO(String caption, String rut, Long pidm, String cuenta, String nombres, String apellidos) {
		super();
		this.caption = caption;
		this.rut = rut;
		this.pidm = pidm;
		this.cuenta = cuenta;
		this.nombres = nombres;
		this.apellidos = apellidos;
	}

	public String getCaption() {
		return caption;
	}

	public String getRut() {
		return rut;
	}

	public Long getPidm() {
		return pidm;
	}

	public String getCuenta() {
		return cuenta;
	}

	public String getNombres() {
		return nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

}
