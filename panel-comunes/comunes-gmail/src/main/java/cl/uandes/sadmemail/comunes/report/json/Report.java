package cl.uandes.sadmemail.comunes.report.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4434195896562110534L;
	@JsonProperty("userEmail")
	private String userEmail;
	@JsonProperty("last_login_time")
	private String lastLoginTime;
	@JsonProperty("creation_time")
	private String creationTime;
	@JsonProperty("is_disabled")
	private Boolean isDisabled;
	@JsonProperty("gmail_used_quota_in_mb")
	private Long gmailUsedQuotaInMb;
	@JsonProperty("drive_used_quota_in_mb")
	private Long driveUsedQuotaInMb;
	@JsonProperty("gplus_photos_used_quota_in_mb")
	private Long gplusPhotosUsedQuotaInMb;
	@JsonProperty("is_suspended")
	private Boolean isSuspended;
	@JsonProperty("total_quota_in_mb")
	private Long totalQuotaInMb;
	@JsonProperty("used_quota_in_mb")
	private Long usedQuotaInMb;
	@JsonProperty("used_quota_in_percentage")
	private Long usedQuotaInPercentage;

	@JsonCreator
	public Report(
			@JsonProperty("userEmail")String userEmail,
			@JsonProperty("last_login_time")String lastLoginTime,
			@JsonProperty("creation_time")String creationTime,
			@JsonProperty("is_disabled")Boolean isDisabled,
			@JsonProperty("gmail_used_quota_in_mb")Long gmailUsedQuotaInMb, 
			@JsonProperty("drive_used_quota_in_mb")Long driveUsedQuotaInMb, 
			@JsonProperty("gplus_photos_used_quota_in_mb")Long gplusPhotosUsedQuotaInMb,
			@JsonProperty("is_suspended")Boolean isSuspended, 
			@JsonProperty("total_quota_in_mb")Long totalQuotaInMb, 
			@JsonProperty("used_quota_in_mb")Long usedQuotaInMb, 
			@JsonProperty("used_quota_in_percentage")Long usedQuotaInPercentage) {
		super();
		this.userEmail = userEmail;
		this.lastLoginTime = lastLoginTime;
		this.creationTime = creationTime;
		this.isDisabled = isDisabled;
		this.gmailUsedQuotaInMb = gmailUsedQuotaInMb;
		this.driveUsedQuotaInMb = driveUsedQuotaInMb;
		this.gplusPhotosUsedQuotaInMb = gplusPhotosUsedQuotaInMb;
		this.isSuspended = isSuspended;
		this.totalQuotaInMb = totalQuotaInMb;
		this.usedQuotaInMb = usedQuotaInMb;
		this.usedQuotaInPercentage = usedQuotaInPercentage;
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public Long getGmailUsedQuotaInMb() {
		return gmailUsedQuotaInMb;
	}

	public void setGmailUsedQuotaInMb(Long gmailUsedQuotaInMb) {
		this.gmailUsedQuotaInMb = gmailUsedQuotaInMb;
	}

	public Long getDriveUsedQuotaInMb() {
		return driveUsedQuotaInMb;
	}

	public void setDriveUsedQuotaInMb(Long driveUsedQuotaInMb) {
		this.driveUsedQuotaInMb = driveUsedQuotaInMb;
	}

	public Long getGplusPhotosUsedQuotaInMb() {
		return gplusPhotosUsedQuotaInMb;
	}

	public void setGplusPhotosUsedQuotaInMb(Long gplusPhotosUsedQuotaInMb) {
		this.gplusPhotosUsedQuotaInMb = gplusPhotosUsedQuotaInMb;
	}

	public Boolean getIsSuspended() {
		return isSuspended;
	}

	public void setIsSuspended(Boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	public Long getTotalQuotaInMb() {
		return totalQuotaInMb;
	}

	public void setTotalQuotaInMb(Long totalQuotaInMb) {
		this.totalQuotaInMb = totalQuotaInMb;
	}

	public Long getUsedQuotaInMb() {
		return usedQuotaInMb;
	}

	public void setUsedQuotaInMb(Long usedQuotaInMb) {
		this.usedQuotaInMb = usedQuotaInMb;
	}

	public Long getUsedQuotaInPercentage() {
		return usedQuotaInPercentage;
	}

	public void setUsedQuotaInPercentage(Long usedQuotaInPercentage) {
		this.usedQuotaInPercentage = usedQuotaInPercentage;
	}

	public synchronized String getUserEmail() {
		return userEmail;
	}

	public synchronized void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public synchronized String getLastLoginTime() {
		return lastLoginTime;
	}

	public synchronized void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public synchronized String getCreationTime() {
		return creationTime;
	}

	public synchronized void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public synchronized Boolean getIsDisabled() {
		return isDisabled;
	}

	public synchronized void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}
