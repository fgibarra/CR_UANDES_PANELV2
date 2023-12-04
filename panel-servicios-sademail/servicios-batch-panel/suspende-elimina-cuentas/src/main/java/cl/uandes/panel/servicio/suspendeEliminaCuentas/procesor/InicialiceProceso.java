package cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarCuentas;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.servicios.exceptions.KcoFuncionesException;
import cl.uandes.panel.comunes.utils.RegistrosEnBD;

public class InicialiceProceso extends RegistrosEnBD implements Processor {

	@EndpointInject(uri = "sql:classpath:sql/getKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKcoFunciones;

	@EndpointInject(uri = "sql:classpath:sql/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;
	
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate creaMiResultado;
	
	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;
	
	@EndpointInject(uri = "sql:classpath:sql/actualizarKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate actualizarKcoFunciones;

	private ResultadoFuncion resultadoFuncion = null;

	/**
	 * Lee datos de KCO_FUNCIONES para cargar parametros
	 * Inicializa MI_RESULTADO para registrar la salida
	 * @param exchange
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Map<String,Object> headers = exchange.getIn().getHeaders();
		String proceso = (String)message.getHeader("proceso");
		logger.info(String.format("InicialiceProceso: proceso=%s", proceso));
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>datos = (List<Map<String, Object>>)getKcoFunciones.requestBodyAndHeader(null, "proceso", proceso);
		if (datos != null && datos.size() > 0) {
			DatosKcoFunciones data = new DatosKcoFunciones(datos.get(0));
			logger.info(String.format("InicialiceProceso: data: %s", data));
			setResultadoFuncion(inicialiceResultadoFuncion(exchange, data));
			logger.info(String.format("InicialiceProceso: ResultadoFuncion %s", resultadoFuncion!=null?getResultadoFuncion():"GUARDO NULO"));
			headers.put("contadoresSincronizarCuentas", new ContadoresSincronizarCuentas(proceso));
			if (data.getParametros().getDisabled()) {
				// El proceso esta deshabilitado
				ProcesoDiarioResponse response = procesoDeshabilitado(data);
				headers.put("estaInicializado", "false");
				headers.put("resSincCuenta", response);
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
