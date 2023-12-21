package cl.uandes.panel.aeOwnersMembers.bean;

import org.apache.camel.Header;

import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersResponse;

public interface Mensajes {
	public AeOwnersMembersResponse generaResponse(@Header(value = "Operacion")String operacion, 
			@Header(value = "AeOwnersMembersRequest")AeOwnersMembersRequest request,
			@Header(value = "glosaWS_JBOSS")String glosa
			);
	public AeOwnersMembersResponse notFound(@Header(value = "Operacion")String operacion, 
			@Header(value = "AeOwnersMembersRequest")AeOwnersMembersRequest request
			);
	public AeOwnersMembersResponse connectError(@Header(value = "Operacion")String operacion, 
			@Header(value = "AeOwnersMembersRequest")AeOwnersMembersRequest request
			);
	
}
