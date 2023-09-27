package cl.uandes.panel.sincronizaGrupos.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.sincronizaGrupos.SincronizaGruposRequest;
import cl.uandes.panel.comunes.json.sincronizaGrupos.SincronizaGruposResponse;
import cl.uandes.panel.sincronizaGrupos.dto.SincronizaGruposDTO;

public class GeneraRespuesta implements Processor {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;
	String ruta = "seda:lupea";
	Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		Integer countProcesar = ((Map<String, List<SincronizaGruposDTO>>)exchange.getIn().getHeader("mapXGrupo")).size();
		SincronizaGruposRequest request = (SincronizaGruposRequest)exchange.getIn().getHeader("request");

		CamelContext camelContext = producer.getCamelContext();
		ProducerTemplate procesoBatch = camelContext.createProducerTemplate();
		Exchange pexchange = ExchangeBuilder.anExchange(camelContext).withHeader("mapXGrupo", exchange.getIn().getHeader("mapXGrupo"))
				.withBody(request).build();
		procesoBatch.asyncSend(ruta, pexchange);

		SincronizaGruposResponse response = new SincronizaGruposResponse(Integer.valueOf(0), 
				String.format("hay %d grupos en la lista para procesar", countProcesar));
		exchange.getIn().setBody(response);
	}

}
