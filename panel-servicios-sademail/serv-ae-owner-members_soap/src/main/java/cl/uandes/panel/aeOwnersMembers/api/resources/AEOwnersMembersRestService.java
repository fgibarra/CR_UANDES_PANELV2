package cl.uandes.panel.aeOwnersMembers.api.resources;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.ObjectFactory;
import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.PanelRequestTYPE;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersResponse;

/**
 * @author fernando
 *
 * Recibe el request json, 
 * - valida que vengan los campos obligatorios
 * - genera el request XML para enviarlo via SOAP al WS JBOSS y lo deja en el Body
 * - coloca en el header request JSON y operacion
 */
@Path("/")
public class AEOwnersMembersRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/pueblaWrkOwnersGrupos")
	public AeOwnersMembersResponse procesar(AeOwnersMembersRequest request) {

		if (request.getCuentasEnvio() == null || request.getCuentasEnvio().length() == 0)
			return new AeOwnersMembersResponse(Integer.valueOf(-1), "Debe ingresar correo para informar");

		if (request.getServicio() == null || request.getServicio().length() == 0)
			return new AeOwnersMembersResponse(Integer.valueOf(-1), "Debe ingresar servicio a ejecutar");

		String operacion = request.getServicio();
		if (!operacion.equalsIgnoreCase("ownersGrupos") && !operacion.equalsIgnoreCase("miembrosGrupos"))
			return new AeOwnersMembersResponse(Integer.valueOf(-1),
					String.format("servicios solicitado %s no es un servicio conocido", operacion));

		// Validado.
		// preparar el request para WS en JBOSS
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Operacion", request.getServicio());
		headers.put("AeOwnersMembersRequest", request);
		PanelRequestTYPE reqType = ObjectFactory.factoryPanelRequestTYPE(request);
		logger.info(String.format("AEOwnersMembersRestService: request para JBOSS: %s", reqType));
		
		String type = "Basic";
    	String username = "panelesb";
    	String password = "panel2019";
    	
        	String usernameAndPassword = username + ":" + password;
        	byte[] encodedAuth = Base64.encodeBase64(usernameAndPassword.getBytes(Charset.forName("US-ASCII")));
        	String authorizationHeaderValue = String.format("%s %s", type, new String(encodedAuth));
        	
		headers.put(HttpHeaders.AUTHORIZATION, authorizationHeaderValue);
		return (AeOwnersMembersResponse) producer.requestBodyAndHeaders(reqType, headers);
	}

}
