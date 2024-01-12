package cl.uandes.panel.comunes.json.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContadoresCrearCuentasAD extends ContadoresCrearCuentas implements Contadores {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1017572592844485752L;
	@JsonCreator
	public ContadoresCrearCuentasAD(@JsonProperty("cuentas-leidas") Integer countProcesados,
			@JsonProperty("errores") Integer countErrores,
			@JsonProperty("cuentas-agregadas-bd") Integer countAgregadosBD,
			@JsonProperty("cuentas-agregadas-ad") Integer countAgregadosAD) {
		super(countProcesados, countErrores, countAgregadosBD, countAgregadosAD);
	}

}
