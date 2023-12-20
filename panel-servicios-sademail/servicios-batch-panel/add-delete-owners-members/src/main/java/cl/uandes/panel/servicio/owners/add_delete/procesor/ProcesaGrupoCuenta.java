package cl.uandes.panel.servicio.owners.add_delete.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;

import cl.uandes.panel.comunes.bean.RegistrosComunes;

public class ProcesaGrupoCuenta implements Processor {

	private RegistrosComunes registrosBD;

    @PropertyInject(value = "uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

	}

	//======================================================================================================================================
	// Getters y Setters
	//======================================================================================================================================

	public RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

	public String getGmailServices() {
		return gmailServices;
	}

	public void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}

}
