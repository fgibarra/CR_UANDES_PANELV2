package cl.uandes.panel.paraGui.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.paraGui.json.GuiResponse;
import cl.uandes.panel.paraGui.json.LastResponse;

@Path("/")
public class GuiRestService {

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

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/lastUsoOf/{funcion}")
	public LastResponse lastUsoOf(@PathParam("funcion")String in_msg) {
		logger.info(String.format("lastUsoOf: funcion=%s", in_msg));
		LastResponse response = producer.requestBody("direct:lastUso", in_msg, LastResponse.class);
		logger.info(String.format("lastUsoOf: response=%s", response));
		return response;
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/actualizaConsumosPanel/{id}")
	public Response actualizaConsumosPanel(@PathParam("id")String in_msg) {
		Response response = null;
		Status status;
		String mensaje = null;
		if ("1".equals(in_msg)) {
			GuiResponse respuesta = producer.requestBody("direct:actualizaConsumoPanel",null, GuiResponse.class);
			mensaje = respuesta.getMensaje();
			if (respuesta.getCodigo() == 0) {
				status = Response.Status.ACCEPTED;
			} else {
				status = Response.Status.BAD_REQUEST;
			}
		} else {
			status = Response.Status.BAD_REQUEST;
			
		}
		response = Response.status(status).entity(mensaje).build();
		return response;
	}
}
