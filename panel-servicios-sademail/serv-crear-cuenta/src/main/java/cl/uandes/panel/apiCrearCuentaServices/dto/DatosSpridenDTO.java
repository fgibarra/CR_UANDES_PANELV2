package cl.uandes.panel.apiCrearCuentaServices.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosSpridenDTO implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 3901514845700266930L;
	@JsonProperty("SPRIDEN_ID")
	private String spridenId;
	@JsonProperty("SPRIDEN_PIDM")
	private Integer spridenPidm;
	@JsonProperty("SPRIDEN_FIRST_NAME")
	private String firstName;
	@JsonProperty("SPRIDEN_MI")
	private String middleName;
	@JsonProperty("SPRIDEN_LAST_NAME")
	private String lastName;

	public DatosSpridenDTO(Map<String, Object> datos) {
		super();
		this.spridenId = (String) datos.get("SPRIDEN_ID");
		this.spridenPidm = (Integer.valueOf(((java.math.BigDecimal) datos.get("SPRIDEN_PIDM")).intValue()));
		this.firstName = (String) datos.get("SPRIDEN_FIRST_NAME");
		this.middleName = (String) datos.get("SPRIDEN_MI");
		this.lastName = (String) datos.get("SPRIDEN_LAST_NAME");
		if (this.lastName != null && this.lastName.indexOf('/') >= 0) {
			char chars[] = this.lastName.toCharArray();
			for (int i=0; i<chars.length; i++)
				if (chars[i] == '/')
					chars[i] = ' ';
			this.lastName = new String(chars);
		}
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	@JsonIgnore
	public String getNombres() {
		return String.format("%s%s", getFirstName(), getMiddleName()!= null ? String.format(" %s", getMiddleName()):"");
	}
    /**
     * Obtiene un loginName no definido en Gmail
     * @param data
     * @return
     */
	@JsonIgnore
    public String getLoginName() {
        if (getSpridenPidm() == null || getLastName() == null || getFirstName() == null ||
            getLastName().length() == 0 || getFirstName().length()== 0)
            return null;

        // dos espacios por un espacio
        String apellidos = null;
        do
            apellidos = getLastName().toLowerCase().replace("  "," ");
            while (apellidos.indexOf("  ")>=0);

        String nombre = null;
        if (getMiddleName()!=null && !getMiddleName().isEmpty())
        	nombre = String.format("%s %s", getFirstName().toLowerCase().replace(" ", ""), getMiddleName().toLowerCase());
        else
        	nombre = getFirstName().toLowerCase();
        String apellido = null;
        int indx = apellidos.indexOf('/');
        if (indx < 0) {
            // puede que apellidos no vengan separados por /
            apellidos = apellidos.replace(' ','/');
            indx = apellidos.indexOf('/');
            switch (cuantos('/', apellidos)) {
            case 0:
                // no viene un apellido
                break;
            case 1:
                // paterno y materno
                break;
            default:
                // es un apellido compuesto
                return null;
            }
        }
        if (indx > 0) {
            apellido = apellidos.substring(0,indx);
            apellidos = getLastName().replace('/', ' ');
        } else {
            apellido = apellidos;
        }

        StringBuffer sb = new StringBuffer().append(nombre.charAt(0));
        indx = nombre.indexOf(' ');
        if (indx > 0) {
            do {
                indx++;
            } while (nombre.charAt(indx) == ' ');
            sb = sb.append(nombre.charAt(indx));
        }
        sb = sb.append(apellido);
        String cuenta = toLetrasAscii(sb.toString());

        return cuenta;
    }

	@JsonIgnore
    @SuppressWarnings("unused")
	public String toLetrasAscii(String valor) {
        if (valor == null || valor.length() == 0)
            return valor;
        int len = valor.length();
        char chars[] = new char[len];
        valor.toLowerCase().getChars(0,len,chars,0);
        StringBuffer newValor = new StringBuffer();
        for (int i=0; i<len;i++) {
            char cc = chars[i];
            if (cc == ' ')
                continue; //skip espacios
            if (cc > 'z') {
                char europeos[] = {'á','é','è','í','ó','ú','ö','ü','ñ'};
                char europeoshex[] = {0xE1,0xE9,0xE8,0xED,0xF3,0xFA,0xF6,0xFC,0xF1};
                char corresp[] = {'a','e','e','i','o','u','o','u','n'};
                for (int j=0; j<europeoshex.length; j++)
                    if (cc == europeoshex[j]) {
                        cc = corresp[j];
                        break;
                    }
                if (cc < 'a' || cc > 'z') {
                    // no es letra europea
                    continue;
                }
            }
            if ((cc >= 'A' && cc <= 'Z') || (cc >= 'a' && cc <= 'z') ||
                (cc >= '0' && cc <= '9') || cc =='.' || cc == '_')
                newValor.append(cc);
        }
        return newValor.toString();
    }

	@JsonIgnore
	private int cuantos(char c, String s) {
        int n = 0;

        for (int p = 0; p < s.length(); p++)
            if (s.charAt(p) == c) {
                n++;
            }

        return n;
	}

	public String getSpridenId() {
		return spridenId;
	}

	public void setSpridenId(String spridenId) {
		this.spridenId = spridenId;
	}

	public Integer getSpridenPidm() {
		return spridenPidm;
	}

	public void setSpridenPidm(Integer spridenPidm) {
		this.spridenPidm = spridenPidm;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
