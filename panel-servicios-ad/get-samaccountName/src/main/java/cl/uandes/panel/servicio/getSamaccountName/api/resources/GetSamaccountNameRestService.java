package cl.uandes.panel.servicio.getSamaccountName.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.serviciosAD.CuentasADRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;

@Path("/")
public class GetSamaccountNameRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
     
    private String msgError;
	Logger logger = Logger.getLogger(getClass());

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/help")
	public Response getDocumentacion() {
		String type = "application/pdf";
		java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("documentacion.pdf");		
		Response response = Response.ok(is, type).build();
		return response;
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/get")
	public ServiciosLDAPResponse getSamaccountName(CuentasADRequest request) {
		return null;
	}
}
