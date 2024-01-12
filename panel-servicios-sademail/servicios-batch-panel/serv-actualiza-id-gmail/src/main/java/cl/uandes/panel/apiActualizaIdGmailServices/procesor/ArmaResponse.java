package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.updateidgmail.UpdateIdGmailResponse;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

public class ArmaResponse implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	private final static String DIAHORA_DATE_PATTERN = "dd-MM-yy HH:mm:ss";
	private final static String HORA_DATE_PATTERN = "hh:mm:ss";
	private final static String MIN_DATE_PATTERN = "mm:ss";
	private final static DateFormat DIAHORA_DATE_FORMATTER = new SimpleDateFormat(DIAHORA_DATE_PATTERN);
	private final static DateFormat HORA_DATE_FORMATTER = new SimpleDateFormat(HORA_DATE_PATTERN);
	private final static DateFormat MIN_DATE_FORMATTER = new SimpleDateFormat(MIN_DATE_PATTERN);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Integer cuentasActualizadas = (Integer)exchange.getIn().getHeader("cuentasActualizadas");
		String shoraComienzo = (String)exchange.getIn().getHeader("horaComienzo");
		logger.info(String.format("recupera: shoraComienzo: %s ",
				shoraComienzo));
		java.util.Date horaComienzo = StringUtils.YYYYMMDDHHMMSS_DATE_FORMATTER.parse(shoraComienzo);
		java.util.Date horaTermino = new java.util.Date();
		java.util.Date duracion;
		Integer codigo = (Integer)exchange.getIn().getHeader("codigo");
		String mensaje = (String)exchange.getIn().getHeader("mensaje");
		String sHoraComienzo;
		String sDuracion;
		
		if (horaComienzo != null) {
			sHoraComienzo = DIAHORA_DATE_FORMATTER.format(horaComienzo);
			long d = horaTermino.getTime() - horaComienzo.getTime();
			logger.info(String.format("duracion: %d", d));
			duracion = new java.util.Date();
			duracion.setTime(d);
			if (d < 3600000)
				sDuracion = MIN_DATE_FORMATTER.format(duracion);
			else
				sDuracion = HORA_DATE_FORMATTER.format(duracion);
		} else {
			sHoraComienzo = "??:??:??";
			sDuracion = "??";
		}
		logger.info(String.format("recupera: cuentasActualizadas: %d codigo:%s mensaje: %s ",
				cuentasActualizadas, codigo!=null?codigo.toString():"NULO", mensaje!=null?mensaje:"NULO"));
		if (codigo == null) codigo = 0;
		if (mensaje == null) mensaje = "OK";
		
		UpdateIdGmailResponse response = new UpdateIdGmailResponse(codigo, mensaje, cuentasActualizadas,
				sHoraComienzo, DIAHORA_DATE_FORMATTER.format(horaTermino), 
				sDuracion);
		exchange.getIn().setBody(response);
	}

}
