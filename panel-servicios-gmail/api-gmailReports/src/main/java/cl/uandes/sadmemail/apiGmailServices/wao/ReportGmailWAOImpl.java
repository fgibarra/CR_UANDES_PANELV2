package cl.uandes.sadmemail.apiGmailServices.wao;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.reports.Reports;
import com.google.api.services.admin.reports.Reports.UserUsageReport;
import com.google.api.services.admin.reports.model.UsageReport;
import com.google.api.services.admin.reports.model.UsageReports;

import com.google.api.services.admin.reports.ReportsScopes;

public abstract class ReportGmailWAOImpl {
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
			ReportsScopes.ADMIN_REPORTS_AUDIT_READONLY);
	
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
			(new File(String.format("%s/%s","/home/appServers/fuse-7.11-UANDES/etc/auth/", "panel.p12" ))),
					"panel"),
	};
	static String APPLICATION_NAME = datos[indexDatosAccount].getApplicationName(); // "panel";
	static String SERVICE_ACCOUNT_EMAIL = datos[indexDatosAccount].getServiceAccount();//"913462879831-avq9g03d9vif9ojv3lln2033ks34b7vf@developer.gserviceaccount.com";
	static File SERVICE_ACCOUNT_PKCS12_FILE_PATH = datos[indexDatosAccount].getPkcs12FilePath();
	/* */
	static String ADMIN_EMAIL = "fgibarra@miuandes.cl";
	protected GoogleCredential credential = null;

	protected Reports reports = null;
	
	protected  String domain;
    protected  String adminEmail;
    protected  String adminPassword;
	
	public void init() {
        this.domain = getGmailDominio();
        this.adminEmail = getGmailAdmin();
        this.adminPassword = getGmailPasswd();
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			reports = getReportsService(ADMIN_EMAIL);
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

	
	public ReportGmailWAOImpl(Map<String, Object> map) {
		try {
			reports = getReportsService(ADMIN_EMAIL);
		} catch (IOException e) {
			logger.error("main", e);
		} catch (Throwable t) {
			logger.error("main", t);
		}
	}

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

	public void getReportUsuario(String idUsuario) {
		try {
			UserUsageReport.Get r = getReportsService(ADMIN_EMAIL).userUsageReport().get(idUsuario, "2023-11-23");
			UsageReports report = r.execute();
			List<UsageReport> lista = report.getUsageReports();
			for (UsageReport re : lista) {
				Set<Entry<String, Object>> set = re.entrySet();
				StringBuffer sb = new StringBuffer();
				for (Entry<String, Object> entry : set) {
					sb.append(String.format("%s = %s", entry.getKey(), entry.getValue())).append('\n');
				}
				System.out.println(sb.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
