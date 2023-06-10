package cl.uandes.panel.apiCrearCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosSpridenDTO;

/**
 * @author fernando
 *
 */
public class ArmaDatosCuentaDeSpriden implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Recive en ${header.datosSpriden} lo leido desde SPRIDEN
	 * Debe dejar en ${header.cuentaporciento} el loginName 
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		DatosSpridenDTO datosSpriden = (DatosSpridenDTO)exchange.getIn().getHeader("datosSpriden");
		exchange.getIn().setHeader("cuentaporciento", String.format("%s%c",datosSpriden.getLoginName(),'%'));
		logger.info(String.format("ArmaDatosCuentaDeSpriden: cuentaporciento: %s", exchange.getIn().getHeader("cuentaporciento")));
	}

}
