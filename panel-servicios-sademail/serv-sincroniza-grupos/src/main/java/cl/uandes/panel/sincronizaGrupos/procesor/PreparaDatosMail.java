package cl.uandes.panel.sincronizaGrupos.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.sincronizaGrupos.SincronizaGruposRequest;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class PreparaDatosMail implements Processor {

    @PropertyInject(value = "serv-sincroniza-grupos.mail_to", defaultValue="fibarra.fir@gmail.com")
    private String para;
    @PropertyInject(value = "serv-sincroniza-grupos.mail_cc", defaultValue="fgibarra@miuandes.cl")
    private String cc;
    @PropertyInject(value = "serv-sincroniza-grupos.mail_asunto", defaultValue="Procesa Sincronizaci&o;acute grupos")
    private String asunto;
    private Logger logger = Logger.getLogger(getClass());
    
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeader("Subject", toIso88591(asunto));
		SincronizaGruposRequest request = (SincronizaGruposRequest)exchange.getIn().getHeader("request");
		if (request != null) {
			String correoInforme = request.getMailTO();
			logger.info(String.format("PreparaDatosMail: correoInforme=%s", correoInforme));
			if (correoInforme != null)
				exchange.getIn().setHeader("To", correoInforme);
			else
				exchange.getIn().setHeader("To", para);
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
