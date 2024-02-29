package cl.uandes.panel.apiCambiaApellidoServices.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.CambiaCuentaRequest;
import cl.uandes.panel.comunes.json.cambiacuenta.CambiaCuentaResponse;

@Path("/")
public class CambiaApellidoRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/cambiaCuenta")
	public CambiaCuentaResponse procese(CambiaCuentaRequest in_msg) {
		String operacion;
		
		if (in_msg.getRut() == null)
			return new CambiaCuentaResponse(Integer.valueOf(-1), "Debe ingresar RUT",null, null);
		else if (in_msg.getNuevoNombreCuenta() != null) {
			operacion = "porCuenta";
		} else
			operacion = "porRUT";

		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", operacion);
		headers.put("CambiaCuentaRequest", in_msg);
		return (CambiaCuentaResponse)producer.requestBodyAndHeaders(in_msg, headers);
		/*
		CambiaCuentaResponse response = new CambiaCuentaResponse(0, "OK", 
				new cl.uandes.panel.comunes.json.cambiacuenta.DatosCuenta("6794383K", 123456l, "antigua", "antiguoN", "atiguoAp", "idgmail"),
				new cl.uandes.panel.comunes.json.cambiacuenta.DatosCuenta("6794383K", 123456l, "nueva", "nuevoN", "nuevoAp", "idgmail")
				);
		return response;
		*/
	}

}
