package cl.uandes.panel.apiCrearCuentasServices.procesor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentasServices.dto.Reporte;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;

public class PreparaTemplateMail implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy", new Locale("es", "CL"));
		exchange.getIn().setHeader("fechaProceso", sdf.format(new java.util.Date()));
		
		List<CreaCuentaResponse> lista = (List<CreaCuentaResponse>) exchange.getIn().getHeader("crearCuentasResponses");
		List<Reporte> listaReporte = new ArrayList<Reporte>();
		for (CreaCuentaResponse response : lista) {
			Reporte reporte = new Reporte(response);
			logger.info(String.format("PreparaTemplateMail: reporte %s", reporte));
			listaReporte.add(reporte);
		}
		
		exchange.getIn().setHeader("reporteList", listaReporte);
		
	}

}
