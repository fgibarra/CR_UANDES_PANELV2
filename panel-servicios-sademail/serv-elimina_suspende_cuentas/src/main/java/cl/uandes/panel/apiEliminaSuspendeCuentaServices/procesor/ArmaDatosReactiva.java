package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosReactivarBannerDTO;
import cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor.exceptions.NotFoundBannerException;

public class ArmaDatosReactiva implements Processor {

	Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		
		logger.info(String.format("leidos desde WRK_CUENTAS_OPERAR: %d", resultados.size()));
		
		List<DatosReactivarBannerDTO> lista = new ArrayList<DatosReactivarBannerDTO>();
		for (int i=0; i<resultados.size(); i++) {
			DatosReactivarBannerDTO dto = new DatosReactivarBannerDTO(resultados.get(i));
			lista.add(dto);
		}
		logger.info(String.format("process: leidos %d elementos", lista.size()));
		
		exchange.getIn().setHeader("listaCuentas", lista);
		exchange.getIn().setHeader("countEliminados", Integer.valueOf(0));
		exchange.getIn().setHeader("countSuspendidos", Integer.valueOf(0));
		exchange.getIn().setHeader("countReactivados", Integer.valueOf(0));
		exchange.getIn().setHeader("countRecreados", Integer.valueOf(0));
	}

}
