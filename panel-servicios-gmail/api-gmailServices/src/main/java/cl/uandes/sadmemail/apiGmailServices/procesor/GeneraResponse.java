package cl.uandes.sadmemail.apiGmailServices.procesor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.Aliases;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;
import com.google.api.services.admin.directory.model.Users;

import cl.uandes.sadmemail.apiGmailServices.wao.GmailWAOesb;
import cl.uandes.sadmemail.comunes.gmail.json.AliasRequest;
import cl.uandes.sadmemail.comunes.gmail.json.AliasResponse;
import cl.uandes.sadmemail.comunes.gmail.json.AliasesResponse;
import cl.uandes.sadmemail.comunes.gmail.json.AllUsersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.AllUsersResponse;
import cl.uandes.sadmemail.comunes.gmail.json.GroupRequest;
import cl.uandes.sadmemail.comunes.gmail.json.GroupResponse;
import cl.uandes.sadmemail.comunes.gmail.json.GroupsResponse;
import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MemberResponse;
import cl.uandes.sadmemail.comunes.gmail.json.MembersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MembersResponse;
import cl.uandes.sadmemail.comunes.gmail.json.UserRequest;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.google.api.services.Member;
import cl.uandes.sadmemail.comunes.google.api.services.Members;

public class GeneraResponse implements Processor {

	GmailWAOesb wao;
	private String domain = "miuandes.cl";
	
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

		} else if ("all-user-retrieve".equals(operacion)) {
			AllUsersRequest request = (AllUsersRequest)exchange.getIn().getHeader("Body");
			logger.info(String.format("request: %s", request.toString()));
			AllUsersResponse response = new AllUsersResponse(0, "OK", null);
			Users users;
			try {
				users = wao.getAllUsers(request.getPageToken(), 
							request.getShowDeleted() != null ? request.getShowDeleted().toString() : null);
				if (users != null) {
					response = new AllUsersResponse(0, "OK", users.getNextPageToken());
					response.setListaUsuarios(response.factoryListaUsuarios(users.getUsers()));
					response.setCantidadRecuperados(response.getListaUsuarios().size());
				} else 
					response = new AllUsersResponse(-1, "NOT_FOUND", null);
			} catch (Exception e) {
				logger.error("all-user-retrieve", e);
				response = new AllUsersResponse(-1, e.getMessage(), null);
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
			String username = (String)exchange.getIn().getHeader("Body");
			try {
				wao.suspendUser(username);
				exchange.getIn().setBody(Response.ok().build());
			} catch (Exception e) {
				exchange.getIn().setBody(Response.status(Response.Status.BAD_REQUEST).build());
			}
		} else if ("user-reactivar".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			try {
				wao.reactivarUser(username);
				exchange.getIn().setBody(Response.ok().build());
			} catch (Exception e) {
				exchange.getIn().setBody(Response.status(Response.Status.BAD_REQUEST).build());
			}
		} else if ("user-delete".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			logger.info(String.format("user_delete: username=%s", username));
			try {
				wao.deleteUser(username);
				exchange.getIn().setBody(Response.ok().build());
			} catch (Exception e) {
				logger.error(String.format("ERROR user_delete: username=%s", username), e);
				exchange.getIn().setBody(Response.status(Response.Status.BAD_REQUEST).build());
			}
			
		} else if ("user-undelete".equals(operacion)) {
			String userKey = (String)exchange.getIn().getHeader("Body");
			logger.info(String.format("user_undelete: username=%s", userKey));
			try {
				wao.unDeleteUser(userKey);
				exchange.getIn().setBody(Response.ok().build());
			} catch (Exception e) {
				logger.error(String.format("ERROR user_delete: userKey=%s", userKey), e);
				exchange.getIn().setBody(Response.status(Response.Status.BAD_REQUEST).build());
			}
			
		} else if ("nickname-create".equals(operacion)) {
			AliasRequest req = (AliasRequest)exchange.getIn().getHeader("Body");
			AliasResponse response = null;
			try {
				Alias g_alias = wao.createNickname(req.getUserName(), req.getAliasName());
				response = new AliasResponse(0, "OK", Boolean.TRUE);
				response.setAlias(new cl.uandes.sadmemail.comunes.google.api.services.Alias(g_alias.getAlias(),g_alias.getEtag(),g_alias.getId(),g_alias.getKind(),g_alias.getPrimaryEmail()));
			} catch (Exception e) {
				response = new AliasResponse(-1, e.getMessage(), Boolean.FALSE);
			}
			exchange.getIn().setBody(response);
		} else if ("nickname-retrieve".equals(operacion)) {
			String username = (String)exchange.getIn().getHeader("Body");
			AliasResponse response = null;
			try {
				Alias g_alias = wao.retrieveNickname(username);
				if (g_alias != null) {
					response = new AliasResponse(0, "OK", Boolean.TRUE);
					response.setAlias(new cl.uandes.sadmemail.comunes.google.api.services.Alias(g_alias.getAlias(),g_alias.getEtag(),g_alias.getId(),g_alias.getKind(),g_alias.getPrimaryEmail()));
				} else {
					response = new AliasResponse(1, "OK", Boolean.FALSE);
				}
			} catch (Exception e) {
				response = new AliasResponse(-1, e.getMessage(), Boolean.FALSE);
			}
			exchange.getIn().setBody(response);
			logger.info(String.format("AliasResponse: %s", response.toString()));
			
		} else if ("nicknames-retrieve".equals(operacion)) {
			AliasesResponse response = null;
			String username = (String)exchange.getIn().getHeader("Body");
			try {
				Aliases aliases = wao.retrieveNicknames(username);
				List<cl.uandes.sadmemail.comunes.google.api.services.Alias> lista = new ArrayList<cl.uandes.sadmemail.comunes.google.api.services.Alias>();
				for (com.google.api.services.admin.directory.model.Alias g_alias : aliases.getAliases()) {
					lista.add(new cl.uandes.sadmemail.comunes.google.api.services.Alias(g_alias.getAlias(), g_alias.getEtag(), g_alias.getId(), g_alias.getKind(), g_alias.getPrimaryEmail()));
				}
				response = new AliasesResponse(0, "OK", lista);
				
			} catch (Exception e) {
				response = new AliasesResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			logger.info(String.format("AliasResponse: %s", response.toString()));
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
			String loginName = (String)exchange.getIn().getHeader("Body");
			GroupsResponse response =  null;
			try {
				com.google.api.services.admin.directory.model.Groups g_groups = wao.retrieveGroups(loginName);
				response = new GroupsResponse(0, "OK", new cl.uandes.sadmemail.comunes.google.api.services.Groups(g_groups));
			} catch (Exception e) {
				response = new GroupsResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("groups-retrieveAll".equals(operacion)) {
			String pageToken = (String)exchange.getIn().getHeader("Body");
			GroupsResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Groups g_groups = wao.retrieveAllGroups(pageToken);
				response = new GroupsResponse(0, "OK", new cl.uandes.sadmemail.comunes.google.api.services.Groups(g_groups));
			} catch (Exception e) {
				response = new GroupsResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("group-retrieveSettings".equals(operacion)) {
			String groupId = (String)exchange.getIn().getHeader("Body");
			GroupsResponse response = null;
			try {
				String email = wao.retrieveGroupSettings(groupId);
				response = new GroupsResponse(0, email, null);
			} catch (Exception e) {
				response = new GroupsResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("group-create".equals(operacion)) {
			GroupRequest req = (GroupRequest)exchange.getIn().getHeader("Body");
			GroupResponse response =  null;
			try {
				com.google.api.services.admin.directory.model.Group g_group = wao.createGroup(req.getGroupName(), null,
						req.getDescripcion(), req.getEmailPermission());
				response = new GroupResponse(0, "OK", new cl.uandes.sadmemail.comunes.google.api.services.Group(
						g_group.getDescription(), g_group.getEmail(), g_group.getEtag(),
						g_group.getId(), g_group.getKind(), g_group.getName(), g_group.getAliases(), 
						g_group.getDirectMembersCount()));
			} catch (Exception e) {
				response = new GroupResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("group-retrieve".equals(operacion)) {
			String groupName = (String)exchange.getIn().getHeader("Body");
			GroupResponse response =  null;
			try {
				com.google.api.services.admin.directory.model.Group g_group = wao.retrieveGroup(groupName);
				if (g_group != null)
					response = new GroupResponse(0, "OK", new cl.uandes.sadmemail.comunes.google.api.services.Group(
							g_group.getDescription(), g_group.getEmail(), g_group.getEtag(),
							g_group.getId(), g_group.getKind(), g_group.getName(), g_group.getAliases(), 
							g_group.getDirectMembersCount()));
				else
					response = new GroupResponse(-1, "No se pudo recuperar", null);
			} catch (Exception e) {
				response = new GroupResponse(-1, e.getMessage(), null);
			}
			logger.info(String.format("group-retrieve: response |%s|", response));
			exchange.getIn().setBody(response);
			
		} else if ("group-update".equals(operacion)) {
			GroupRequest req = (GroupRequest)exchange.getIn().getHeader("Body");
			GroupResponse response =  null;
			try {
				com.google.api.services.admin.directory.model.Group g_group = wao.updateGroup(req.getGroupName(),
						null, req.getDescripcion(), req.getEmailPermission());
				response = new GroupResponse(0, "OK", new cl.uandes.sadmemail.comunes.google.api.services.Group(
						g_group.getDescription(), g_group.getEmail(), g_group.getEtag(),
						g_group.getId(), g_group.getKind(), g_group.getName(), g_group.getAliases(), 
						g_group.getDirectMembersCount()));
			} catch (Exception e) {
				response = new GroupResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);

		} else if ("group-delete".equals(operacion)) {
			String groupName = (String)exchange.getIn().getHeader("Body");
			Response response =  null;
			try {
				wao.deleteGroup(groupName);
				response = Response.status(Response.Status.OK).build();
			} catch (Exception e) {
				response = Response.noContent().status(420, String.format("Fallo metodo: %s", e.getMessage())).build();
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-isOwner".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			Response response = null;
			try {
				Boolean es = wao.isOwner(req.getGroupName(), req.getEmail());
				response = Response.status(Response.Status.OK).entity(String.format("%b",es)).build();
			} catch (Exception e) {
				response = Response.status(Response.Status.BAD_REQUEST).build();
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-isMember".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			Response response = null;
			try {
				Boolean es = wao.isMember(req.getGroupName(), req.getEmail());
				Response.status(Response.Status.OK).entity(String.format("%b",es)).build();
			} catch (Exception e) {
				response = Response.status(Response.Status.BAD_REQUEST).build();
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-addOwner".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			MemberResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Member g_member = 
						wao.addOwnerToGroup(req.getGroupName(), req.getEmail());
				response = new MemberResponse(0, "OK", new Member(g_member.getEtag(), 
						g_member.getEmail(),g_member.getId(), g_member.getKind(), 
						g_member.getRole(), g_member.getType()));
			} catch (Exception e) {
				response = new MemberResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-addMember".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			MemberResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Member g_member = 
						wao.addMemberToGroup(req.getGroupName(), req.getEmail());
				response = new MemberResponse(0, "OK", new Member(g_member.getEtag(), 
						g_member.getEmail(),g_member.getId(), g_member.getKind(), 
						g_member.getRole(), g_member.getType()));
			} catch (Exception e) {
				response = new MemberResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-retrieve".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			MemberResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Member g_member = 
						wao.retrieveMember(req.getGroupName(), req.getEmail());
				response = new MemberResponse(0, "OK", new Member(g_member.getEtag(), 
						g_member.getEmail(),g_member.getId(), g_member.getKind(), 
						g_member.getRole(), g_member.getType()));
			} catch (Exception e) {
				response = new MemberResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("members-retrieveOwners".equals(operacion)) {
			MembersRequest req = (MembersRequest)exchange.getIn().getHeader("Body");
			MembersResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Members g_members =
						wao.retreiveGroupOwners(req.getGroupName(), req.getToken());
				response = new MembersResponse(0, "OK", new Members(g_members));
			} catch (Exception e) {
				response = new MembersResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("members-retreiveMembers".equals(operacion)) {
			MembersRequest req = (MembersRequest)exchange.getIn().getHeader("Body");
			MembersResponse response = null;
			try {
				com.google.api.services.admin.directory.model.Members g_members =
						wao.retrieveAllMembers(req.getGroupName(), req.getToken());
				response = new MembersResponse(0, "OK", new Members(g_members));
			} catch (Exception e) {
				response = new MembersResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		} else if ("member-deleteMember".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			Response response =  null;
			try {
				wao.deleteMemberFromGroup(req.getGroupName(), req.getEmail());
				response = Response.noContent().status(200).build();
			} catch (Exception e) {
				response = Response.noContent().status(420, String.format("Fallo metodo: %s", e.getMessage())).build();
			}
			logger.info(String.format("member-deleteMember: responde con status= %d", response.getStatus()));
			exchange.getIn().setBody(response);
			
		} else if ("member-deleteOwner".equals(operacion)) {
			MemberRequest req = (MemberRequest)exchange.getIn().getHeader("Body");
			GroupResponse response =  null;
			try {
				wao.deleteOwnerFromGroup(req.getGroupName(), req.getEmail());
				response = new GroupResponse(0, "OK", null);
			} catch (Exception e) {
				response = new GroupResponse(-1, e.getMessage(), null);
			}
			exchange.getIn().setBody(response);
			
		}
	}

	protected String getCuenta(String loginName) {
		if (loginName.indexOf('@') >= 0)
			return loginName;
		StringBuffer sb = new StringBuffer(loginName).append('@').append(domain);
		return sb.toString();
	}


}
