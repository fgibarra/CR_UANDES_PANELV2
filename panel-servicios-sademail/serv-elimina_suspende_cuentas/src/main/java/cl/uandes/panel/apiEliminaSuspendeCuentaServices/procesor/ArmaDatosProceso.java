package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiEliminaSuspendeCuentaServices.dto.DatosLeidosBannerDTO;

public class ArmaDatosProceso implements Processor {

	private Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		List<DatosLeidosBannerDTO> lista = (List<DatosLeidosBannerDTO>) exchange.getIn().getHeader("listaCuentas");
		DatosLeidosBannerDTO dto = lista.remove(0);
		exchange.getIn().setHeader("datosCuenta", dto);
		exchange.getIn().setHeader("operacion", dto.getFuncion());
		exchange.getIn().setHeader("spriden_id", dto.getSpridenId());
		
		//logger.info(String.format("funcion: %s datos: %s", dto.getFuncion(), dto.toString()));
	}

}
