package cl.uandes.panel.apiActualizaIdGmailServices.procesor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
//import org.apache.log4j.Logger;

public class ArmaUpdate implements Processor {

	//private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getHeader("listaCuentas");
		Map<String, Object> map = resultados.remove(0);
		BigDecimal key = (BigDecimal) map.get("KEY");
		String loginName = (String)map.get("LOGIN_NAME");
		String idGmail = (String)map.get("ID_GMAIL");
		//logger.info(String.format("key: %s loginName: %s idGmail: %s", key, loginName, idGmail));
		exchange.getIn().setHeader("key",key);
		exchange.getIn().setHeader("loginName",loginName);
		exchange.getIn().setHeader("idGmail", idGmail);		
		String qry="update mi_cuentas_gmail set id_gmail=:?idGmail where key=:?key";
		exchange.getIn().setBody(qry);
	}

}
