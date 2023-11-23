package cl.uandes.sadmemail.apiGmailServices.wao;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.admin.reports.Reports;
import com.google.api.services.admin.reports.Reports.UserUsageReport;
import com.google.api.services.admin.reports.ReportsScopes;
import com.google.api.services.admin.reports.model.UsageReport;
import com.google.api.services.admin.reports.model.UsageReports;

public class ReportGmailWAOImpl extends GmailWAOesb {

	protected Reports reports = null;
	
	public ReportGmailWAOImpl(Map<String, Object> map) {
		super(map);
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
