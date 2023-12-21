package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosRecrearDTO;
import cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor.exceptions.NotFoundBannerException;

public class RecuperaDatosRecrear implements Processor {

	Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		
		logger.info(String.format("leidos desde WRK_CUENTAS_OPERAR: %d", resultados.size()));
		
		List<DatosLeidosRecrearDTO> lista = new ArrayList<DatosLeidosRecrearDTO>();
		for (int i=0; i<resultados.size(); i++) {
			DatosLeidosRecrearDTO dto = new DatosLeidosRecrearDTO(resultados.get(i));
			lista.add(dto);
		}
		
		logger.info(String.format("process: leidos %d elementos", lista.size()));
			
		exchange.getIn().setHeader("listaCuentas", lista);
		exchange.getIn().setHeader("countRecreados", Integer.valueOf(0));
	}

}
