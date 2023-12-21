package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosBannerDTO;
import cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor.exceptions.NotFoundBannerException;
import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaRequest;

public class RecuperaDeBanner implements Processor {

    @PropertyInject(value = "serv-elimina-suspende-cuentas.maxUso")
	private String maxUso;
    
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		
		EliminaSuspendeCuentaRequest request = (EliminaSuspendeCuentaRequest)exchange.getIn().getHeader("EliminaSuspendeCuentaRequest");
		if (request.getUso() != null && request.getUso() > 0)
			setMaxUso(String.format("%d", request.getUso()));

		logger.info(String.format("process: maxUso: %s leido desde %s", maxUso, request.getUso() != null?"REQUEST":"Propiedades"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		
		logger.info(String.format("leidos desde WRK_CUENTAS_OPERAR: %d", resultados.size()));
		
		List<DatosLeidosBannerDTO> lista = new ArrayList<DatosLeidosBannerDTO>();
		for (int i=0; i<resultados.size(); i++) {
			DatosLeidosBannerDTO dto = new DatosLeidosBannerDTO(resultados.get(i), Integer.valueOf(getMaxUso()));
			/*logger.info(String.format("RecuperaDeBanner: esProcesable=%b uso=%d leido: %s", 
					dto.esProcesable(), dto.getUsado(), dto));*/
			if (dto.esProcesable())
				lista.add(dto);
			/*
			else
				logger.info(String.format("process: %s NO se procesa", dto.toString()));
				*/
		}
		
		logger.info(String.format("process: leidos %d elementos", lista.size()));
			
		exchange.getIn().setHeader("listaCuentas", lista);
		exchange.getIn().setHeader("countEliminados", Integer.valueOf(0));
		exchange.getIn().setHeader("countSuspendidos", Integer.valueOf(0));
		exchange.getIn().setHeader("countReactivados", Integer.valueOf(0));
	}

	public String getMaxUso() {
		return maxUso;
	}

	public void setMaxUso(String maxUso) {
		this.maxUso = maxUso;
	}

}
