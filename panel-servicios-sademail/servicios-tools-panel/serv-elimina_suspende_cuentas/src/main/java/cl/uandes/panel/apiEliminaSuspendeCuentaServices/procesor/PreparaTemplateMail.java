package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class PreparaTemplateMail implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy", new Locale("es", "CL"));
		exchange.getIn().setHeader("fechaProceso", sdf.format(new java.util.Date()));
		Integer countSuspendidos = (Integer) exchange.getIn().getHeader("countSuspendidos");
		if (countSuspendidos == null) exchange.getIn().setHeader("countSuspendidos", Integer.valueOf(0));
		Integer countEliminados = (Integer) exchange.getIn().getHeader("countEliminados");
		if (countEliminados == null) exchange.getIn().setHeader("countEliminados", Integer.valueOf(0));
		Integer countReactivados = (Integer) exchange.getIn().getHeader("countReactivados");
		if (countReactivados == null) exchange.getIn().setHeader("countReactivados", Integer.valueOf(0));
		Integer countRecreados = (Integer) exchange.getIn().getHeader("countRecreados");
		if (countRecreados == null) exchange.getIn().setHeader("countRecreados", Integer.valueOf(0));
	}

}
