package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.io.SequenceInputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class DefineResultadoActualizacionGmail implements Processor {

	Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// 
		Object obj = exchange.getIn().getBody();
		
		UserResponse response = null;
		if (obj instanceof UserResponse)
			response = (UserResponse) obj;
		if (obj instanceof ResponseImpl) {
			StringBuffer sb = new StringBuffer();
			ResponseImpl responseImpl = (ResponseImpl)obj;
			java.io.SequenceInputStream in = (SequenceInputStream) responseImpl.getEntity();
			
			int incc;
			while ((incc=in.read()) != -1)
				sb.append((char)incc);
			responseImpl.close();
			
			String jsonString = sb.toString();
//			logger.info(String.format("DefineResultadoActualizacionGmail: jsonString: %s", jsonString.toString()));

			response = (UserResponse) JSonUtilities.getInstance().json2java(jsonString, UserResponse.class, false);
//			logger.info(String.format("DefineResultadoActualizacionGmail: response: %s", response.toString()));
//			logger.info(String.format("DefineResultadoActualizacionGmail: response.getCodigo: %d", response.getCodigo()));
		}
		
		Integer codigo = null;
		if (response != null)
			codigo = response.getCodigo();
		else
			codigo = -1;
		
		exchange.getIn().setHeader("resultadoActualizacionGmail", codigo);

//		logger.info(String.format("DefineResultadoActualizacionGmail: resultadoActualizacionGmail: %d", exchange.getIn().getHeader("resultadoActualizacionGmail")));
	}

}
