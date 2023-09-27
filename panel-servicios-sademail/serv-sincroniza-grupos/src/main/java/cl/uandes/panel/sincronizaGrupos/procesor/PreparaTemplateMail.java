package cl.uandes.panel.sincronizaGrupos.procesor;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class PreparaTemplateMail implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy", new Locale("es", "CL"));
		exchange.getIn().setHeader("fechaProceso", sdf.format(new java.util.Date()));
		Integer countProcesados = (Integer) exchange.getIn().getHeader("countProcesados");
		if (countProcesados == null) exchange.getIn().setHeader("countProcesados", Integer.valueOf(0));
		Integer countAgregados = (Integer) exchange.getIn().getHeader("countAgregados");
		if (countAgregados == null) exchange.getIn().setHeader("countAgregados", Integer.valueOf(0));
	}

}
