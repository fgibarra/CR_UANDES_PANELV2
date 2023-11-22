package cl.uandes.panel.servicio.crearCuentas.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.servicios.exceptions.KcoFuncionesException;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;

/**
 * Inicializa el proceso de creacion de cuentas
 * @author fernando
 *
 */
public class InicialiceCrearCuentas extends cl.uandes.panel.comunes.utils.RegistrosEnBD implements Processor {
	
	@EndpointInject(uri = "sql:classpath:sql/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate creaMiResultado;
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;
	@EndpointInject(uri = "sql:classpath:sql/actualizarKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarKcoFunciones;

	private Logger logger = Logger.getLogger(getClass());
	private ResultadoFuncion resultadoFuncion = null;
	
	/**
	 * Recupera los datos de la tabla KCO_FUNCIONES del sql y lo deja en el header en key 'DatosKcoFunciones'
	 * Si en los parametros viene que el servicio esta dehabilitado, deja 'estaInicializado' en false y en la 
	 * key 'resCrearCuenta' el ProcesoDiarioResponse
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>datos = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (datos != null && datos.size() > 0) {
			Map<String,Object> headers = exchange.getIn().getHeaders();
			DatosKcoFunciones data = new DatosKcoFunciones(datos.get(0));
			logger.info(String.format("InicialiceCrearCuentas: data: %s", data));
			setResultadoFuncion(inicialiceResultadoFuncion(exchange, data));
			logger.info(String.format("Almacenado: ResultadoFuncion %s", resultadoFuncion!=null?getResultadoFuncion():"GUARDO NULO"));
			headers.put("countProcesados", Integer.valueOf(0));
			headers.put("countErrores", Integer.valueOf(0));
			headers.put("countAgregadosBD", Integer.valueOf(0));
			headers.put("countAgregadosAD", Integer.valueOf(0));
			if (data.getParametros().getDisabled()) {
				// El proceso esta deshabilitado
				ProcesoDiarioResponse response = procesoDeshabilitado(data);
				headers.put("estaInicializado", "false");
				headers.put("resCrearCuenta", response);
			} else {
				headers.put("estaInicializado", "true");
			}
			headers.put("DatosKcoFunciones", data);
			headers.put("keyResultado", getResultadoFuncion().getKey());
			
			logger.info(String.format("estaInicializado: %s keyResultado: %d", 
					headers.get("estaInicializado"), headers.get("keyResultado")));
			actualizaInicio(data.getKey());
			return;
		}
		throw new KcoFuncionesException("No pudo recuperar datos desde tabla KCO_FUNCIONES");
	}

	public ResultadoFuncion getResultadoFuncion() {
		return resultadoFuncion;
	}

	public void setResultadoFuncion(ResultadoFuncion resultadoFuncion) {
		this.resultadoFuncion = resultadoFuncion;
	}

	@Override
	public ProducerTemplate getKey() {
		return getKey;
	}

	@Override
	public ProducerTemplate creaMiResultado() {
		return creaMiResultado;
	}

	@Override
	public ProducerTemplate insertMiResultadoErrores() {
		return insertMiResultadoErrores;
	}

	@Override
	public ProducerTemplate actualizarKcoFunciones() {
		return actualizarKcoFunciones;
	}
}
