package cl.uandes.panel.paraGui.json;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.ObjectFactory;

public class ActualizaConsumoPanelRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 265608440770857249L;
	@JsonProperty("cuentas_alumnos")
	Integer cuentasAlumnos;
	@JsonProperty("cuentas_externas")
	Integer cuentasExternas;
	@JsonProperty("cuentas_profesores")
	Integer cuentasProfesores;
	@JsonProperty("total_cuentas")
	Integer totalCuentas;
	@JsonProperty("total_drive")
	Integer totalDrive;
	@JsonProperty("total_espacio")
	Integer totalEspacio;
	@JsonProperty("total_gmail")
	Integer totalGmail;
	@JsonProperty("total_photos")
	Integer totalPhotos;

	public ActualizaConsumoPanelRequest(Map<String, Object> data) {
		super();
		setCuentasAlumnos(ObjectFactory.toInteger((BigDecimal)data.get("CUENTAS_ALUMNOS")));
		setCuentasExternas(ObjectFactory.toInteger((BigDecimal)data.get("CUENTAS_EXTERNAS")));
		setCuentasProfesores(ObjectFactory.toInteger((BigDecimal)data.get("TOTAL_PROFESORES")));
		setTotalCuentas(ObjectFactory.toInteger((BigDecimal)data.get("TOTAL_CUENTAS")));
		setTotalDrive(ObjectFactory.toInteger((BigDecimal)data.get("USADO_DRIVE")));
		setTotalEspacio(ObjectFactory.toInteger((BigDecimal)data.get("QUOTA")));
		setTotalGmail(ObjectFactory.toInteger((BigDecimal)data.get("USADO_GMAIL")));
		setTotalPhotos(ObjectFactory.toInteger((BigDecimal)data.get("USADO_PHOTOS")));
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

	public Integer getCuentasAlumnos() {
		return cuentasAlumnos;
	}

	public void setCuentasAlumnos(Integer cuentasAlumnos) {
		this.cuentasAlumnos = cuentasAlumnos;
	}

	public Integer getCuentasExternas() {
		return cuentasExternas;
	}

	public void setCuentasExternas(Integer cuentasExternas) {
		this.cuentasExternas = cuentasExternas;
	}

	public Integer getCuentasProfesores() {
		return cuentasProfesores;
	}

	public void setCuentasProfesores(Integer cuentasProfesores) {
		this.cuentasProfesores = cuentasProfesores;
	}

	public Integer getTotalCuentas() {
		return totalCuentas;
	}

	public void setTotalCuentas(Integer totalCuentas) {
		this.totalCuentas = totalCuentas;
	}

	public Integer getTotalDrive() {
		return totalDrive;
	}

	public void setTotalDrive(Integer totalDrive) {
		this.totalDrive = totalDrive;
	}

	public Integer getTotalEspacio() {
		return totalEspacio;
	}

	public void setTotalEspacio(Integer totalEspacio) {
		this.totalEspacio = totalEspacio;
	}

	public Integer getTotalGmail() {
		return totalGmail;
	}

	public void setTotalGmail(Integer totalGmail) {
		this.totalGmail = totalGmail;
	}

	public Integer getTotalPhotos() {
		return totalPhotos;
	}

	public void setTotalPhotos(Integer totalPhotos) {
		this.totalPhotos = totalPhotos;
	}

}
