package cl.uandes.panel.aeOwnersMembers.bean;

import org.apache.camel.Header;

import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersResponse;

public class MensajesBean implements Mensajes {

	@Override
	public AeOwnersMembersResponse generaResponse(@Header(value = "Operacion")String operacion,
			@Header(value = "AeOwnersMembersRequest")AeOwnersMembersRequest request,
			@Header(value = "glosaWS_JBOSS")String glosa) {
		AeOwnersMembersResponse response = new AeOwnersMembersResponse(Integer.valueOf(-1), glosa);
		response.setCriterio(request.getCriterio());
		return response;
	}

	@Override
	public AeOwnersMembersResponse notFound(String operacion, AeOwnersMembersRequest request) {
		AeOwnersMembersResponse response = new AeOwnersMembersResponse(Integer.valueOf(-1), "No pudo recuperar datos desde WRK_OWNERS_GRUPOS");
		return response;
	}

	@Override
	public AeOwnersMembersResponse connectError(String operacion, AeOwnersMembersRequest request) {
		AeOwnersMembersResponse response = new AeOwnersMembersResponse(Integer.valueOf(-1), "Web Service en JBOSS no esta disponible");
		return response;
	}

}
