package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosUsuarioBannerDTO;
import cl.uandes.panel.apiCambiaApellidoServices.procesor.exceptions.NotFoundBannerException;

/**
 * @author fernando
 *
 * En el body viene el resultado de la qry:
 * sql:select spriden_pidm, spriden_id, spriden_last_name, spriden_first_name, spriden_mi, from sgbstdn, spriden where spriden_pidm = sgbstdn_pidm and spriden_change_ind is null and sgbstdn_stst_code='AS' and (sgbstdn_term_code_eff = sgbstdn_term_code_admit) and (sgbstdn_admt_code &lt;> 'CI') and (sgbstdn_admt_code &lt;> 'CB') and (sgbstdn_styp_code &lt;> 'D') and spriden_id = :#${header.CambiaCuentaRequest.rut}?dataSource=#bannerDataSource"
 * 
 * Si no viene nada debe generar la excepcion NotFoundBannerException
 * 
 * Los datos contienen la informacion actualizada en Banner. 
 * Debe:
 * - armar el dto DatosUsuarioBannerDTO y dejarla en el header.
 * - dejar en el body el nuevo login_name de acuerdo a la regla:
 * 		- primera letra primer nombre + primera letra segundo nombre - apellido paterno  
 */
public class RecuperaDeBanner implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundBannerException(String.format("datos recuperados desde Banner son %s",resultados==null?"NULOS":"VACIOS"));
		DatosUsuarioBannerDTO dto = new DatosUsuarioBannerDTO(resultados.get(0));
		exchange.getIn().setHeader("DatosUsuarioBannerDTO", dto);
		
		String newLoginName = getNewLoginName(dto);
		exchange.getIn().setHeader("new_login_name", newLoginName);
		exchange.getIn().setBody(newLoginName);
	}

	private String getNewLoginName(DatosUsuarioBannerDTO dto) {
		String loginName = null;
        // dos espacios por un espacio
        String apellidos = dto.getLastName().toLowerCase();
        do
            apellidos = apellidos.replace("  ", " "); //ut.replaceInString(dto.getLastName().toLowerCase(),"  "," ");
            while (apellidos.indexOf("  ")>=0);
        apellidos = apellidos.trim();

        String apellido = null;
        int indx = apellidos.indexOf('/');
        if (indx < 0) {
        	// viene un solo apellido
        	apellido = apellidos.replaceAll(" ", "");
        }
        if (indx > 0) {
            apellido = apellidos.substring(0,indx).replaceAll(" ", "");
            apellidos = apellidos.replace("/"," ");
        } else {
            apellido = apellidos;
        }
        apellido = apellido.trim();
        if (apellido.endsWith("."))
        	do {
        		apellido = apellido.substring(0, apellido.length()-1);
        	} while (apellido.endsWith("."));
        
        if (apellido.indexOf('.') >= 0) {
        	logger.info("ERROR!!!! apellido=|"+apellido+"|");
        	apellido = apellido.replace(".","").toLowerCase();
        }

        StringBuffer sb = new StringBuffer();
        sb.append(dto.getFirstName().charAt(0));
        if (dto.getMiddleName() != null)
            sb.append(dto.getMiddleName().charAt(0));
        sb.append(apellido);
        loginName = toLetrasAscii(sb.toString());
		return loginName;
	}
	
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
                @SuppressWarnings("unused")
				char europeos[] =    {'á',  'é', 'è', 'í', 'ó', 'ú', 'ö', 'ü', 'ñ'};
                char europeoshex[] = {0xE1,0xE9,0xE8,0xED,0xF3,0xFA,0xF6,0xFC,0xF1};
                char corresp[] =     {'a',  'e', 'e', 'i', 'o', 'u', 'o', 'u', 'n'};
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
}
