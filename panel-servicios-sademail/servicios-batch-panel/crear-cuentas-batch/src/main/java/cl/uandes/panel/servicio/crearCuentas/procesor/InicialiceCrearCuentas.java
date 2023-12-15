package cl.uandes.panel.servicio.crearCuentas.procesor;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;

/**
 * Inicializa el proceso de creacion de cuentas
 * @author fernando
 *
 */
public class InicialiceCrearCuentas implements Processor {
	private RegistrosComunes registraInicio;

	private ResultadoFuncion resultadoFuncion = null;
	
	/**
	 * Recupera los datos de la tabla KCO_FUNCIONES del sql y lo deja en el header en key 'DatosKcoFunciones'
	 * Si en los parametros viene que el servicio esta dehabilitado, deja 'estaInicializado' en false y en la 
	 * key 'resCrearCuenta' el ProcesoDiarioResponse
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Boolean inicializado = Boolean.FALSE;
		
		Map<String, Object> datosInicializacion = registraInicio.inicializar(exchange);
		if (datosInicializacion != null) {
			DatosKcoFunciones data = (DatosKcoFunciones) datosInicializacion.get("DatosKcoFunciones");
			ResultadoFuncion res = (ResultadoFuncion) datosInicializacion.get("ResultadoFuncion");
			
			message.setHeader("DatosKcoFunciones", data);
			message.setHeader("ResultadoFuncion", res);
			message.setHeader("contadoresCrearGrupos", new ContadoresCrearGrupos(0,0,0,0,0,0,0,0,0,0));
			message.setHeader("key", BigDecimal.valueOf(res.getKey().longValue()));		
			
			inicializado = Boolean.TRUE;
		}
		
		message.setHeader("inicializado", inicializado);
	}

	public ResultadoFuncion getResultadoFuncion() {
		return resultadoFuncion;
	}

	public void setResultadoFuncion(ResultadoFuncion resultadoFuncion) {
		this.resultadoFuncion = resultadoFuncion;
	}
	public RegistrosComunes getRegistraInicio() {
		return registraInicio;
	}

	public void setRegistraInicio(RegistrosComunes registraInicio) {
		this.registraInicio = registraInicio;
	}
}
