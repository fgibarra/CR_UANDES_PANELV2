package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.velocity.VelocityConstants;
import org.apache.velocity.VelocityContext;

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
		Map<String, Object> variableMap = new HashMap<String, Object>();
		Map<String, Object> headersMap = new HashMap<String, Object>();
		variableMap.put("header", headersMap);
		variableMap.put("exchange", exchange);

		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy");
		headersMap.put("fechaProceso", sdf.format(new Date()));

		DatosMiCuentaGmailDTO old = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_old");
		DatosMiCuentaGmailDTO nuevo = (DatosMiCuentaGmailDTO) exchange.getIn().getHeader("DatosMiCuentaGmail_new");

		List<DatosMailDTO> listaDatosMail = new ArrayList<DatosMailDTO>();
		headersMap.put("reporteList", listaDatosMail);
		// antes de
		listaDatosMail.add(new DatosMailDTO("Datos originales", old.getMoodleId(), old.getBannerPidm(),
				old.getLoginName(), old.getNombres(), old.getApellidos()));
		// despues de
		listaDatosMail.add(new DatosMailDTO("Datos modificados", nuevo.getMoodleId(), nuevo.getBannerPidm(),
				nuevo.getLoginName(), nuevo.getNombres(), nuevo.getApellidos()));

		VelocityContext velocityContext = new VelocityContext(variableMap);
		exchange.getIn().setHeader(VelocityConstants.VELOCITY_CONTEXT, velocityContext);

	}

}
