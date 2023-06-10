package cl.uandes.panel.apiCrearCuentasServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.comunes.json.creaCuenta.CrearCuentasResponse;

public class PreparaResponse implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer countEnBanner = (Integer)exchange.getIn().getHeader("countEnBanner");
		Integer countNoBanner = (Integer)exchange.getIn().getHeader("countNoBanner");
		Integer errores = (Integer)exchange.getIn().getHeader("errores");
		CrearCuentasResponse response = new CrearCuentasResponse(0, "OK", countEnBanner, countNoBanner, errores);
		exchange.getIn().setBody(response);
	}

}
