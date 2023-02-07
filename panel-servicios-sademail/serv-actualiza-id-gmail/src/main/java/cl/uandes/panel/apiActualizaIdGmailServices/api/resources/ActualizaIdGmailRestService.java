package cl.uandes.panel.apiActualizaIdGmailServices.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.updateidgmail.UpdateIdGmailResponse;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

/**
 * URI : cxf//ESB/panel/panelToolActualizaIdGmail/
 * @author fernando
 *
 */
@Path("/")
public class ActualizaIdGmailRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/procese")
	public UpdateIdGmailResponse procese() {
		
		return (UpdateIdGmailResponse)producer.requestBody(StringUtils.YYYYMMDDHHMMSS_DATE_FORMATTER.format(new java.util.Date()));
	}
}
