package cl.uandes.panel.servicio.getSamaccountName.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.serviciosAD.CuentasADRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;
import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;

@Path("/")
public class GetSamaccountNameRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
     
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
		CuentasADDTO dto = new CuentasADDTO();
		dto.setApellidos(request.getApellidos());
		dto.setComuna(request.getComuna());
		dto.setDireccion(request.getDireccion());
		dto.setEmail(request.getEmail());
		dto.setMiddleName(request.getMiddleName());
		dto.setNombre(request.getNombre());
		dto.setNombres(request.getNombres());
		dto.setPassword(dto.getPassword());
		dto.setRama(request.getRama());
		dto.setRut(request.getRut());
		try {
			return (ServiciosLDAPResponse)producer.requestBodyAndHeader(request, "CuentasADDTO", dto);
		} catch (CamelExecutionException e) {
			logger.error("GetSamaccountNameRestService: error");
			return new ServiciosLDAPResponse(-1, e.getMessage());
		}
	}
}
