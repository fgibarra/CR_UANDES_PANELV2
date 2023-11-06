package cl.uandes.panel.comunes.servicios.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.utils.ObjectFactory;

/**
 * Datos recuperados desde SQL getAlumnosNuevos o getAlumnosNuevosPeriodos
 * @author fernando
 *
 */
public class DatosCuentasBanner implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -1672062873052136254L;
	@JsonProperty("SPRIDEN_PIDM")
	private Integer spridenPidm;
	@JsonProperty("SPRIDEN_ID")
	private String spridenId;
	@JsonProperty("SPRIDEN_LAST_NAME")
	private String spridenLastName;
	@JsonProperty("SPRIDEN_FIRST_NAME")
	private String spridenFirstName;
	@JsonProperty("SPRIDEN_MI")
	private String spridenMi;
	@JsonProperty("nombre-cuenta")
	private String nombreCuenta;
	@JsonProperty("secuencia")
	private Integer secuencia;

	public DatosCuentasBanner(Map<String, Object> datos) {
		this.spridenPidm = ObjectFactory.toInteger((BigDecimal)datos.get("SPRIDEN_PIDM"));
		this.spridenId = (String)datos.get("SPRIDEN_ID");
		this.spridenLastName = (String)datos.get("SPRIDEN_LAST_NAME");
		this.spridenFirstName = (String)datos.get("SPRIDEN_FIRST_NAME");
		this.spridenMi = (String)datos.get("SPRIDEN_MI");
		this.secuencia = 0;
		
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}
	
	/**
	 * Devuelve el nombre completo
	 * @return nombre completo
	 */
	public String getNombres() {
		if (getSpridenMi() != null)
			return parseaDato(String.format("%s %s", getSpridenFirstName(), getSpridenMi()));
		else
			return parseaDato(String.format("%s", getSpridenFirstName()));
	}

	/**
	 * @return apellidos
	 */
	public String getApellidos() {
		return parseaDato(getSpridenLastName());
	}
	
	/**
	 * genera el nombre de cuenta priopuesto en base a la primera letra del primer nombre, primera letra del
	 * segundo nombre (si esta definido), primer apellido; sin caracteres no mimprimibles y en minusculas
	 * @return nombre cuenta propuesto
	 */
	public String getLoginName() {
        String valor = getSpridenLastName();
        do
        	valor = valor.replace("  ", " ");
            while (valor.indexOf("  ")>=0);
        valor = sacaNoImprimibles(valor.trim());

        int indx = valor.indexOf('/');
        valor = valor.substring(0, indx);
        
        StringBuffer sb = new StringBuffer();
        sb.append(getSpridenFirstName().charAt(0));
        if (getSpridenMi()!=null)
        	sb.append(getSpridenMi().charAt(0));
        sb.append(valor);
        return sb.toString().toLowerCase();
	}
	
	private String parseaDato(String dato) {
        // dos espacios por un espacio
        String valor = dato;
        do
        	valor = valor.replace("  ", " "); //ut.replaceInString(dto.getLastName().toLowerCase(),"  "," ");
            while (valor.indexOf("  ")>=0);
        valor = valor.trim();

        int indx = valor.indexOf('/');
        if (indx >= 0) {
        	// viene un solo apellido
        	valor = valor.replace("/"," ");
        }
        return valor;
	}

    private String sacaNoImprimibles(String data) {
    	if (data == null)
    		return data;
    	byte[] buffer = data.getBytes();
    	StringBuffer sb = new StringBuffer();
    	for (int i=0; i<buffer.length; i++) {
    		byte b = buffer[i];
    		if (32 <= b && b < 127) {
     			sb.append((char)b);
    		}
    	}
    	return sb.toString();
    }

    /**
     * Devuelve el spriden_id. Si es el de un RUT de alumno extranjero, es decir parte con @, saca la @
     * @return RUT sin @ (caso extranjeros)
     */
    public String getRut() {
    	if (spridenId == null) return null;
    	if (spridenId.charAt(0) == '@')
    		return spridenId.substring(1);
    	return spridenId.toUpperCase();
    }
    
    /**
     * Devuelve los primeros 8 digitos del spriden id, en caso de empezar con @, ocho 0
     * @return
     */
    public String getPassword() {
    	if (spridenId == null) return "UAndes2024";
    	if (spridenId.charAt(0) == '@')
    		return "00000000";
    	return spridenId.substring(0, 8);
    }
	//==========================================================================================================
	// getters y setters
	//==========================================================================================================
	
	public Integer getSpridenPidm() {
		return spridenPidm;
	}

	public void setSpridenPidm(Integer spridenPidm) {
		this.spridenPidm = spridenPidm;
	}

	public String getSpridenId() {
		return spridenId;
	}

	public void setSpridenId(String spridenId) {
		this.spridenId = spridenId;
	}

	public String getSpridenLastName() {
		return spridenLastName;
	}

	public void setSpridenLastName(String spridenLastName) {
		this.spridenLastName = spridenLastName;
	}

	public String getSpridenFirstName() {
		return spridenFirstName;
	}

	public void setSpridenFirstName(String spridenFirstName) {
		this.spridenFirstName = spridenFirstName;
	}

	public String getSpridenMi() {
		return spridenMi;
	}

	public void setSpridenMi(String spridenMi) {
		this.spridenMi = spridenMi;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public Integer getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(Integer secuencia) {
		this.secuencia = secuencia;
	}

}
