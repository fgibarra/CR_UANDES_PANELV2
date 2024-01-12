package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;

public class ArmaDatosMiCuentasGmail implements Processor {

	Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		logger.info(String.format("leidos desde MI_CUENTAS_GMAIL: %d", resultados.size()));
		if (resultados == null || resultados.isEmpty())
			exchange.getIn().setHeader("existeEnMicuentasGmail", Boolean.FALSE);
		else {
			exchange.getIn().setHeader("existeEnMicuentasGmail", Boolean.TRUE);
			DatosMiCuentasGmailDTO dto = new DatosMiCuentasGmailDTO(resultados.get(0));
			exchange.getIn().setHeader("datosMiCuentasGmail", dto);
			logger.info(String.format("ArmaDatosMiCuentasGmail: leido: %s", dto));
		}
	}

}
