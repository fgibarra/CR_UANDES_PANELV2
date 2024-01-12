package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMiCuentaGmailDTO;
import cl.uandes.panel.comunes.json.cambiacuenta.CambiaCuentaResponse;
import cl.uandes.panel.comunes.json.cambiacuenta.DatosCuenta;

/**
 * @author fernando
 *
 *         Genera el response para el que invoco al servicio
 */
public class PreparaResponse implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		DatosMiCuentaGmailDTO old = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_old");
		DatosCuenta antes = new DatosCuenta(old.getMoodleId(), old.getBannerPidm(), old.getLoginName(),
				old.getNombres(), old.getApellidos(), old.getIdGmail());
		DatosMiCuentaGmailDTO nuevo = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_new");
		DatosCuenta despues = new DatosCuenta(nuevo.getMoodleId(), nuevo.getBannerPidm(), nuevo.getLoginName(),
				nuevo.getNombres(), nuevo.getApellidos(), nuevo.getIdGmail());
		
		CambiaCuentaResponse response = new CambiaCuentaResponse(0, "OK", antes, despues);
		exchange.getIn().setBody(response);
	}

}
