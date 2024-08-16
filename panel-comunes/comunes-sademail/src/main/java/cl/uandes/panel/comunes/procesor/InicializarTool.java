package cl.uandes.panel.comunes.procesor;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.CountThreads;

public class InicializarTool implements Processor {

	private RegistrosComunes registraInicio;
	private String proceso = null;
	private ResultadoFuncion resultadoFuncion = null;
	
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Boolean inicializado = Boolean.FALSE;
		if (getProceso() == null)
			throw new RuntimeException("No se define el nombre del tool a usar");
		message.setHeader("proceso", getProceso());
		Map<String, Object> datosInicializacion = registraInicio.inicializarTool(exchange);
		if (datosInicializacion != null) {
			ResultadoFuncion res = (ResultadoFuncion) datosInicializacion.get("ResultadoFuncion");
			
			message.setHeader("ResultadoFuncion", res);
			message.setHeader("key", BigDecimal.valueOf(res.getKey().longValue()));		
			
			message.setHeader("countThread", new CountThreads());
			
			inicializado = Boolean.TRUE;
		} else {
			// no pudo inicializar
			ProcesoDiarioResponse response = new ProcesoDiarioResponse(-1, proceso, "No pudo inicializar la funcion en tablas KCO_FUNCIONES, MI_RESULTADOS", null);
			message.setBody(response);
		}
		
		message.setHeader("inicializado", inicializado);
		logger.info(String.format("InicializarProceso: %s inicializado=%b countThread: %s", getProceso(),
				message.getHeader("inicializado"), message.getHeader("countThread")));
	}

	//======================================================================================================================================
	// Getters y Setters
	//======================================================================================================================================

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

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

}
