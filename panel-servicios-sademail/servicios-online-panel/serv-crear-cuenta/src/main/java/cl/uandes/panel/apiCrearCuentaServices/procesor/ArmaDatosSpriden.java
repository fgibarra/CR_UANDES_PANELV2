package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosSpridenDTO;

public class ArmaDatosSpriden implements Processor {

	Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		logger.info(String.format("leidos desde SPRIDEN: %d", resultados.size()));
		if (resultados == null || resultados.isEmpty())
			exchange.getIn().setHeader("existeEnSpriden", Boolean.FALSE);
		else {
			exchange.getIn().setHeader("existeEnSpriden", Boolean.TRUE);
			DatosSpridenDTO dto = new DatosSpridenDTO(resultados.get(0));
			exchange.getIn().setHeader("datosSpriden", dto);
			logger.info(String.format("ArmaDatosSpriden: leido: %s", dto));
		}
	}

}
