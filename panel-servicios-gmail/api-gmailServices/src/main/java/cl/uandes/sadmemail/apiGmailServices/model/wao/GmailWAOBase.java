package cl.uandes.sadmemail.apiGmailServices.model.wao;

import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.Aliases;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;

public interface GmailWAOBase {


	public User createUser(String username, String givenName,
            String familyName,
            String password) throws Exception;
	public User retrieveUser(String username) throws Exception;
	public User updateUser(User user) throws Exception;
	public User forceUserToChangePassword(String username) throws Exception;
	public void suspendUser(String username) throws Exception;
	public void reactivarUser(String username) throws Exception;
	public void deleteUser(String username) throws Exception;

	public Alias createNickname(String userName, String aliasName) throws Exception;
	public Alias retrieveNickname(String nickname) throws Exception;
	public Aliases retrieveNicknames(String username) throws Exception;
	public void deleteNickname(String oldAlias) throws Exception;
	
	public Groups retrieveGroups(String loginName) throws Exception;
	public String retrieveGroupSettings(String groupName) throws Exception;
	
	public Group createGroup(String groupName, String groupX, String descripcion, String emailPermission) throws Exception;
	public Group updateGroup(String groupName, String groupX, String descripcion, String emailPermission) throws Exception;
	public Group retrieveGroup(String groupName) throws Exception;
	public void deleteGroup(String groupName) throws Exception;
	//public Map<String, Object> retrievePageOfGroups(String token) throws Exception;
	public Groups retrieveAllGroups(String pageToken) throws Exception ;
	
	public boolean isOwner(String groupName, String email) throws Exception;
	public boolean isMember(String groupName, String email) throws Exception;
	public Member addOwnerToGroup(String groupName, String email) throws Exception;
	public Member addMemberToGroup(String groupName, String email) throws Exception;
	public Member retrieveMember(String groupName, String correo) throws Exception;
	public Members retreiveGroupOwners(String groupName, String token) throws Exception;
	public Members retrieveAllMembers(String groupName, String token) throws Exception;
	public void deleteMemberFromGroup(String groupName, String email) throws Exception;
	public void deleteOwnerFromGroup(String groupName, String email) throws Exception;
}
