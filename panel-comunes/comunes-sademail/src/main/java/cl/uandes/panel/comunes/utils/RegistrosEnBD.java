package cl.uandes.panel.comunes.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;


/**
 * Metodos comunes a los servicios para registrar resultados en la BD
 * @author fernando
 *
 */
public abstract class RegistrosEnBD {

	public abstract ProducerTemplate getKey();
	public abstract ProducerTemplate creaMiResultado();
	public abstract ProducerTemplate insertMiResultadoErrores();
	public abstract ProducerTemplate actualizarKcoFunciones();
	
	protected Logger logger = Logger.getLogger(getClass());
	/**
	 * @param exchange Mensaje en el process
	 * @param data DatosKcoFunciones
	 * @return ResultadoFuncion incluido key de la fila o null
	 */
	public ResultadoFuncion inicialiceResultadoFuncion(Exchange exchange, DatosKcoFunciones data) {
		
		logger.info(String.format("inicialiceResultadoFuncion: DatosKcoFunciones: %s",
				data!=null?data:"es NULO"));
		if (data == null) return null;
		if (getKey() == null) throw new RuntimeException ("El ProducerTemplate getKey no esta instanciado");
		if (creaMiResultado() == null) throw new RuntimeException ("El ProducerTemplate creaMiResultado no esta instanciado");
		
		List<?> resRoute = (List<?>)getKey().requestBody(null);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)resRoute.get(0);
		logger.info(String.format("key: %s", map!=null?map.keySet().toArray()[0]:"NULO"));
		BigDecimal valor = (BigDecimal)map.get("NEXTVAL");
		logger.info((String.format("valor: %s", valor!=null?valor.toString():"NULO")));
		// inicializa el resultado
		ResultadoFuncion res = ObjectFactory.createResultadoFuncion(data, valor);
		logger.info(String.format("ResultadoFuncion: %s", res.toString()));
		
		Map<String, Object> headers = new HashMap<String,Object>();
		headers.put("key", BigDecimal.valueOf(res.getKey().longValue()));
		headers.put("funcion", res.getFuncion());
		headers.put("horaComienzo", res.getHoraComienzo());
		headers.put("minThreads", BigDecimal.valueOf(res.getMinThreads().longValue()));
		headers.put("maxThreads", BigDecimal.valueOf(res.getMaxThreads().longValue()));
		Object resCrea = creaMiResultado().requestBodyAndHeaders(null, headers);
		logger.info(String.format("inicialiceResultadoFuncion: ResultadoFuncion: %s resultado insert: %s clase: %s",
				res.toString(), resCrea, resCrea!=null?resCrea.getClass().getName():"es NULO"));
		
		return res;
		
	}

	public ProcesoDiarioResponse procesoDeshabilitado(DatosKcoFunciones data) {
		// Actualizar hora de termino
		actualizaHoraTermino(data.getKey());
		ProcesoDiarioResponse response = new ProcesoDiarioResponse(0, data.getFuncion(), "El proceso esta deshabilitado en KCO_FUNCIONES", null);
		return response;
	}
	
	public void actualizaHoraTermino(Integer keyKcoFunciones) {
		if (actualizarKcoFunciones() == null) throw new RuntimeException ("El ProducerTemplate actualizarKcoFunciones no esta instanciado");
		logger.info(String.format("actualizaHoraTermino: en tabla KCO_FUNCIONES key=%d", keyKcoFunciones));
		actualizarKcoFunciones().requestBodyAndHeader(null, "key", ObjectFactory.toBigDecimal(keyKcoFunciones));
	}
	
	public void registraError(String args[], Integer keyResultado) {
		if (insertMiResultadoErrores() == null) throw new RuntimeException ("El ProducerTemplate insertMiResultadoErrores no esta instanciado");
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<args.length; i++)
			sb.append(args[i]);
		logger.info(String.format("registraError: args: |%s|",sb.toString()));
		CommandLineParser parser = new CommandLineParser(args);
		Map<String, Object> headers = new HashMap<String, Object> ();
		headers.put("ID_USUARIO", parser.getValue("idUsuario"));
		headers.put("TIPO", parser.getValue("tipo"));
		headers.put("CAUSA", parser.getValue("causa"));
		headers.put("KEY_GRUPO", ObjectFactory.toBigDecimal(parser.getValue("keyGrupo")));
		headers.put("KEY_RESULTADO", ObjectFactory.toBigDecimal(keyResultado));
		insertMiResultadoErrores().requestBodyAndHeaders(null, headers);
	}
}
