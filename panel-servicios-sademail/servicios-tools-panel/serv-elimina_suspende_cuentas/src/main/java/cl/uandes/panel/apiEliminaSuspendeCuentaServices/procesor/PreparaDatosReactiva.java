package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaRequest;

public class PreparaDatosReactiva implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		EliminaSuspendeCuentaRequest request = (EliminaSuspendeCuentaRequest)exchange.getIn().getHeader("EliminaSuspendeCuentaRequest");
		List<String> listaRuts = new ArrayList<String>();
		if (request.getListaRuts() != null && request.getListaRuts().length > 0) {
			listaRuts = Arrays.asList(request.getListaRuts());
		}
		exchange.getIn().setHeader("ruts", listaRuts);
		logger.info(String.format("PreparaDatosReactiva: ruts.size = %d", listaRuts.size()));
	}

}
