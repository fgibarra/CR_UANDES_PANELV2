package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaResponse;

public class GeneraRespuesta implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer countEliminados = (Integer)exchange.getIn().getHeader("countEliminados");
		Integer countSuspendidos = (Integer)exchange.getIn().getHeader("countSuspendidos");
		EliminaSuspendeCuentaResponse response = new EliminaSuspendeCuentaResponse(0, "OK", countEliminados, countSuspendidos);
		
		exchange.getIn().setBody(response);
	}

}
