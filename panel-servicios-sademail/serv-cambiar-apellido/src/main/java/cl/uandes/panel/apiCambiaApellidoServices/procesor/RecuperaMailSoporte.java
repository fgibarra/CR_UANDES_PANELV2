package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCambiaApellidoServices.procesor.exceptions.NotFoundBannerException;

/**
 * @author fernando
 *
 * Recupera los datos de qry: select email_support from kco_sendmail_params where key={{sendmail_params_id}}?dataSource=#bannerDataSource
 * y lo deja en el header emailTO
 */
public class RecuperaMailSoporte implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		String cuentasEnvio = (String)((Map<String,Object>)resultados.get(0)).get("sendmail_params_id");
		logger.info(String.format("process: cuentasEnvio: %s", cuentasEnvio));
		exchange.getIn().setHeader("", cuentasEnvio);
	}

}
