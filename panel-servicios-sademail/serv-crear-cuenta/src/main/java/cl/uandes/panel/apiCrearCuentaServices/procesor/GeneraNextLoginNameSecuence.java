package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosSpridenDTO;

public class GeneraNextLoginNameSecuence implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Integer lastSecuence = Integer.valueOf(0);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		logger.info(String.format("GeneraNextLoginNameSecuence: resultados class: %s length: %d", resultados.getClass().getName(), resultados.size()));
		if (resultados != null && resultados.size() > 0) {
			for (Map<String,Object> lastLoginNameDS : resultados) {
				String lastLoginName = (String)lastLoginNameDS.get("LOGIN_NAME");
				String cuenta = ((DatosSpridenDTO)exchange.getIn().getHeader("datosSpriden")).getLoginName();
				logger.info(String.format("GeneraNextLoginNameSecuence: lastLoginName: %s cuenta: %s", lastLoginName, cuenta));
				if (lastLoginName.length() > cuenta.length()) {
					String valor = lastLoginName.substring(cuenta.length());
					if (valor.matches("[0-9]*")) {
						Integer seq =  Integer.valueOf(valor)+1;
						if (seq > lastSecuence)
							lastSecuence = seq;
					}
				}
			}
		}
		exchange.getIn().setHeader("lastSecuence", lastSecuence);
		logger.info(String.format("GeneraNextLoginNameSecuence: lastSecuence: %d", lastSecuence));
	}

}
