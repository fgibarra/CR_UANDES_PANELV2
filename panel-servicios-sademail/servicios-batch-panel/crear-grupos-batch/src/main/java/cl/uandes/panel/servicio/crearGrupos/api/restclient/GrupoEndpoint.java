package cl.uandes.panel.servicio.crearGrupos.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupResponse;
import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberResponse;
import cl.uandes.sadmemail.comunes.gmail.json.MembersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MembersResponse;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;

/**
 * Definicion de los Endpoints del servicio api-grupos usados en el proceso de creacion de grupos 
 * @author fernando
 *
 */
@Path("/")
public interface GrupoEndpoint {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/user/retrieve/{username}")
	public UserResponse retrieveUser(@PathParam("username")String in_msg);

		@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/create")
	public GroupResponse createGroup(GroupRequest in_msg);
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/retrieveAll/{pageToken}")
	public GroupResponse retrieveAllGroups(@PathParam("pageToken")String in_msg);
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/retrieve/{groupName}")
	public GroupResponse retrieveGroup(@PathParam("groupName")String in_msg);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/update")
	public GroupResponse updateGroup(GroupRequest in_msg);
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/group/delete/{groupName}")
	public Response deleteGroup(@PathParam("groupName")String in_msg);
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/isOwner")
	public Response isOwner(MemberRequest in_msg);
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/isMember")
	public Response isMember(MemberRequest in_msg);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/addOwner")
	public MemberResponse addOwnerToGroup(MemberRequest in_msg);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/addMember")
	public MemberResponse addMemberToGroup(MemberRequest in_msg);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/retrieve")
	public MemberResponse retrieveMember(MemberRequest in_msg);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/deleteMember")
	public Response deleteMemberFromGroup(MemberRequest in_msg);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/deleteOwner")
	public Response deleteOwnerFromGroup(MemberRequest in_msg);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/members/retrieveMembers")
	public MembersResponse retrieveAllMembers(MembersRequest in_msg);
	
}
