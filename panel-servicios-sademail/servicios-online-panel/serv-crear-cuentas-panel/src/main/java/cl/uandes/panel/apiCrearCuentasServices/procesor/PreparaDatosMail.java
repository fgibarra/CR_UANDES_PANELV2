package cl.uandes.panel.apiCrearCuentasServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

public class PreparaDatosMail implements Processor {

    @PropertyInject(value = "serv-crear-cuentas-panel.mail_to", defaultValue="fibarra.fir@gmail.com")
    private String para;
    @PropertyInject(value = "serv-crear-cuentas-panel.mail_cc", defaultValue="fgibarra@miuandes.cl")
    private String cc;
    @PropertyInject(value = "serv-crear-cuentas-panel.mail_asunto", defaultValue="Crea cuentas Panel")
    private String asunto;
    private Logger logger = Logger.getLogger(getClass());

    @Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeader("Subject", toIso88591(asunto));
		String correoInforme = (String)exchange.getIn().getHeader("email_informe");
		if (correoInforme != null) {
			logger.info(String.format("PreparaDatosMail: correoInforme=%s", correoInforme));
			exchange.getIn().setHeader("To", correoInforme);
		} else {
			exchange.getIn().setHeader("To", para);
		}
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
