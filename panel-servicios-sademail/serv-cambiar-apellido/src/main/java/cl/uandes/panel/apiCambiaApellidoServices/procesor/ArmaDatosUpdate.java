package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author fernando
 *
 * En header.new_login_name esta el nombre a usar para la cuenta; validado que no existe en Gmail
 */
public class ArmaDatosUpdate implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

	}

}
