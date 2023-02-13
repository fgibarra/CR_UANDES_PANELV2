package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMiCuentaGmailDTO;
import cl.uandes.panel.comunes.json.cambiacuenta.CambiaCuentaResponse;
import cl.uandes.panel.comunes.json.cambiacuenta.DatosCuenta;

public class GeneraResponseError implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String msgError = (String)exchange.getIn().getBody();
		DatosCuenta antes = null;
		DatosCuenta despues = null;
		
		DatosMiCuentaGmailDTO old = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_old");
		if (old != null)
			antes = new DatosCuenta(old.getMoodleId(), old.getBannerPidm(), old.getLoginName(),
				old.getNombres(), old.getApellidos(), old.getIdGmail());
		DatosMiCuentaGmailDTO nuevo = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_new");
		if (nuevo != null)
			despues = new DatosCuenta(nuevo.getMoodleId(), nuevo.getBannerPidm(), nuevo.getLoginName(),
					nuevo.getNombres(), nuevo.getApellidos(), nuevo.getIdGmail());
		
		CambiaCuentaResponse response = new CambiaCuentaResponse(-1, msgError, antes, despues);
		exchange.getIn().setBody(response);
		
	}

}
