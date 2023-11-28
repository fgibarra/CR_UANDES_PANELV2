package cl.uandes.panel.comunes.json.batch;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Usado para colocar en el comentario de la tabla MI_RESULTADO
 * 
 * @author fernando
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContadoresCrearCuentas implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 8780869348691973254L;
	@JsonProperty("cuentas-leidas")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("cuentas-agregadas-bd")
	private Integer countAgregadosBD;
	@JsonProperty("cuentas-agregadas-ad")
	private Integer countAgregadosAD;

	@JsonCreator
	public ContadoresCrearCuentas(@JsonProperty("cuentas-leidas") Integer countProcesados,
			@JsonProperty("errores") Integer countErrores,
			@JsonProperty("cuentas-agregadas-bd") Integer countAgregadosBD,
			@JsonProperty("cuentas-agrgadas-ad") Integer countAgregadosAD) {
		super();
		this.countProcesados = countProcesados;
		this.countErrores = countErrores;
		this.countAgregadosBD = countAgregadosBD;
		this.countAgregadosAD = countAgregadosAD;
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public synchronized void incCountProcesados() {
		countProcesados++;
	}

	public synchronized void incCountErrores() {
		countErrores++;
	}

	public synchronized void incCountAgregadosBD() {
		countAgregadosBD++;
	}

	public synchronized void incCountAgregadosAD() {
		countAgregadosAD++;
	}

	public synchronized Integer getCountProcesados() {
		return countProcesados;
	}

	public synchronized Integer getCountErrores() {
		return countErrores;
	}

	public synchronized Integer getCountAgregadosBD() {
		return countAgregadosBD;
	}

	public synchronized Integer getCountAgregadosAD() {
		return countAgregadosAD;
	}

}
