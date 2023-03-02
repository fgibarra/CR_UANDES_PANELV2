package cl.uandes.panel.aeOwnersMembers.procesor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.message.MessageContentsList;
import org.apache.log4j.Logger;

import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.PanelEsbResponseTYPE;

/**
 * @author fernando
 *
 * Debe:
 * - activar la ruta batch _esperaProceso para que continue con la generacion del mail cuando termine el
 *   proceso AsignarOwners / AgregarMiembroAGrupos en el ESB del JBOSS
 * - Recupera la respuesta que devolvio el WS del JBOSS, lo coloca en el header
 * - Deja en el header el request json que vino para generar la respuesta
 */
public class PreparaResponse implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		MessageContentsList list = (MessageContentsList)exchange.getIn().getBody();
		logger.info(String.format("list.size=%d", list.size()));
		PanelEsbResponseTYPE responseTYPE = (PanelEsbResponseTYPE) list.get(0);
		Integer codigo = responseTYPE.getCodigo();
		String glosa = responseTYPE.getGlosa();
		exchange.getIn().setHeader("glosaWS_JBOSS", glosa);
		
		// Activa la parte batch si el WS respondio que estaba trabajando
		if (codigo == 0) {
			CamelContext camelContext = exchange.getContext();
			ProducerTemplate apiProcesoBatch = camelContext.createProducerTemplate();
			apiProcesoBatch.requestBodyAndHeaders("seda:procesoBatch?timeout=-1",
					exchange.getIn().getBody(), exchange.getIn().getHeaders());
		}
	}

}
