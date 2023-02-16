package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMailDTO;
import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMiCuentaGmailDTO;

/**
 * @author fernando
 *
 *         Utiliza camel-velocity para hacer el reemplazo de las partes
 *         variables del mail en su template
 */
public class PreparaTemplateMail implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy", new Locale("es", "CL"));
		exchange.getIn().setHeader("fechaProceso", sdf.format(new Date()));

		DatosMiCuentaGmailDTO old = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_old");
		DatosMiCuentaGmailDTO nuevo = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_new");

		List<DatosMailDTO> listaDatosMail = new ArrayList<DatosMailDTO>();
		exchange.getIn().setHeader("reporteList", listaDatosMail);
		// antes de
		listaDatosMail.add(new DatosMailDTO("Datos originales", old.getMoodleId(), old.getBannerPidm(),
				old.getLoginName(), old.getNombres(), old.getApellidos()));
		// despues de
		listaDatosMail.add(new DatosMailDTO("Datos modificados", nuevo.getMoodleId(), nuevo.getBannerPidm(),
				nuevo.getLoginName(), nuevo.getNombres(), nuevo.getApellidos()));

	}

}
