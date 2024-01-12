package cl.uandes.panel.apiCrearCuentasServices.api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaResponse;
import cl.uandes.panel.comunes.json.creaCuenta.CrearCuentasRequest;
import cl.uandes.panel.comunes.json.creaCuenta.CrearCuentasResponse;
import cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta;

@Path("/")
public class CrearCuentasRestService {

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
    @Path("/crearCuentas")
	public CrearCuentasResponse creaCuenta(CrearCuentasRequest request) {
		CrearCuentasResponse response = valida(request);
		if (response == null) {
			Map<String,Object> headers = new HashMap<String,Object>();
			List<CreaCuentaRequest> lista = new ArrayList<CreaCuentaRequest>();
			if (request.getEnBanner() != null) {
				logger.info("creaCuenta: ruts leidos:");
				for (String rut : request.getEnBanner().getRuts()) {
					CreaCuentaRequest cta = new CreaCuentaRequest(Boolean.TRUE, rut, null, null, null, null);
					logger.info(String.format("%s", rut));
					lista.add(cta);
				}
			}
			if (request.getNoBanner() != null) {
				String password = request.getNoBanner().getPassword();
				for (DatosCrearCuenta cuenta : request.getNoBanner().getCuentas()) {
					CreaCuentaRequest cta = new CreaCuentaRequest(Boolean.FALSE, cuenta.getRut(), cuenta.getCuenta(), cuenta.getNombreCuenta(), cuenta.getApellidos(), password);
					lista.add(cta);
				}
			}
			headers.put("crearCuentas", lista);
			headers.put("crearCuentasResponses", new ArrayList<CreaCuentaResponse>());
			headers.put("email_informe", request.getEmailInforme());
			headers.put("countEnBanner", Integer.valueOf(0));
			headers.put("countNoBanner", Integer.valueOf(0));
			headers.put("errores", Integer.valueOf(0));
			return (CrearCuentasResponse)producer.requestBodyAndHeaders(request, headers);
		} else {
			return response;
		}
	}

	private CrearCuentasResponse valida(CrearCuentasRequest request) {
		if (request.getEmailInforme() == null) {
			return new CrearCuentasResponse(-1, "Debe indicar email al cual enviar el informe", null, null, null);
		}
		if (request.getEnBanner() != null) {
			if (request.getEnBanner().getRuts() == null || request.getEnBanner().getRuts().length == 0) {
				return new CrearCuentasResponse(-1, "Debe indicar un rut", null, null, null);
			}
		} 
		if (request.getNoBanner() != null) {
			if (request.getNoBanner().getCuentas() == null || request.getNoBanner().getCuentas().length == 0) {
				return new CrearCuentasResponse(-1, "Debe indicar cuentas no Banner a crear", null, null, null);
			}
			for (DatosCrearCuenta cuenta : request.getNoBanner().getCuentas()) {
				if (cuenta.getCuenta() == null || cuenta.getNombreCuenta() == null || cuenta.getApellidos() == null ) {
					return new CrearCuentasResponse(-1, "Debe indicar cuenta, nombres, apellidos para todas las cuentas No Banner a crear", null, null, null);
				}
			}
		}
		return null;
	}

	/* para crear el request
	public static void main(String[] args) {
		String ruts[] = {"11111111","6794383k"};
		cl.uandes.panel.comunes.json.creaCuenta.EnBanner enBanner = new cl.uandes.panel.comunes.json.creaCuenta.EnBanner(ruts);
		
		cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta cta1 = new cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta("12.678.234-9","pepito.perez","pepito antonio","perez rojas");
		cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta cta2 = new cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta("12.678.238-9","lolita.perez","Lolita antonio","perez rubilar");
		cl.uandes.panel.comunes.json.creaCuenta.DatosCrearCuenta cuentas[] = {cta1, cta2};
		cl.uandes.panel.comunes.json.creaCuenta.NoBanner noBanner = new cl.uandes.panel.comunes.json.creaCuenta.NoBanner(cuentas, "UAndes2023");
		CrearCuentasRequest request = new CrearCuentasRequest(enBanner, noBanner, "fernando.ibarra@fibarra.cl");
		
		System.out.println(request);
	}*/
}
