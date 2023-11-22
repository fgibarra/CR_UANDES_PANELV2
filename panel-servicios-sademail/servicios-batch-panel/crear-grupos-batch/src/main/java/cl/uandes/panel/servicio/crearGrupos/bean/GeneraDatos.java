package cl.uandes.panel.servicio.crearGrupos.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupResponse;
import cl.uandes.panel.comunes.json.batch.crearGrupos.Grupo;
import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberResponse;
import cl.uandes.panel.comunes.servicios.dto.GruposMiUandes;

/**
 * Multiples metodos usados en las rutas Camel del proceso
 * @author fernando
 *
 */
public class GeneraDatos {

	Logger logger = Logger.getLogger(getClass());
    @PropertyInject(value = "crear-grupos-gmail.numero-pruebas", defaultValue="-1")
    String propNumberPrueba;


	public void getListaOperaciones(Exchange exchange) {
		@SuppressWarnings("unchecked")
		List<String> lista = (List<String>)exchange.getIn().getHeader("listaOperaciones");
		logger.info(String.format("getListaOperaciones: lista.size=%d", lista.size()));
		String proceso = lista.remove(0);
		logger.info(String.format("getListaOperaciones: proceso= %s", proceso));
		exchange.getIn().setHeader("proceso", proceso);
	}
	
	public void factoryGrupos(Exchange exchange) {
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> listaRecuperada = (List<Map<String,Object>>)exchange.getIn().getBody();
		int numberPrueba = Integer.valueOf(propNumberPrueba);
		logger.info(String.format("factoryGrupos: recuperados %d grupos; para probar %d",
				listaRecuperada!=null?listaRecuperada.size():0, numberPrueba));
		List<GruposMiUandes> listaGrupos = new ArrayList<GruposMiUandes>();
		for (Map<String,Object> map : listaRecuperada) {
			listaGrupos.add(new GruposMiUandes(map));
			if (--numberPrueba == 0)
				break;
		}
		exchange.getIn().setHeader("listaGrupos", listaGrupos);
	}
	
	public void getGrupoFromListaGrupos(@Header("listaGrupos") List<GruposMiUandes> listaGrupos, Exchange exchange) {
		GruposMiUandes grupo = listaGrupos.remove(0);
		logger.info(String.format("getGrupoFromListaGrupos:grupo %s", grupo));
		exchange.getIn().setHeader("grupoGmail", grupo);
	}

	/* ==========================================================================================================
	 * Para sincronizar grupos
	 * 
	 */
	public void factoryGruposSincronizar(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>)message.getBody();
		List<String> lista = new ArrayList<String>();
		for (Map<String, Object> map : datos) {
			lista.add((String)map.get("GROUP_NAME"));
		}
		message.setHeader("listaGruposSincronizar", lista);
	}
	
	public void getGrupoSincronizar(@Header("listaGruposSincronizar") List<String> listaGruposSincronizar, Exchange exchange) {
		String groupName = listaGruposSincronizar.remove(0);
		logger.info(String.format("getGrupoSincronizar: groupName %s", groupName));
		exchange.getIn().setHeader("grupoGmail", groupName);
	}
	/* ==========================================================================================================
	 * Para probar offline
	 * 
	 * para probar sin tener que invocar con soapui
	 */
	public void setBody(Exchange exchange) {
		String tipos[] = {"crear_grupos_azure"/*, "grupos_inprogress_azure", "grupos_inprogress_posgrado_azure"*/};
		ProcesoDiarioRequest req = new ProcesoDiarioRequest("generaGrupos", tipos);
		logger.info(String.format("generaRequestCrearGupos: req:%s", req));
		exchange.getIn().setBody(req);
		exchange.getIn().setHeader("listaOperaciones", new ArrayList<String>(Arrays.asList(req.getOperaciones())));
	}
	
	public void testCreateGrupoGmail(Exchange exchange) {
		GroupRequest body = (GroupRequest) exchange.getIn().getBody();
		logger.info(String.format("testCreateGrupoAzure: %s clase %s", body, body!=null?body.getClass().getName():"es NULO"));
		Map<String,Object> headers = exchange.getIn().getHeaders();
		StringBuffer sb = new StringBuffer();
		for (String key : headers.keySet()) {
			sb.append(String.format("header.key: %s ", key));
			Object valor = headers.get(key);
			sb.append(String.format("header.value: %s clase %s\n", valor, valor!=null?valor.getClass().getName():"es NULO"));
		}
		logger.info(sb.toString());
		GruposMiUandes grupoBD = (GruposMiUandes)exchange.getIn().getHeader("grupoGmail");
		Grupo grupo = new Grupo();
		grupo.setId(String.format("id-raro-para-al-grupo-%d",grupoBD.getKey()));
		GroupResponse response = new GroupResponse(0, "OK", grupo);
		
		exchange.getIn().setBody(response);
	}
	
	public void testRecuperaGrupoGmail(Exchange exchange) {
		Object body = exchange.getIn().getBody();
		logger.info(String.format("testRecuperaGrupoAzure: %s clase %s", body, body!=null?body.getClass().getName():"es NULO"));
		Map<String,Object> headers = exchange.getIn().getHeaders();
		StringBuffer sb = new StringBuffer();
		for (String key : headers.keySet()) {
			sb.append(String.format("header.key: %s ", key));
			Object valor = headers.get(key);
			sb.append(String.format("header.value: %s clase %s\n", valor, valor!=null?valor.getClass().getName():"es NULO"));
		}
		logger.info(sb.toString());
		GroupResponse response = new GroupResponse(0, "OK", null);
		
		exchange.getIn().setBody(response);
	}
	
	public void testSacarMember(Exchange exchange) {
		MemberResponse response = new MemberResponse(0, "OK", null);
		exchange.getIn().setBody(response);
	}
	
	public void testAgregarMember(Exchange exchange) {
		MemberResponse response = new MemberResponse(0, "OK", null);
		exchange.getIn().setBody(response);
	}
	
	public void testDeleteGrupoGmail(Exchange exchange) {
		Response response = Response.noContent().status(200).build();
		exchange.getIn().setBody(response);
	}

	public String getPropNumberPrueba() {
		return propNumberPrueba;
	}

	public void setPropNumberPrueba(String propNumberPrueba) {
		this.propNumberPrueba = propNumberPrueba;
	}
}
