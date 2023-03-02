package cl.uandes.panel.aeOwnersMembers.procesor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import cl.uandes.panel.aeOwnersMembers.procesor.dto.ReporteDTO;
import cl.uandes.panel.aeOwnersMembers.procesor.exceptions.NotFoundBannerException;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.aeOwnersMembers.RequestOwners;

public class RecuperaWrkOwnersGrupos implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		
		List<ReporteDTO> lista = new ArrayList<ReporteDTO>();
		for (Map<String, Object> map : resultados)
			lista.add(new ReporteDTO(map));
		
		exchange.getIn().setHeader("reporteList", lista);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE dd MMMMMMMMMMM yyyy", new Locale("es", "CL"));
		exchange.getIn().setHeader("fechaProceso", sdf.format(new Date()));

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
		
		exchange.getIn().setHeader("operacion", String.format("%s %s", funcion, tipo));
		
	}

}
