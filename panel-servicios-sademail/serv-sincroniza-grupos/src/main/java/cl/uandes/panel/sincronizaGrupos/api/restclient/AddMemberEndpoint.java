package cl.uandes.panel.sincronizaGrupos.api.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MemberResponse;

@Path("/")
public interface AddMemberEndpoint {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Path("/member/addMember")
	public MemberResponse addMemberToGroup(MemberRequest in_msg);
}