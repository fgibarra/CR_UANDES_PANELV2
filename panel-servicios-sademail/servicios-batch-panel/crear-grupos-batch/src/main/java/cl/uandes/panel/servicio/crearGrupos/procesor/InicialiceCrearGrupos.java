package cl.uandes.panel.servicio.crearGrupos.procesor;

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
 * Action que parte el proceso despues de leer los parametros guardado en la tabla KCO_FUNCIONES
 * @author fernando
 *
 */
@Deprecated
public class InicialiceCrearGrupos implements Processor {
	private RegistrosComunes registraInicio;
	
	/**
	 *	
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

	public RegistrosComunes getRegistraInicio() {
		return registraInicio;
	}

	public void setRegistraInicio(RegistrosComunes registraInicio) {
		this.registraInicio = registraInicio;
	}
	
}
