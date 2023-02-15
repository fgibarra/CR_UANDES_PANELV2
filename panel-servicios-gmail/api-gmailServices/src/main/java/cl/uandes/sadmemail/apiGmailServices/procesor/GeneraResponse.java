package cl.uandes.sadmemail.apiGmailServices.procesor;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.Response;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;

import cl.uandes.sadmemail.apiGmailServices.wao.GmailWAOesb;
import cl.uandes.sadmemail.comunes.gmail.json.AliasResponse;
import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;

public class GeneraResponse implements Processor {

	GmailWAOesb wao;
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) ((List<?>)exchange.getIn().getBody()).get(0);
		//logger.info(String.format("Va a instanciar GmailWAOesb: map es %s NULO", map!=null?"NO":"LAMENTABLEMENTE"));
		wao = new GmailWAOesb(map);
		String operacion = (String)exchange.getIn().getHeader("Operacion");
		
		if ("user-create".equals(operacion)) {
			UserRequest request = (UserRequest)exchange.getIn().getHeader("Body");
			//logger.info(String.format("recuperado desde header: %s", request!=null?request.toString():"NULO!!!!"));
			User user = wao.createUser(request.getUser().getUsername(), 
					request.getUser().getGivenName(), 
					request.getUser().getFamilyName(), 
					request.getUser().getPassword());
			UserResponse response = new UserResponse(0, "OK", null);
			response.setUser(response.factoryUser(user));
			//logger.info(String.format("respuesta desde gmail: %s", response!=null?response.toString():"NULO!!!!"));
			exchange.getIn().setBody(response);
			
		} else if ("user-retrieve".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			UserResponse response = new UserResponse(0, "OK", null);
			User user;
			try {
				user = wao.retrieveUser(username);
				if (user != null) {
					response = new UserResponse(0, "OK", null);
					response.setUser(response.factoryUser(user));
				} else 
					response = new UserResponse(-1, "NOT_FOUND", null);
			} catch (Exception e) {
				response = new UserResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);

		} else if ("user-update".equals(operacion)) {
			UserRequest request = (UserRequest)exchange.getIn().getHeader("Body");
			UserResponse response = null;
			User usr = new User();
			UserName name = new UserName();
			/*logger.info(String.format("user-update: Id: %s FamilyName:%s Username: %s GivenName: %s", 
					request.getUser().getId(),request.getUser().getFamilyName(), request.getUser().getUsername(),
					request.getUser().getGivenName()));*/
			name.setFamilyName(request.getUser().getFamilyName());
			name.setFullName(request.getUser().getUsername());
			name.setGivenName(request.getUser().getGivenName());
			usr.setId(request.getUser().getId());
			usr.setName(name);
			usr.setPrimaryEmail(request.getUser().getEmail());
			try {
				usr = wao.updateUser( usr);
				if (usr != null) {
					response = new UserResponse(0, "OK", null);
					response.setUser(response.factoryUser(usr));
				}
			} catch (IOException e) {
				response = new UserResponse(-1, e.getMessage(), null);
			}
			logger.info(String.format("user-update: response: %s", response.toString()));
			exchange.getIn().setBody(response);
			
		} else if ("user-forceToChangePassword".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			UserResponse response = null;
			try {
				User userGmail = wao.forceUserToChangePassword(username);
				if (userGmail != null) {
					response = new UserResponse(0, "OK", null);
					response.setUser(response.factoryUser(userGmail));
				}
			} catch (Exception e) {
				response = new UserResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
		} else if ("user-suspend".equals(operacion)) {
			// TODO
			
		} else if ("user-reactivar".equals(operacion)) {
			// TODO
			
		} else if ("user-delete".equals(operacion)) {
			// TODO
			
		} else if ("nickname-create".equals(operacion)) {
			// TODO
			
		} else if ("nickname-retrieve".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			AliasResponse response = null;
			try {
				Alias alias = wao.retrieveNickname(username);
				if (alias != null) {
					logger.info(String.format("Alias: %s", alias.getAlias()));
					response = new AliasResponse(0, "OK", Boolean.TRUE);
					response.setHayAlias(true);
				} else {
					response = new AliasResponse(1, "OK", Boolean.FALSE);
					response.setHayAlias(false);
				}
			} catch (Exception e) {
				response = new AliasResponse(-1, e.getMessage(), Boolean.FALSE);
			}
			exchange.getIn().setBody(response);
			logger.info(String.format("AliasResponse: %s", response.toString()));
			
		} else if ("nicknames-retrieve".equals(operacion)) {
			// TODO

		} else if ("nickname-delete".equals(operacion)) {
			
			String oldAlias = (String)exchange.getIn().getHeader("Body");
			Response response = null;
			if (oldAlias != null) {
				if (oldAlias.indexOf('@') < 0)
					oldAlias = String.format("%s@miuandes.cl", oldAlias);
				wao.deleteNickname(oldAlias);
				response = Response.status(Response.Status.OK).entity(String.format("Alias %s eliminado",oldAlias)).build();
			} else {
				response = Response.status(Response.Status.BAD_REQUEST).build();
			}
			exchange.getIn().setBody(response);
			
		} else if ("groups-retrieve".equals(operacion)) {
			// TODO
			
		} else if ("groups-retrieveAll".equals(operacion)) {
			// TODO
			
		} else if ("group-retrieveSettings".equals(operacion)) {
			// TODO
			
		} else if ("group-create".equals(operacion)) {
			// TODO
			
		} else if ("group-retrieve".equals(operacion)) {
			// TODO
			
		} else if ("group-update".equals(operacion)) {
			// TODO
			
		} else if ("group-delete".equals(operacion)) {
			// TODO
			
		} else if ("member-isOwner".equals(operacion)) {
			// TODO
			
		} else if ("member-isMember".equals(operacion)) {
			// TODO
			
		} else if ("member-addOwner".equals(operacion)) {
			// TODO
			
		} else if ("member-addMember".equals(operacion)) {
			// TODO
			
		} else if ("member-retrieve".equals(operacion)) {
			// TODO
			
		} else if ("members-retrieveOwners".equals(operacion)) {
			// TODO
			
		} else if ("members-retreiveMembers".equals(operacion)) {
			// TODO
			
		} else if ("member-deleteMember".equals(operacion)) {
			// TODO
			
		} else if ("member-deleteOwner".equals(operacion)) {
			// TODO
			
		}
	}

}
