package cl.uandes.panel.tools.actualizaCuentasAD.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.batch.Contadores;

public class ContadoresActualizaCuentas implements Contadores {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2715043999617830852L;
	@JsonProperty("cuentas-leidas")
	private Integer countProcesados;
	@JsonProperty("errores")
	private Integer countErrores;
	@JsonProperty("cuentas-actualizadas-ad")
	private Integer countActualizadasAD;
	@JsonProperty("cuentas-creadas-ad")
	private Integer countCreadasAD;
	@JsonProperty("cuentas-recreadas-ad")
	private Integer countRecreadasAD;

	public ContadoresActualizaCuentas() {
		super();
		this.countProcesados = 0;
		this.countErrores = 0;
		this.countActualizadasAD = 0;
		this.countCreadasAD = 0;
		this.countRecreadasAD = 0;
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public synchronized void incCountProcesados() {
		countProcesados++;
	}

	public synchronized void incCountErrores() {
		countErrores++;
	}

	public synchronized void incCountActualizadasAD() {
		countActualizadasAD++;
	}

	public synchronized void incCountCreadasAD() {
		countCreadasAD++;
	}

	public synchronized void incCountRecreadasAD() {
		countRecreadasAD++;
	}

	public synchronized Integer getCountActualizadasAD() {
		return countActualizadasAD;
	}

	public synchronized Integer getCountCreadasAD() {
		return countCreadasAD;
	}

	public synchronized Integer getCountRecreadasAD() {
		return countRecreadasAD;
	}

	@Override
	public Integer getCountProcesados() {
		return countProcesados;
	}

	@Override
	public Integer getCountErrores() {
		return countErrores;
	}

	@Override
	public Integer getCount1() {
		return countActualizadasAD;
	}

	@Override
	public Integer getCount2() {
		return countRecreadasAD;
	}

}
