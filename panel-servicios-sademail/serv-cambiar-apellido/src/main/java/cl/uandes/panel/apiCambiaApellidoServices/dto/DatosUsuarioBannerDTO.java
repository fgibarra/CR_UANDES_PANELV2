package cl.uandes.panel.apiCambiaApellidoServices.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosUsuarioBannerDTO implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -5356635183505735156L;
	@JsonProperty("spriden_pidm")
	Integer spridenPidm;
	@JsonProperty("spriden_id")
	String spridenId;
	@JsonProperty("spriden_last_name")
	String lastName;
	@JsonProperty("spriden_first_name")
	String firstName;
	@JsonProperty("spriden_mi")
	String middleName;

	public DatosUsuarioBannerDTO(Map<String, Object> mapDatos) {
		super();
		this.spridenPidm = Integer.valueOf(((String)mapDatos.get("spriden_pidm")));
		this.spridenId = (String)mapDatos.get("spriden_id");
		this.lastName = parseaDato((String)mapDatos.get("spriden_last_name"));
		this.firstName = (String)mapDatos.get("spriden_first_name");
		this.middleName = (String)mapDatos.get("spriden_mi");
		
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
	
	public Object getNombres() {
		return parseaDato(String.format("%s %s", getFirstName(), getMiddleName()));
	}

	public Object getApellidos() {
		return parseaDato(getLastName());
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

	//===========================================================================================
	// GETTERS y SETTERS
	//===========================================================================================

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

}
