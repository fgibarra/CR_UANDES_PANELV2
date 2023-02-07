package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.updateidgmail.UpdateIdGmailResponse;

public class ArmaResponse implements Processor {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer cuentasActualizadas = (Integer)exchange.getIn().getHeader("cuentasActualizadas");
		Integer codigo = (Integer)exchange.getIn().getHeader("codigo");
		String mensaje = (String)exchange.getIn().getHeader("mensaje");
		logger.info(String.format("recupera: cuentasActualizadas: %d codigo:%s mensaje: %s ",
				cuentasActualizadas, codigo!=null?codigo.toString():"NULO", mensaje!=null?mensaje:"NULO"));
		if (codigo == null) codigo = 0;
		if (mensaje == null) mensaje = "OK";
		
		UpdateIdGmailResponse response = new UpdateIdGmailResponse(codigo, mensaje, cuentasActualizadas);
		exchange.getIn().setBody(response);
	}

}
