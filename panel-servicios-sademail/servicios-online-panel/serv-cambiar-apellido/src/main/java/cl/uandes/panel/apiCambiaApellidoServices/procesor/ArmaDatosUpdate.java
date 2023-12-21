package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosMiCuentaGmailDTO;
import cl.uandes.panel.apiCambiaApellidoServices.dto.DatosUsuarioBannerDTO;
import cl.uandes.panel.apiCambiaApellidoServices.procesor.exceptions.NotFoundPanelException;

/**
 * @author fernando
 *
 * En header.new_login_name esta el nombre a usar para la cuenta; validado que no existe en Gmail
 * En header.CambiaCuentaRequest esta el request original
 * En header.DatosUsuarioBannerDTO datos leidos desde Banner
 * 
 * Debe dejar en header.DatosMiCuentaGmail_old los datos recien leidos desde la tabla mi_cuentas_gmail
 * en header.DatosMiCuentaGmail_new los modificados
 * en header.datosGoremal los datos leidos desde goremal
 * 
 * Dejar en el body los datos para invocar al SP que actualiza mi_cuentas_gmail y goremal
 */
public class ArmaDatosUpdate implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty())
			throw new NotFoundPanelException(String.format("datos recuperados desde MI_CUENTAS_GMAIL son %s",resultados==null?"NULOS":"VACIOS"));
		DatosMiCuentaGmailDTO dto = new DatosMiCuentaGmailDTO(resultados.get(0));
		String newLoginName = (String) exchange.getIn().getHeader("new_login_name");
		logger.info(String.format("ArmaDatosUpdate: DatosMiCuentaGmail_old: %s new_login_name: %s",
				dto.toString(), newLoginName));
		if (newLoginName == null)
			throw new RuntimeException ("newLoginName no esta definido");
		
		exchange.getIn().setHeader("DatosMiCuentaGmail_old", dto);
		// Armar el nuevo nombre
		Map<String, Object> mapDatos = new HashMap<String, Object>();
		mapDatos.put("key", dto.getKey());
		mapDatos.put("moodle_id", dto.getMoodleId());
		mapDatos.put("banner_pidm", dto.getBannerPidm());
		mapDatos.put("login_name", newLoginName);
		DatosUsuarioBannerDTO usuarioBanner = (DatosUsuarioBannerDTO)exchange.getIn().getHeader("DatosUsuarioBannerDTO");
		if (usuarioBanner != null) {
			mapDatos.put("nombres", usuarioBanner.getNombres());
			mapDatos.put("apellidos", usuarioBanner.parseaDato((String) usuarioBanner.getApellidos()));
		} else {
			mapDatos.put("nombres", dto.getNombres());
			mapDatos.put("apellidos", dto.getApellidos());
		}
		mapDatos.put("id_gmail", dto.getIdGmail());
		mapDatos.put("rowid", dto.getRowid());
		DatosMiCuentaGmailDTO nuevo = new DatosMiCuentaGmailDTO(mapDatos);
		exchange.getIn().setHeader("DatosMiCuentaGmail_new", nuevo);
		exchange.getIn().setHeader("funcionPrd", "DO"); // actualiza

		logger.info(String.format("ArmaDatosUpdate: DatosMiCuentaGmail_new: %s", nuevo.toString()));
	}

}
