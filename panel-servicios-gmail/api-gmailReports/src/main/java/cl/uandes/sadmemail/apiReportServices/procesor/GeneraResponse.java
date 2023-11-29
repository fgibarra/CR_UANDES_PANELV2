package cl.uandes.sadmemail.apiReportServices.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import com.google.api.services.admin.reports.model.UsageReport;
import com.google.api.services.admin.reports.model.UsageReport.Entity;
import com.google.api.services.admin.reports.model.UsageReport.Parameters;

import cl.uandes.sadmemail.apiReportServices.wao.ReportGmailWAOesb;
import cl.uandes.sadmemail.comunes.report.json.Report;
import cl.uandes.sadmemail.comunes.report.json.ReportResponse;

public class GeneraResponse implements Processor {

	ReportGmailWAOesb wao;
	
    @PropertyInject(value = "panelv2.dominio", defaultValue="miuandes.cl")
	private String dominio;
	
    private Logger logger = Logger.getLogger(getClass());
    
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Map<String, Object> headers = message.getHeaders();
		ReportGmailWAOesb wao = new ReportGmailWAOesb(headers);
		String operacion = (String)exchange.getIn().getHeader("Operacion");
		if ("report-usage".equals(operacion)) {
			Map<String, Object> reporteGmail = wao.getReportUsuario((String)headers.get("Body"));
			UsageReport.Entity entity = (Entity) reporteGmail.get("entity");
			@SuppressWarnings("unchecked")
			List<UsageReport.Parameters> listaParametros = (List<Parameters>) reporteGmail.get("parameters");
			Report reporte = factoryReporte(entity, listaParametros);
			ReportResponse response = new ReportResponse(0, "OK", reporte);
			message.setBody(response);
		}
	}

	private Report factoryReporte(Entity entity, List<Parameters> listaParametros) {
		String userEmail = entity.getUserEmail();
		Map<String, Object> map = createMap(listaParametros);
		String lastLoginTime = (String) map.get("last_login_time");
		String creationTime = (String)map.get("creation_time");
		Boolean isDisabled = (Boolean)map.get("is_disabled");
		Integer gmailUsedQuotaInMb = (Integer)map.get("gmail_used_quota_in_mb");
		Integer driveUsedQuotaInMb = (Integer)map.get("drive_used_quota_in_mb");
		Integer gplusPhotosUsedQuotaInMb = (Integer)map.get("gplus_photos_used_quota_in_mb");
		Boolean isSuspended = (Boolean)map.get("is_suspended");
		Integer totalQuotaInMb = (Integer)map.get("total_quota_in_mb");
		Integer usedQuotaInMb = (Integer)map.get("used_quota_in_mb");
		Integer usedQuotaInPercentage = (Integer)map.get("used_quota_in_percentage");
		Report report = new Report(
				userEmail,
				lastLoginTime,
				creationTime,
				isDisabled,
				gmailUsedQuotaInMb,
				driveUsedQuotaInMb,
				gplusPhotosUsedQuotaInMb,
				isSuspended,
				totalQuotaInMb,
				usedQuotaInMb,
				usedQuotaInPercentage
				);
		return report;
	}

	private Map<String, Object> createMap(List<Parameters> listaParametros) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Parameters parameter : listaParametros) {
			map.put(parameter.getName(), parameter.get(parameter.getName()));
			logger.info(String.format("createMap: %s = %s", parameter.getName(), parameter.get(parameter.getName())));
		}
		return map;
	}

	public synchronized String getDominio() {
		return dominio;
	}

	public synchronized void setDominio(String dominio) {
		this.dominio = dominio;
	}

}
