package cl.uandes.panel.aeOwnersMembers.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.RequestOwners;

public class PreparaDatosMail implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		AeOwnersMembersRequest request = (AeOwnersMembersRequest)exchange.getIn().getHeader("AeOwnersMembersRequest");
		
		String tipo = "";
		if ("addOwnersGrupos".equalsIgnoreCase(request.getServicio())) {
			tipo = "owners";
		} else if ("miembrosGrupos".equalsIgnoreCase(request.getServicio())) {
			tipo = "miembros";
		}
		RequestOwners criterio = request.getCriterio();
		String funcion = "";
		if ("ADD".equalsIgnoreCase(criterio.getFuncion()))
			funcion = "agregado";
		else if ("DEL".equalsIgnoreCase(criterio.getFuncion()))
			funcion = "sacado";
		
		exchange.getIn().setHeader("Subject", String.format("Se han %s %s de grupos", funcion, tipo));
		exchange.getIn().setHeader("To", request.getCuentasEnvio());
	}

}
