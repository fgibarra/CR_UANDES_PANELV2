package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.mail.json.SendmailRequest;
import cl.uandes.panel.comunes.json.cambiacuenta.CambiaCuentaRequest;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

/**
 * @author fernando
 *
 * Toma el cuerpo del mail desde el body donde lo dejo Velocity
 * Debe inicializar el SendmailRequest y dejarlo en el body para que el servicio que envia mail haga su trabajo
 */
public class PreparaDatosMail implements Processor {

	private Logger logger = Logger.getLogger(getClass());
    @PropertyInject(value = "serv.cambiaApellido.mail_to", defaultValue="fibarra.fir@gmail.com")
    private String para;
    @PropertyInject(value = "serv.cambiaApellido.mail_cc", defaultValue="fgibarra@miuandes.cl")
    private String cc;
    @PropertyInject(value = "serv.cambiaApellido.mail_asunto", defaultValue="Actualiza cuenta correo @miuandes.cl")
    private String asunto;

	@Override
	public void process(Exchange exchange) throws Exception {
		CambiaCuentaRequest req = (CambiaCuentaRequest)exchange.getIn().getHeader("CambiaCuentaRequest");
		if (req.getCuentasEnvio() != null)
			para = req.getCuentasEnvio();
		
		exchange.getIn().setHeader("To", para);
		exchange.getIn().setHeader("Subject", toIso88591(asunto));
		
		logger.info(String.format("PreparaDatosMail.process: To:%s Subject: %s", para, asunto));
	}


	Charset utf8charset = Charset.forName("UTF-8");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	private String toIso88591(String input) {
		ByteBuffer inputBuffer = ByteBuffer.wrap(input.getBytes());
		CharBuffer data = utf8charset.decode(inputBuffer);
		ByteBuffer outputBuffer = iso88591charset.encode(data);
		return new String(outputBuffer.array());
	}
}
