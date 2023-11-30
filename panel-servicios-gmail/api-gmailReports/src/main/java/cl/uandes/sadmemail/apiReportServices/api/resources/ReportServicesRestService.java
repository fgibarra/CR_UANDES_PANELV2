package cl.uandes.sadmemail.apiReportServices.api.resources;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.report.json.ReportResponse;

@Path("/")
public class ReportServicesRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	@EndpointInject(uri = "sql:classpath:sql/qrySendmailParams.sql?dataSource=#bannerDataSource")
	ProducerTemplate qrySendMailParams;

	Logger logger = Logger.getLogger(getClass());

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/report/usage/{userId}")
	public ReportResponse retrieveUser(@PathParam("userId")String in_msg) {
		logger.info(String.format("retrieveUser: in_msg: %s - %s",in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "report-usage");
		headers.put("Body", in_msg);
		return (ReportResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}

	@SuppressWarnings("unchecked")
	public void inicialiceParametrosMail(Exchange exchange) {
		Message message = exchange.getIn();
		Map<String, Object> headers = (Map<String, Object>)message.getHeaders();
		
		List<Map <String, Object>> datos = (List<Map <String, Object>>)qrySendMailParams.requestBodyAndHeader(null, "key", BigDecimal.ONE);
		if (datos != null && datos.size() > 0) {
			Map<String, Object> data = datos.get(0);
			headers.putAll(data);
		}
	}
}
