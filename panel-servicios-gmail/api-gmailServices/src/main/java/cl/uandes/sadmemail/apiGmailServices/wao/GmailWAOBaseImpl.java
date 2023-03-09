package cl.uandes.sadmemail.apiGmailServices.wao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.Aliases;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;
import com.google.api.services.groupssettings.*;
import com.google.gson.Gson;

public abstract class GmailWAOBaseImpl implements GmailWAOBase {



	protected Logger logger = Logger.getLogger(this.getClass().getName());
	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	/** Global instance of the HTTP transport. */
	protected static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	protected static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** OAuth 2.0 scopes. */
	protected static final List<String> SCOPES = Arrays.asList(
			DirectoryScopes.ADMIN_DIRECTORY_USER,
			DirectoryScopes.ADMIN_DIRECTORY_GROUP,
			DirectoryScopes.ADMIN_DIRECTORY_USER_SECURITY,
			"https://www.googleapis.com/auth/userinfo.profile",
			"https://www.googleapis.com/auth/userinfo.email" /*,
			ReportsScopes.ADMIN_REPORTS_AUDIT_READONLY*/);
	
	// usar para uandes
	// usar en forma local local
	public static class DatosAccount {
		private String serviceAccount;
		private File pkcs12FilePath;
		private String applicationName;
	
		public DatosAccount(String serviceAccount, File pkcs12FilePath,
				String applicationName) {
			super();
			this.serviceAccount = serviceAccount;
			this.pkcs12FilePath = pkcs12FilePath;
			this.applicationName = applicationName;
		}
		public String getServiceAccount() {
			return serviceAccount;
		}
		public void setServiceAccount(String serviceAccount) {
			this.serviceAccount = serviceAccount;
		}
		public File getPkcs12FilePath() {
			return pkcs12FilePath;
		}
		public void setPkcs12FilePath(File pkcs12FilePath) {
			this.pkcs12FilePath = pkcs12FilePath;
		}
		public String getApplicationName() {
			return applicationName;
		}
		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}
		
	}
	static int indexDatosAccount = 0;
	static DatosAccount datos[] = { 
		new DatosAccount(
			"913462879831-avq9g03d9vif9ojv3lln2033ks34b7vf@developer.gserviceaccount.com",
			System.getProperty("karaf.etc") != null ?
			(new File(String.format("%s/auth/%s",System.getProperty("karaf.etc"), "panel.p12")) ):
			(new File(String.format("%s/%s","/home/fernando/datosUAndes/KeysGoogle_panel/", "panel.p12" ))),
					"panel"),
	};
	static String APPLICATION_NAME = datos[indexDatosAccount].getApplicationName(); // "panel";
	static String SERVICE_ACCOUNT_EMAIL = datos[indexDatosAccount].getServiceAccount();//"913462879831-avq9g03d9vif9ojv3lln2033ks34b7vf@developer.gserviceaccount.com";
	static File SERVICE_ACCOUNT_PKCS12_FILE_PATH = datos[indexDatosAccount].getPkcs12FilePath();
	/* */
	static String ADMIN_EMAIL = "fgibarra@miuandes.cl";

	protected GoogleCredential credential = null;
	protected GoogleCredential credentialGS = null;
	protected Directory directory = null;
//	protected Reports reports = null;
	protected Groupssettings groupssettings;
	
	protected  String domain;
    protected  String adminEmail;
    protected  String adminPassword;
	
	public void init() {
        this.domain = getGmailDominio();
        this.adminEmail = getGmailAdmin();
        this.adminPassword = getGmailPasswd();
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			directory = getDirectoryService(ADMIN_EMAIL);
			//reports = getReportsService(ADMIN_EMAIL);
			//groupssettings = getGroupssettingsService(ADMIN_EMAIL);
			/*
			logger.info("GmailWAOBaseImpl_constructor: domain="+domain+" adminEmail="+adminEmail+
					" APPLICATION_NAME="+APPLICATION_NAME+" SERVICE_ACCOUNT_EMAIL="+SERVICE_ACCOUNT_EMAIL+
					" directory="+(directory != null ? directory.getServicePath() : "NULO")+
					" groupssettings="+(groupssettings != null ? groupssettings.getServicePath() : "NULO"));
					*/
			return;
		} catch (IOException e) {
			logger.error("main", e);
		} catch (Throwable t) {
			logger.error("main", t);
		}
	}

    protected abstract String getGmailAdmin();


    protected abstract String getGmailPasswd();


    protected abstract String getGmailDominio();

	private Directory getDirectoryService(String userEmail) throws Exception {
		try {
			if (credential == null) {
				credential = new GoogleCredential.Builder()
						.setTransport(httpTransport)
						.setJsonFactory(JSON_FACTORY)
						.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
						.setServiceAccountScopes(SCOPES)
						.setServiceAccountUser(userEmail)
						.setServiceAccountPrivateKeyFromP12File(
								SERVICE_ACCOUNT_PKCS12_FILE_PATH).build();
			}
			
	    	//logger.info("getDirectoryService credential="+(credential == null?"NULO":credential.getServiceAccountScopesAsString()));
			
			Directory service = new Directory.Builder(httpTransport, JSON_FACTORY,
					null).setApplicationName(APPLICATION_NAME)
					.setHttpRequestInitializer(credential).build();
			return service;
		} catch (Exception e) {
			logger.error("getDirectoryService", e);
			throw e;
		}
	}

/*
	private Reports getReportsService(String userEmail) throws Exception {
        
		try {
			if (credential == null)
				credential = new GoogleCredential.Builder()
						.setTransport(httpTransport)
						.setJsonFactory(JSON_FACTORY)
						.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
						.setServiceAccountScopes(SCOPES)
						.setServiceAccountUser(userEmail)
						.setServiceAccountPrivateKeyFromP12File(
								SERVICE_ACCOUNT_PKCS12_FILE_PATH).build();
			return new Reports.Builder(
					httpTransport, JSON_FACTORY, credential)
			        .setApplicationName(APPLICATION_NAME)
			        .build();
		} catch (Exception e) {
			logger.error("getReportsService", e);
			throw e;
		}
    }
*/	
	protected Directory getDirectory() {
		return directory;
	}
	
	public Groupssettings getGroupssettings() {
		return groupssettings;
	}
	
	protected String getCuenta(String loginName) {
		if (loginName.indexOf('@') >= 0)
			return loginName;
		StringBuffer sb = new StringBuffer(loginName).append('@').append(domain);
		return sb.toString();
	}
	

	public class GoogleJsonResponse {
		Integer code;
		List<Errores> errors;
		String message;
		
		public class Errores {
			String domain;
			String message;
			String reason;
			String extendedHelp;
			public String getDomain() {
				return domain;
			}
			public void setDomain(String domain) {
				this.domain = domain;
			}
			public String getMessage() {
				return message;
			}
			public void setMessage(String message) {
				this.message = message;
			}
			public String getReason() {
				return reason;
			}
			public void setReason(String reason) {
				this.reason = reason;
			}
			public String getExtendedHelp() {
				return extendedHelp;
			}
			public void setExtendedHelp(String extendedHelp) {
				this.extendedHelp = extendedHelp;
			}
			
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public List<Errores> getErrors() {
			return errors;
		}

		public void setErrors(List<Errores> errors) {
			this.errors = errors;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	public int analizaException(com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
		GoogleJsonResponse data = new Gson().fromJson(e.getContent(), GoogleJsonResponse.class);
		if (data.getCode() == 403) {
			for (GoogleJsonResponse.Errores error : data.errors) {
				if ("usageLimits".equals(error.getDomain()))
					return 10;
			}
		} else if (data.getCode() == 404) {
			for (GoogleJsonResponse.Errores error : data.errors) {
				if ("global".equals(error.getDomain())) {
					if (error.getMessage().startsWith("Resource Not Found"))
						return 1;
				}
				return 4;
			}
		}
		return 1;
	}
	
	//---------------------------------------------------------------------------------------------------
	//  CUENTAS
	//---------------------------------------------------------------------------------------------------

	/* Crear cuenta
	 * @see sadmemail.lib.model.wao.GmailWAOBase#createUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public User createUser(String username, String givenName,
			String familyName, String password) throws IOException {
		/*
		logger.info(String.format("createUser: username:%s givenName: %s familyName: %s password: %s", 
				username,givenName,familyName, password));
		logger.info(String.format("primaryEmail: %s", getCuenta(username)));
		*/
		User user = new User();
		// populate are the required fields only
		UserName name = new UserName();
		name.setFamilyName(familyName);
		name.setGivenName(givenName);
		user.setName(name);
		user.setPassword(password);
		user.setPrimaryEmail(getCuenta(username));

		/*logger.info("createUser: setPrimaryEmail="+user.getPrimaryEmail()+
				" getFullName="+name.getFullName());
		*/
		// requires DirectoryScopes.ADMIN_DIRECTORY_USER scope
		user = directory.users().insert(user).execute();

		return user;
	}
	
	/* Recupera el User del username indicado
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveUser(java.lang.String)
	 */
	@Override
	public User retrieveUser(String username) throws Exception {
		String gn = (username.indexOf("@") >= 0 ? username : getCuenta(username));
		//logger.info("retrieveUser: username="+gn);
		try {
			User user = directory.users().get(gn).execute();
			//logger.info("retrieveUser: id="+user.getId()+" email="+user.getPrimaryEmail());
			return user;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				logger.info("retrieveUser: username="+gn+" NOT FOUND");
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveUser",e);
					throw e;
				}
				return null;
			}
			logger.error(String.format("retrieveUser: directory es %s NULO",directory!=null?"NO":"lamentablemente"),e);
			throw e;
		}
	}

	/* Actualizar datos del usuario
	 * @see sadmemail.lib.model.wao.GmailWAOBase#updateUser(com.google.api.services.admin.directory.model.User)
	 */
	@Override
	public User updateUser(User user) throws IOException {
		try {
			if (user.getPrimaryEmail() != null)
				user.setPrimaryEmail(getCuenta(user.getPrimaryEmail()));
			user = directory.users().update(user.getId(), user).execute();
			return user;
		} catch (IOException e) {
			logger.error("updateUser", e);;
			throw e;
		}
	}

	/* Actualizar el usuario para forzar el cambio de password siguiente acceso
	 * @see sadmemail.lib.model.wao.GmailWAOBase#forceUserToChangePassword(java.lang.String)
	 */
	@Override
	public User forceUserToChangePassword(String username) throws Exception {
		com.google.api.services.admin.directory.Directory.Users.Get get = directory
				.users().get(getCuenta(username));
		try {
			User user = get.execute();
			user.setChangePasswordAtNextLogin(true);
			return updateUser(user);
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("forceUserToChangePassword",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
	}

	/* Eliminar la cuenta
	 * @see sadmemail.lib.model.wao.GmailWAOBase#deleteUser(java.lang.String)
	 */
	@Override
	public void deleteUser(String username) throws Exception {
		User user = retrieveUser(username);
		if (user != null) {
			directory.users().delete(user.getId()).execute();
		}
	}

	/* Borrar el alias
	 * @see sadmemail.lib.model.wao.GmailWAOBase#deleteNickname(java.lang.String)
	 */
	@Override
	public void deleteNickname(String oldAlias) throws Exception {
		Alias a = retrieveNickname(oldAlias);
		if (a != null) {
			logger.info("deleteNickname: a.getId()="+a.getId()+" a.getAlias()="+a.getAlias());
			directory.users().aliases().delete(a.getId(), a.getAlias()).execute();
		}
	}

	/* Recuperar el alias
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveNickname(java.lang.String)
	 */
	@Override
	public Alias retrieveNickname(String nickname) throws Exception {
		User usr = retrieveUser(nickname);
		if (usr == null) return null;
		List<String> lista = usr.getAliases();
		if (lista != null) {
			String key = getCuenta(nickname);
			for (String alias : lista) {
				if (key.equals(alias)) {
					Alias a = new Alias();
					a.setAlias(key);
					a.setId(usr.getId());
					return a;
				}
			}
		}
		return null;
	}

	/* Recuperar todos los alias de la cuenta
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveNicknames(java.lang.String)
	 */
	@Override
	public Aliases retrieveNicknames(String username) throws Exception {
		try {
			Aliases aliases = directory.users().aliases().list(getCuenta(username)).execute();
			if  (aliases != null && aliases.getAliases() != null) {
				for (Alias alias : aliases.getAliases()) {
					logger.info("retrieveNicknames: getAlias="+alias.getAlias()+" getPrimaryEmail="+alias.getPrimaryEmail());
				}
				return aliases;
			}
			return null;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				com.google.api.client.googleapis.json.GoogleJsonResponseException er = (com.google.api.client.googleapis.json.GoogleJsonResponseException)e;
				for (ErrorInfo err : er.getDetails().getErrors()) {
					if (err.getMessage().startsWith("Resource Not Found")) {
						return null;
					}
				}
				throw e;
			}
			throw e;
		}
	}

	/* Crea nickName a cuenta
	 * @see sadmemail.lib.model.wao.GmailWAOBase#createNickname(java.lang.String, java.lang.String)
	 */
	@Override
	public Alias createNickname(String userName, String aliasName) throws Exception {
		User user = retrieveUser(userName);
		logger.info("createNickname: userName="+userName+" user="+(user==null?"NOTFOUND":"FOUND"));
		if (user != null) {
			Alias alias = new Alias();
			alias.setId(user.getId());
			alias.setAlias(getCuenta(aliasName));
			try {
				alias = directory.users().aliases().insert(user.getId(), alias).execute();
				logger.info("createNickname: getAlias="+alias.getAlias());
				return alias;
			} catch (Exception e) {
				if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
					com.google.api.client.googleapis.json.GoogleJsonResponseException er = (com.google.api.client.googleapis.json.GoogleJsonResponseException)e;
					for (ErrorInfo err : er.getDetails().getErrors()) {
						if (err.getMessage().startsWith("Entity already exists")) {
							return alias;
						}
					}
					throw e;
				}
			}
		}
		return null;
	}

	/* Suspender al usuario
	 * @see sadmemail.lib.model.wao.GmailWAOBase#suspendUser(java.lang.String)
	 */
	@Override
	public void suspendUser(String username) throws Exception {
		User user = retrieveUser(username);
		if (user != null) {
			user.setSuspended(true);
			updateUser(user);
		}
	}
	
	/* Rectivar al usuario
	 * @see sadmemail.lib.model.wao.GmailWAOBase#reactivarUser(java.lang.String)
	 */
	@Override
	public void reactivarUser(String username) throws Exception {
		User user = retrieveUser(username);
		if (user != null) {
			user.setSuspended(false);
			updateUser(user);
		}
	}
	
	/* Recupera los grupos a los que pertenece la cuenta email
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveGroups(java.lang.String)
	 */
	@Override
	public Groups retrieveGroups(String loginName) throws Exception {
		User user = retrieveUser(loginName);
		if (user != null) {
			Directory.Groups.List list = directory.groups().list();
			list.setUserKey(user.getId());
			Groups grupos = list.execute();
			return grupos;
		}
		return null;
	}

	//---------------------------------------------------------------------------------------------------
	//  GRUPOS
	//---------------------------------------------------------------------------------------------------

	/* Crear grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#createGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Group createGroup(String groupName, String groupX, String descripcion, String emailPermission) throws Exception {
		Group grupo = new Group();
		grupo.setAdminCreated(true);
		grupo.setDescription(descripcion);
		grupo.setEmail(getCuenta(groupName));
		grupo.setName(groupName);
		if ("Owner".equals(emailPermission)) 
			setOwnerGroupProperties(grupo);

		logger.info("createGroup: getName="+grupo.getName()+" getEmail="+grupo.getEmail());
		try {
			grupo = directory.groups().insert(grupo).execute();
			return grupo;
		} catch (IOException e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				com.google.api.client.googleapis.json.GoogleJsonResponseException er = (com.google.api.client.googleapis.json.GoogleJsonResponseException)e;
				for (ErrorInfo err : er.getDetails().getErrors()) {
					if (err.getMessage().startsWith("Entity already exists")) {
						return retrieveGroup(groupName);
					}
				}
				throw e;
			} else
				throw e;
		}
	}

	/* Actualiza datos generales del grupo. groupX pasa a ser el nuevo nombre, groupName@miuandes.cl es el email y
	 * groupName es el nombre.
	 * @see sadmemail.lib.model.wao.GmailWAOBase#updateGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Group updateGroup(String groupName, String groupX, String descripcion, String emailPermission) throws Exception {
		Group grupo = retrieveGroup(getCuenta(groupName));
		if (grupo != null) {
			grupo.setAdminCreated(true);
			grupo.setDescription(descripcion);
			grupo.setName(groupX);
			if ("Owner".equals(emailPermission)) 
				setOwnerGroupProperties(grupo);
			try {
				grupo = directory.groups().update(grupo.getId(), grupo).execute();
			} catch (IOException e) {
				throw e;
			}
		}
		return grupo;
	}

	private void setOwnerGroupProperties(Group grupo) {
		grupo.set("whoCanPostMessage", "ALL_MANAGERS_CAN_POST");
		grupo.set("whoCanAdd", "ALL_MANAGERS_CAN_ADD");
		grupo.set("whoCanInvite", "ALL_MANAGERS_CAN_INVITE");
	}

	/* Recupera el Group para el groupName indicado
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveGroup(java.lang.String)
	 */
	@Override
	public Group retrieveGroup(String groupName) throws Exception {
		String grup = (groupName.indexOf('@') >= 0 ? groupName : getCuenta(groupName));
		com.google.api.services.admin.directory.Directory.Groups.Get get = directory.groups().get(grup);
		
		logger.info("retrieveGroup: grup="+grup);
		try {
			Group grupo = get.execute();
			logger.info("retrieveGroup: grupo="+(grupo != null ? "NO ":"")+"es NULO");
			return grupo;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveGroup",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
	}

	@Override
	public String retrieveGroupSettings(String groupId) throws Exception {
		
		logger.info("retrieveGroupSettings: groupId="+groupId);
		Groupssettings.Groups.Get get = groupssettings.groups().get(groupId);
		
		get.setFields("whoCanPostMessage");

		try {
			com.google.api.services.groupssettings.model.Groups grupo = get.execute();
			logger.info("retrieveGroupSettings: grupo="+(grupo != null ? "NO ":"")+"es NULO");
			return grupo.getEmail();
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveGroupSettings",e);
					throw e;
				}
				return null;
			}
			logger.error("retrieveGroupSettings", e);
			throw e;
		}
	}

	/* Borrar grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#deleteGroup(java.lang.String)
	 */
	@Override
	public void deleteGroup(String groupName) throws Exception {
		Group grupo = retrieveGroup(getCuenta(groupName));
		if (grupo != null) {
			try {
				directory.groups().delete(grupo.getId()).execute();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	@Override
	public Groups retrieveAllGroups(String pageToken) throws Exception {
		Groups listaGrupos = null;
		String dominio = getGmailDominio();
		try {
			Directory.Groups.List dgl = directory.groups().list();
			dgl.setDomain(dominio);

			if (pageToken == null) {
				listaGrupos = dgl.execute();
			} else {
				listaGrupos = dgl.setPageToken(pageToken).execute();
			}
			return listaGrupos;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveAllGroups",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
		
	}
	/* TRUE si el email tiene el rol OWNER en el grupo indicado
	 * @see sadmemail.lib.model.wao.GmailWAOBase#isOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isOwner(String groupName, String email) throws Exception {
		String token = null;
		do {
			Members members = retreiveGroupOwners(groupName, token);
			if (members != null) {
				for (Member member : members.getMembers())
					if (email.equals(member.getEmail()))
						return true;
			}
		} while (token != null);
		return false;
	}

	/* TRUE si el email pertence al grupo indicado
	 * @see sadmemail.lib.model.wao.GmailWAOBase#isMember(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isMember(String groupName, String email) throws Exception {
		String token = null;
		do {
			Members members = retrieveAllMembers(groupName, token);
			if (members != null) {
				for (Member member : members.getMembers())
					if (email.equals(member.getEmail()))
						return true;
				token = members.getNextPageToken();
			}
		} while (token != null);
		return false;
	}

	/* Agregar un owner a un grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#addOwnerToGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public Member addOwnerToGroup(String groupName, String email) throws Exception {
		logger.info("addOwnerToGroup: groupName="+groupName+" email="+email);
		Group grupo = retrieveGroup(groupName);
		if (grupo != null) {
			String groupKey = grupo.getId();
			Member member = new Member();
			member.setEmail(email);
			member.setRole("OWNER");
			member.setType("USER");
			logger.info("addOwnerToGroup: grupo="+grupo.getName()+" groupKey="+groupKey);
			try {
				directory.members().insert(groupKey, member).execute();
			} catch (Exception e) {
				if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
					com.google.api.client.googleapis.json.GoogleJsonResponseException er = (com.google.api.client.googleapis.json.GoogleJsonResponseException)e;
					for (ErrorInfo err : er.getDetails().getErrors()) {
						logger.error("addOwnerToGroup: err.getMessage()="+err.getMessage());
						if (err.getMessage().indexOf("Member already") >= 0) {
							logger.info("addOwnerToGroup: Member already FOUND");
							if (member.getId() != null) {
								try {
									directory
											.members()
											.update(groupKey, member.getId(),
													member).execute();
								} catch (Exception e2) {
									logger.error("addOwnerToGroup: ", e2);
								}
							}
							return member;
						}
						if (err.getMessage().indexOf("groupKey") >=  0) {
							logger.info("addOwnerToGroup: groupKey FOUND");
							return member;
						}
						throw e;
					}
				} else {
					logger.error("addOwnerToGroup: ", e);
					throw e;
				}
			}
			return member;
		} else 
			logger.info("addOwnerToGroup: grupo="+groupName+" NOT FOUND!!");
		return null;
	}

	/* Agregar un miembro al grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#addMemberToGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public Member addMemberToGroup(String groupName, String email) throws Exception {
		logger.info("addMemberToGroup: groupName="+groupName+" email="+email);
		Group grupo= retrieveGroup(groupName);
		if (grupo != null) {
			String groupKey = grupo.getId();
			User user = retrieveUser(email);
			if (user != null) {
				logger.info("addMemberToGroup: groupKey="+groupKey+" getId="+user.getId()+" email="+email);
				Member member = new Member();
				member.setEmail(getCuenta(email));
				member.setRole("MEMBER");
				member.setType("USER");
				directory.members().insert(groupKey, member).execute();
				return member;
			}
		}
		return null;
	}

	/* Saca un miembro del grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#deleteMemberFromGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteMemberFromGroup(String groupName, String email) throws Exception {
		logger.info("deleteMemberFromGroup: groupName="+groupName+
				" getCuenta(groupName)="+getCuenta(groupName)+
				" email="+email);
		try {
			groupName = getCuenta(groupName);
			String pageToken = null;
			Directory.Members.List res = directory.members().list(groupName);
			do {
				Members members = res.execute();
				if (members != null && members.getMembers() != null) {
					logger.info("deleteMemberFromGroup: grupo "+groupName+" tiene "+members.getMembers().size()+" miembros");
					for (Member member : members.getMembers()) {
						logger.info("deleteMemberFromGroup: miembro |"+member.getEmail()+"| role="+member.getRole()+" getCuenta(email)=|"+getCuenta(email)+"|");
						if (member.getEmail().equals(getCuenta(email)) /*&& member.getRole().equals("MEMBER")*/) {
							logger.info("deleteMemberFromGroup: elimina cuenta |"+getCuenta(email)+"|");
							directory.members().delete(groupName, getCuenta(email)).execute();
							return;
						}
					}
				} else {
					logger.info("deleteMemberFromGroup: groupName="+groupName+" NO TIENE MIEMBROS????");
					return;
				}
				pageToken = members.getNextPageToken();
				res.setPageToken(pageToken);
				logger.info("deleteMemberFromGroup: pageToken="+pageToken);
			} while (pageToken !=  null);
			return;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("deleteMemberFromGroup",e);
					throw e;
				}
				GoogleJsonResponseException gje = (GoogleJsonResponseException)e;
				logger.error("deleteMemberFromGroup:"+gje.getDetails().getMessage(), gje);
				return ;
			}
			throw e;
		}
	}

	/* saca un owner del grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#deleteOwnerFromGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteOwnerFromGroup(String groupName, String email) throws Exception {
		logger.info("deleteOwnerFromGroup: groupName="+groupName+" email="+email);
		try {
			Group grupo = retrieveGroup(groupName);
			if (grupo != null) {
				Members members = directory.members().list(getCuenta(groupName)).execute();
				
				logger.info("deleteOwnerFromGroup: members.size()="+members.size());
				
				for (Member member : members.getMembers()) {
					
					if (member.getEmail().equals(email) && member.getRole().equals("OWNER")) {
						logger.info("deleteOwnerFromGroup: ELIMINANDO email="+email+
								" grupo.getId()="+grupo.getId()+
								" member.getId()="+member.getId());
						
						directory.members().delete(grupo.getId(), member.getId()).execute();
						return;
					}
				}
			} else
				logger.info("deleteOwnerFromGroup: groupName="+groupName+" NOT FOUND!!!");
			return;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("deleteOwnerFromGroup",e);
					throw e;
				}
				return;
			}
			throw e;
		}
	}

	/* Recupera los owners del grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retreiveGroupOwners(java.lang.String)
	 */
	@Override
	public Members retreiveGroupOwners(String groupName, String token) throws Exception {
		try {
			Members members;
			if (token != null) {
				members = directory.members().list(getCuenta(groupName)).setPageToken(token).execute();
			} else {
				members = directory.members().list(getCuenta(groupName)).execute();
			}
			List<Member> owners = new ArrayList<Member>();
			for (Member member : members.getMembers()) {
				if ("OWNER".equals(member.getRole()))
					owners.add(member);
			}
			members.setMembers(owners);
			return members;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retreiveGroupOwners",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
	}

	/* recupera miembros y owners del grupo
	 * @see sadmemail.lib.model.wao.GmailWAOBase#retrieveAllMembers(java.lang.String)
	 */
	@Override
	public Members retrieveAllMembers(String groupName, String token) throws Exception {
		logger.info("retrieveAllMembers: groupName="+groupName+" token="+token);
		try {
			Members members;
			if (token != null) {
				members = directory.members().list(getCuenta(groupName)).setPageToken(token).execute();
			} else
				members = directory.members().list(getCuenta(groupName)).execute();
			return members;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveAllMembers",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
	}

	@Override
	public Member retrieveMember(String groupName, String correo) throws Exception {
		logger.info("retrieveMember: groupName="+groupName+" correo="+correo);
		try {
			Member member;
				member = directory.members().get(getCuenta(groupName), getCuenta(correo)).execute();
			return member;
		} catch (Exception e) {
			if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
				if (analizaException((com.google.api.client.googleapis.json.GoogleJsonResponseException)e) > 5) {
					logger.error("retrieveAllMembers",e);
					throw e;
				}
				return null;
			}
			throw e;
		}
	}
}
