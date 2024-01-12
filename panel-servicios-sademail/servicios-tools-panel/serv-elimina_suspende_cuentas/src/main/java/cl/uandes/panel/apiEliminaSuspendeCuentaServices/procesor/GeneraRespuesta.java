package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaResponse;

public class GeneraRespuesta implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String funcion = (String) exchange.getIn().getHeader("funcion");
		String msg = (String) exchange.getIn().getHeader("msgError");
		EliminaSuspendeCuentaResponse response = null;
		logger.info(String.format("funcion: %s msg: %s", funcion,msg!=null?msg:"ES NULO"));
		if (!"error".equalsIgnoreCase(funcion))
			response = new EliminaSuspendeCuentaResponse(0, "OK");
		else
			response = new EliminaSuspendeCuentaResponse(-1, msg);
		exchange.getIn().setBody(response);
	}

}
