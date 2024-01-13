package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.panel.comunes.utils.ObjectFactory;

/**
 * Datos recuperados desde SQL getAlumnosNuevos o getAlumnosNuevosPeriodos
 * @author fernando
 *
 */
public class DatosCuentasBanner extends CuentasADDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5255854765123492217L;
	@JsonProperty("SPRIDEN_PIDM")
	private Integer spridenPidm;

	public DatosCuentasBanner(Map<String, Object> datos) {
		super(datos);
		this.spridenPidm = ObjectFactory.toInteger((BigDecimal)datos.get("SPRIDEN_PIDM"));
	}

	public String getSpridenId() {
		return getRut();
	}
}
