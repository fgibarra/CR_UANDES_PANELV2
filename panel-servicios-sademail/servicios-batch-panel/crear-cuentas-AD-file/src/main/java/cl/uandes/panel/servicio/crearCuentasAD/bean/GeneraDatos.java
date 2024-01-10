package cl.uandes.panel.servicio.crearCuentasAD.bean;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

public class GeneraDatos {

	@PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue = "proceso")
	private String proceso;
	@PropertyInject(value = "crear-cuentas-gmail.kco-funcion", defaultValue = "crear_cuentas")
	private String kcoFuncion;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Genera lista para crear cuentas AD a partir del archivo leido
	 * 
	 * @param exchange
	 */
	public void generaListaXfile(Exchange exchange) {
		Message message = exchange.getIn();
		Object body = message.getBody();

		// KCO_FUNCIONES
		logger.info(String.format("para KCO_FUNCIONES: key: %s\n%s", body.getClass().getSimpleName(), body));

	}

	/**
	 * Genera lista para crear cuentas AD segun el request recibido
	 * 
	 * @param exchange
	 */
	public void generaListaXrequest(Exchange exchange) {
		Message message = exchange.getIn();
		Object body = message.getBody();

		// KCO_FUNCIONES
		logger.info(String.format("para KCO_FUNCIONES: key: %s\n%s", body.getClass().getSimpleName(), body));

	}

	//===============================================================================================================
	// Getters y Setters
	//===============================================================================================================
	
	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getKcoFuncion() {
		return kcoFuncion;
	}

	public void setKcoFuncion(String kcoFuncion) {
		this.kcoFuncion = kcoFuncion;
	}

}
