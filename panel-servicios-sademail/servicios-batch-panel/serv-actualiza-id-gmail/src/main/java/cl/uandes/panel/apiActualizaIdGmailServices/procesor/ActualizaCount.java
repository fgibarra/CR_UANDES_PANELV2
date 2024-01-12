package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ActualizaCount implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer cuentasActualizadas = (Integer)exchange.getIn().getHeader("cuentasActualizadas");
		cuentasActualizadas++;
		exchange.getIn().setHeader("cuentasActualizadas", cuentasActualizadas);
	}

}
