package cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto;

import javax.xml.bind.annotation.XmlRegistry;

import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.RequestOwners;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {
		super();
	}

	public PanelRequestTYPE createPanelRequestTYPE() {
		return new PanelRequestTYPE();
	}
	
	public PanelEsbResponseTYPE createPanelEsbResponseTYPE() {
		return new PanelEsbResponseTYPE();
	}

	public static PanelRequestTYPE factoryPanelRequestTYPE(AeOwnersMembersRequest req) {
		PanelRequestTYPE reqJBOSS = new PanelRequestTYPE();
		reqJBOSS.setServicio(req.getServicio());
		reqJBOSS.setCriterio(createRequestOwners(req.getCriterio()));
		return reqJBOSS;
	}

	private static String createRequestOwners(RequestOwners criterio) {
		RequestOwnersTYPE requestOwnersTYPE = new RequestOwnersTYPE(criterio.getFuncion(), 
				criterio.getProgramas(), 
				criterio.getCuentas());
		String xml = requestOwnersTYPE.toString();
		return String.format("<![CDATA[%s]]>", xml.substring(xml.indexOf("<requestOwners>")));
	}
/*	
	public static void main (String args[]) {
		String[] pr = new String[] {"uno", "dos", "tres"};
		String[] cu = new String[] {"c1@miuandes.cl", "c2@miuandes.cl"};
		
		RequestOwners req = new RequestOwners("ADD", java.util.Arrays.asList(pr), java.util.Arrays.asList(cu));
		System.out.println(req.toString());
		AeOwnersMembersRequest request = new AeOwnersMembersRequest("micuenta@miuandes.cl", "ownersGrupos", req);
		PanelRequestTYPE type = factoryPanelRequestTYPE(request);
		System.out.println(type.toString());
	}
	*/
}
