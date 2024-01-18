package cl.uandes.panel.comunes.procesor;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresAsignarOwners;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentas;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentasAD;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarCuentas;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.CountThreads;

public class InicializarProceso implements Processor {

	private RegistrosComunes registraInicio;
    @PropertyInject(value = "RegistrosComunes.key-contadores", defaultValue="No definido")
	private String keyContador;
	
	private String proceso = null;
	private ResultadoFuncion resultadoFuncion = null;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Boolean inicializado = Boolean.FALSE;
		proceso = (String) message.getHeader("proceso");
		
		Map<String, Object> datosInicializacion = registraInicio.inicializar(exchange);
		if (datosInicializacion != null) {
			DatosKcoFunciones data = (DatosKcoFunciones) datosInicializacion.get("DatosKcoFunciones");
			ResultadoFuncion res = (ResultadoFuncion) datosInicializacion.get("ResultadoFuncion");
			
			message.setHeader("DatosKcoFunciones", data);
			message.setHeader("ResultadoFuncion", res);
			message.setHeader("key", BigDecimal.valueOf(res.getKey().longValue()));		
			
			if ("suspende-elimina-cuentas".equals(getRegistraInicio().getLogAplicacion())) {
				message.setHeader("countThread", new CountThreads());
				if (keyContador.equals("No definido"))
					keyContador = "contadoresSincronizarCuentas";
				message.setHeader(keyContador, new ContadoresSincronizarCuentas(proceso));
			} else if ("crear-grupos-batch".equals(getRegistraInicio().getLogAplicacion())) {
				if (keyContador.equals("No definido"))
					keyContador = "contadoresCrearGrupos";
				message.setHeader(keyContador, new ContadoresCrearGrupos(0,0,0,0,0,0,0,0,0,0));
			} else if ("crear-cuentas-batch".equals(getRegistraInicio().getLogAplicacion())) {
				if (keyContador.equals("No definido"))
					keyContador = "contadoresCrearCuentas";
				message.setHeader(keyContador, new ContadoresCrearCuentas(0,0,0,0));
			} else if ("add-delete-owners-members".equals(getRegistraInicio().getLogAplicacion())) {
				message.setHeader("countThread", new CountThreads());
				if (keyContador.equals("No definido"))
					keyContador = "contadoresAsignarOwners";
				message.setHeader(keyContador, new ContadoresAsignarOwners(0,0,0,0,0,0));
			} else if (getRegistraInicio().getLogAplicacion().matches(".*crear_cuentas_AD.*") ) {
				if (keyContador.equals("No definido"))
					keyContador = "contadoresCuentasAD";
				message.setHeader(keyContador, new ContadoresCrearCuentasAD(0, 0, 0, 0));
				message.setHeader("countThread", new CountThreads());
			}
			
			logger.info(String.format("InicializarProceso: KCO_FUNCIONES.deshabilitado %b", data.getParametros().getDisabled()));

			if (data.getParametros().getDisabled()) {
				// El proceso esta deshabilitado
				// cerrar MI_RESULTADOS
				registraInicio.cierraMiResultados(exchange, keyContador);
				ProcesoDiarioResponse response = procesoDeshabilitado(data);
				message.setBody(response);
			} else {
				inicializado = Boolean.TRUE;
			}
		} else {
			// no pudo inicializar
			ProcesoDiarioResponse response = new ProcesoDiarioResponse(-1, proceso, "No pudo inicializar la funcion en tablas KCO_FUNCIONES, MI_RESULTADOS", null);
			message.setBody(response);
		}
		
		message.setHeader("inicializado", inicializado);
		logger.info(String.format("InicializarProceso: inicializado=%b keyContador=%s (%s) countThread: %s",
				message.getHeader("inicializado"), keyContador, message.getHeader(keyContador), message.getHeader("countThread")));
	}

	public ProcesoDiarioResponse procesoDeshabilitado(DatosKcoFunciones data) {
		// Actualizar hora de termino
		registraInicio.actualizaHoraTermino(data.getFuncion());
		ProcesoDiarioResponse response = new ProcesoDiarioResponse(0, data.getFuncion(), "El proceso esta deshabilitado en KCO_FUNCIONES", null);
		return response;
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

	public String getKeyContador() {
		return keyContador;
	}

	public void setKeyContador(String keyContador) {
		this.keyContador = keyContador;
	}
}
