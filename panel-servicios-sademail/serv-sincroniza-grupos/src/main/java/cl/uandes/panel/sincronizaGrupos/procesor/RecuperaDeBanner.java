package cl.uandes.panel.sincronizaGrupos.procesor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.sincronizaGrupos.dto.SincronizaGruposDTO;

public class RecuperaDeBanner implements Processor {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new RuntimeException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		
		logger.info(String.format("leidos desde WRK_CUENTAS_OPERAR: %d", resultados.size()));
		Map<String, List<SincronizaGruposDTO>> mapXGrupo = new HashMap<String, List<SincronizaGruposDTO>>();
		for (int i=0; i<resultados.size(); i++) {
			SincronizaGruposDTO dto = new SincronizaGruposDTO(resultados.get(i));
			if (mapXGrupo.containsKey(dto.getGroupName())) {
				mapXGrupo.get(dto.getGroupName()).add(dto);
			} else {
				List<SincronizaGruposDTO> lista = new ArrayList<SincronizaGruposDTO>();
				lista.add(dto);
				mapXGrupo.put(dto.getGroupName(), lista);
			}
		}
		exchange.getIn().setHeader("mapXGrupo", mapXGrupo);
	}

}
