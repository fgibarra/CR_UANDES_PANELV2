package cl.uandes.panel.apiCrearCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;

public class ArmaMiCuentasGmailFromRequest implements Processor {

	Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		CreaCuentaRequest request = (CreaCuentaRequest)exchange.getIn().getHeader("request");
		
		exchange.getIn().setHeader("existeEnMicuentasGmail", Boolean.TRUE);
		DatosMiCuentasGmailDTO dto = new DatosMiCuentasGmailDTO(request);
		exchange.getIn().setHeader("datosMiCuentasGmail", dto);
		logger.info(String.format("ArmaMiCuentasGmailFromRequest: leido: %s", dto));
	}

}
