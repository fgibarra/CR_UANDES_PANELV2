package cl.uandes.panel.comunes.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;

public class ManejoErrores {

	String logAplicacion;
	
	@EndpointInject(uri = "sql:classpath:sql/getKey.sql?dataSource=#bannerDataSource")
	ProducerTemplate getKey;

	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultado;

	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;

	@EndpointInject(uri = "sql:classpath:sql/insertKcoLogError.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertKcoLogError;

	/**
	 * @param logAplicacion
	 */
	public ManejoErrores(String logAplicacion) {
		super();
		this.logAplicacion = logAplicacion;
	}

	public Integer registraMiResultado(String funcion, Integer minThread, Integer maxThread) {
		Integer key = null;
		
		return key;
	}
	/**
	 * @param idUsuario
	 * @param tipo
	 * @param causa
	 * @param keyGrupo
	 * @param keyResultado
	 * @return
	 */
	public Integer registraMiResultadoErrores(String idUsuario, String tipo, String causa, Integer keyGrupo, Integer keyResultado) {
		Integer key = null;
		
		return key;
	}
	
	/**
	 * @param logClase
	 * @param logMetodo
	 * @param logApoyo
	 * @param e
	 * @param keyMiResultadoErrores
	 */
	public void registraLogError(Class logClase, String logMetodo, String logApoyo, Throwable e, Integer keyMiResultadoErrores) {
		Map<String, Object> headers = new HashMap<String, Object>();
	}

	//============================================================================================================
	// Getters y Setters
	//============================================================================================================

	public String getLogAplicacion() {
		return logAplicacion;
	}

	public void setLogAplicacion(String logAplicacion) {
		this.logAplicacion = logAplicacion;
	}
}
