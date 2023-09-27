package cl.uandes.panel.sincronizaGrupos.procesor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.sincronizaGrupos.dto.SincronizaGruposDTO;

public class ArmaDatosProceso implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		armaMiembrosGrupoPanel(exchange);
	}

	private void armaMiembrosGrupoPanel(Exchange exchange) {
		@SuppressWarnings("unchecked")
		Map<String, List<SincronizaGruposDTO>> mapXGrupo = (Map<String, List<SincronizaGruposDTO>>) exchange.getIn().getHeader("mapXGrupo");
		String grupo = getFirstKey(mapXGrupo);
		logger.info(String.format("ArmaDatosProceso: grupo a procesar %s", grupo));
		List<SincronizaGruposDTO> lista = mapXGrupo.remove(grupo);
		logger.info(String.format("ArmaDatosProceso: lista.size %d", lista.size()));
		
		exchange.getIn().setHeader("grupo", grupo);
		exchange.getIn().setHeader("listaCuentasGrupo", lista);
	}
	private String getFirstKey(Map<String, List<SincronizaGruposDTO>> mapXGrupo) {
		Set<String> keySet = mapXGrupo.keySet();
		if (keySet.isEmpty())
			return null;
		return keySet.toArray(new String[0])[0];
	}
}
