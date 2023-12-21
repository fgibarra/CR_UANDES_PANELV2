package cl.uandes.panel.servicio.sincronizarGrupos.procesor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarGrupos;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;

@Deprecated
public class InicialiceSincronizarGrupos implements Processor {

	@EndpointInject(uri = "sql:classpath:sql/getKcoFuncion.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKcoFuncion;

	@EndpointInject(uri = "sql:classpath:sql/updateKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateKcoFunciones;

	@EndpointInject(uri = "direct:getKey") // recupera SEQUENCE para key
	ProducerTemplate routeGetkey;
	@EndpointInject(uri = "direct:creaResultado") // crea resultado
	ProducerTemplate creaResultado;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultado;

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		
		String proceso = (String) message.getHeader("proceso");
		
		// actualizar la partida
		DatosKcoFunciones data = getDatosKcoFunciones(proceso);
		// iniciar mi_resultados
		logger.info(String.format("InicialiceCrearGrupos: actualiza partida del proceso %s", exchange.getIn().getHeader("proceso")));
		updateKcoFunciones.requestBodyAndHeaders(null, exchange.getIn().getHeaders());

		List<?> resRoute = (List<?>)routeGetkey.requestBody(null);
		//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", resRoute, resRoute!=null?resRoute.getClass().getName():"es NULO"));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)resRoute.get(0);
		//logger.info(String.format("InicialiceCrearGrupos: map: %s clase: %s", map, map!=null?map.getClass().getName():"es NULO"));
		BigDecimal valor = (BigDecimal)map.get("NEXTVAL");
		//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", valor, valor!=null?valor.getClass().getName():"es NULO"));
		
		// inicializa el resultado
		ResultadoFuncion res = ObjectFactory.createResultadoFuncion(data, valor);
		
		//logger.info(String.format("Antes de creaResultado: body: %s",getQryInsertResultadoFuncion()));
		Map<String, Object> headers = new HashMap<String,Object>();
		headers.put("key", BigDecimal.valueOf(res.getKey().longValue()));
		headers.put("funcion", res.getFuncion());
		headers.put("horaComienzo", res.getHoraComienzo());
		headers.put("minThreads", BigDecimal.valueOf(res.getMinThreads().longValue()));
		headers.put("maxThreads", BigDecimal.valueOf(res.getMaxThreads().longValue()));
		insertMiResultado.requestBodyAndHeaders(null, headers);

		message.setHeader("keyResultado", valor);
		message.setHeader("tipoGrupo", data.getParametros().getTipoGrupo());
		message.setHeader("DatosKcoFunciones", data);
		message.setHeader("ResultadoFuncion", res);
		message.setHeader("contadoresSincronizarGrupos", new ContadoresSincronizarGrupos(proceso,0,0,0));
		message.setHeader("key", BigDecimal.valueOf(res.getKey().longValue()));
		
	}

	private DatosKcoFunciones getDatosKcoFunciones(String proceso) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("proceso", proceso);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) getKcoFuncion.requestBodyAndHeaders(null, headers);
		DatosKcoFunciones data = new DatosKcoFunciones(datos.get(0));
		updateKcoFunciones.requestBodyAndHeaders(null, headers);
		return data;
	}

}
