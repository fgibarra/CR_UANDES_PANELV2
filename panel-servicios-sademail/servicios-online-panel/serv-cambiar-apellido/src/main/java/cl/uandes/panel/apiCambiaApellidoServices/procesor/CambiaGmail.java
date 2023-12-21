package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMiCuentaGmailDTO;
import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.google.api.services.User;

/**
 * @author fernando
 *
 * Debe actualizar el usuario cuenta, nombres y apellidos. En caso de producirse un error debe 
 * arrojar la excepcion ActualizaGmailException
 * 
 * En header.DatosMiCuentaGmail_new estan los datos a actualizar
 * 
 */
public class CambiaGmail implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		DatosMiCuentaGmailDTO dto = (DatosMiCuentaGmailDTO)exchange.getIn().getHeader("DatosMiCuentaGmail_new");
		String email = dto.getLoginName();
		if (email.indexOf('@') < 0)
			email = String.format("%s@miuandes.cl", email);
		User user = new User(email, dto.getFullName(), dto.getNombres(), dto.getApellidos(),null);
		user.setId(dto.getIdGmail());
		UserRequest request = new UserRequest(user);
		logger.info(String.format("pide actualizar a: %s", request.toString()));
		exchange.getIn().setBody(request);
	}

}
