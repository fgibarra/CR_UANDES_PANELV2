package cl.uandes.sadmemail.apiGmailServices.api.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

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

/**
 * URI : cxf/ESB/panel/gmailServices/
 * @author fernando
 *
 */
@Path("/")
public class GmailServicesRestService {

	@EndpointInject(uri = "direct:start")
	ProducerTemplate producer;

	Logger logger = Logger.getLogger(getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/create")
	public UserResponse createUser(UserRequest in_msg) {
		//logger.info(String.format("createUser: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-create");
		headers.put("Body", in_msg);
		return (UserResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieveAllUsers")
	public AllUsersResponse retrieveAllUser(AllUsersRequest in_msg) {
		logger.info(String.format("retrieveUser: in_msg: %s - %s",in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "all-user-retrieve");
		headers.put("Body", in_msg);
		return (AllUsersResponse) producer.requestBodyAndHeaders(in_msg, headers);
		//return (UserResponse) producer.requestBodyAndHeader(in_msg, "Operacion", "user-retrieve");
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieve/{username}")
	public UserResponse retrieveUser(@PathParam("username")String in_msg) {
		logger.info(String.format("retrieveUser: in_msg: %s - %s",in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-retrieve");
		headers.put("Body", in_msg);
		return (UserResponse) producer.requestBodyAndHeaders(in_msg, headers);
		//return (UserResponse) producer.requestBodyAndHeader(in_msg, "Operacion", "user-retrieve");
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieve")
	public UserResponse retrieveUserByQP(@QueryParam("loginName")String in_msg) {
		logger.info(String.format("retrieveUserByQP: in_msg: %s - %s",in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-retrieve");
		headers.put("Body", in_msg);
		return (UserResponse) producer.requestBodyAndHeaders(in_msg, headers);
		//return (UserResponse) producer.requestBodyAndHeader(in_msg, "Operacion", "user-retrieve");
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/update")
	public UserResponse updateUser(UserRequest in_msg) {
		//logger.info(String.format("updateUser: in_msg: %s - %s",in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-update");
		headers.put("Body", in_msg);
		return (UserResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieve/{username}")
	public UserResponse forceUserToChangePassword(@PathParam("username")String in_msg) {
		logger.info(String.format("forceUserToChangePassword: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-forceToChangePassword");
		headers.put("Body", in_msg);
		return (UserResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/suspend/{username}")
	public Response suspendUser(@PathParam("username")String in_msg) {
		logger.info(String.format("suspendUser: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-suspend");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/reactivar/{username}")
	public Response reactivarUser(@PathParam("username")String in_msg) {
		logger.info(String.format("reactivarUser: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-reactivar");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/delete/{username}")
	public Response deleteUser(@PathParam("username")String in_msg) {
		logger.info(String.format("deleteUser: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-delete");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/undelete/{userKey}")
	public Response undeleteUser(@PathParam("userKey")String in_msg) {
		logger.info(String.format("undeleteUser: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "user-undelete");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickName/create")
	public AliasResponse createNickname(AliasRequest in_msg) {
		logger.info(String.format("createNickname: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "nickname-create");
		headers.put("Body", in_msg);
		return (AliasResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickName/retrieve/{username}")
	public AliasResponse retrieveNickname(@PathParam("username")String in_msg) {
		logger.info(String.format("retrieveNickname: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "nickname-retrieve");
		headers.put("Body", in_msg);
		return (AliasResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickName/retrieve")
	public AliasResponse retrieveNicknameByQP(@QueryParam("nickname")String in_msg) {
		logger.info(String.format("retrieveNicknameByQP: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "nickname-retrieve");
		headers.put("Body", in_msg);
		return (AliasResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickNames/retrieve/{username}")
	public AliasesResponse retrieveNicknames(@PathParam("username")String in_msg) {
		logger.info(String.format("retrieveNicknames: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "nicknames-retrieve");
		headers.put("Body", in_msg);
		return (AliasesResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/nickName/delete/{username}")
	public Response deleteNickname(@PathParam("username")String in_msg) {
		logger.info(String.format("deleteNickname: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "nickname-delete");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/groups/retrieve/{groupname}")
	public GroupsResponse retrieveGroups(@PathParam("groupname")String in_msg) {
		logger.info(String.format("retrieveGroups: in_msg: %s - %s", 
				in_msg!=null?in_msg.getClass().getSimpleName():"ES NULO", in_msg!=null?in_msg:"ES NULO"));
		if (in_msg == null) {
			return new GroupsResponse(-1, "No viene grupo a consultar", null);
		}
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "groups-retrieve");
		headers.put("Body", in_msg);
		return (GroupsResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/groups/retrieveAll/{pageToken}")
	public GroupsResponse retrieveAllGroups(@PathParam("pageToken")String in_msg) {
		logger.info(String.format("retrieveAllGroups: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "groups-retrieveAll");
		headers.put("Body", in_msg);
		return (GroupsResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	

	@GET
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.TEXT_HTML+"; charset=UTF-8")
    @Path("/group/retrieveSettings/{groupName}")
	public String retrieveGroupSettings(@PathParam("groupName")String in_msg) {
		logger.info(String.format("retrieveGroupSettings: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "group-retrieveSettings");
		headers.put("Body", in_msg);
		return (String) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/create")
	public GroupResponse createGroup(GroupRequest in_msg) {
		logger.info(String.format("createGroup: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "group-create");
		headers.put("Body", in_msg);
		return (GroupResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/retrieve/{groupName}")
	public GroupResponse retrieveGroup(@PathParam("groupName")String in_msg) {
		logger.info(String.format("retrieveGroup: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "group-retrieve");
		headers.put("Body", in_msg);
		return (GroupResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/update")
	public GroupResponse updateGroup(GroupRequest in_msg) {
		logger.info(String.format("updateGroup: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "group-update");
		headers.put("Body", in_msg);
		return (GroupResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/delete/{groupName}")
	public Response deleteGroup(@PathParam("groupName")String in_msg) {
		logger.info(String.format("deleteGroup: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "group-delete");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/isOwner")
	public Response isOwner(MemberRequest in_msg) {
		logger.info(String.format("isOwner: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-isOwner");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/isMember")
	public Response isMember(MemberRequest in_msg) {
		logger.info(String.format("isMember: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-isMember");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/addOwner")
	public MemberResponse addOwnerToGroup(MemberRequest in_msg) {
		logger.info(String.format("addOwnerToGroup: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-addOwner");
		headers.put("Body", in_msg);
		return (MemberResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/addMember")
	public MemberResponse addMemberToGroup(MemberRequest in_msg) {
		logger.info(String.format("addMemberToGroup: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-addMember");
		headers.put("Body", in_msg);
		return (MemberResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/retrieve")
	public MemberResponse retrieveMember(MemberRequest in_msg) {
		logger.info(String.format("retrieveMember: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-retrieve");
		headers.put("Body", in_msg);
		return (MemberResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/members/retrieveOwners")
	public MembersResponse retreiveGroupOwners(MembersRequest in_msg) {
		logger.info(String.format("retreiveGroupOwners: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "members-retrieveOwners");
		headers.put("Body", in_msg);
		return (MembersResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/members/retrieveMembers")
	public MembersResponse retrieveAllMembers(MembersRequest in_msg) {
		logger.info(String.format("retrieveAllMembers: in_msg= %s - %s", in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "members-retreiveMembers");
		headers.put("Body", in_msg);
		return (MembersResponse) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/deleteMember")
	public Response deleteMemberFromGroup(MemberRequest in_msg) {
		logger.info(String.format("deleteMemberFromGroup: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-deleteMember");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/deleteOwner")
	public Response deleteOwnerFromGroup(MemberRequest in_msg) {
		logger.info(String.format("deleteOwnerFromGroup: in_msg: %s - %s", 
				in_msg.getClass().getSimpleName(), in_msg));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("Operacion", "member-deleteOwner");
		headers.put("Body", in_msg);
		return (Response) producer.requestBodyAndHeaders(in_msg, headers);
	}
}
