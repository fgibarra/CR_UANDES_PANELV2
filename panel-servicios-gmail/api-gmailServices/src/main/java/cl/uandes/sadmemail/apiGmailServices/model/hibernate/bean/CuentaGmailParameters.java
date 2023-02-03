package cl.uandes.sadmemail.apiGmailServices.model.hibernate.bean;

import java.io.Serializable;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SendmailParams")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CuentaGmailParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6691983177011434600L;
	private Long key;
	private String smtpServer;
	private String emailSender;
	private String smtpUser;
	private String smtpPassword;
	private String webmaster;
	private String support;
	private String gmailAdmin;
	private String gmailPasswd;
	private String gmailDominio;
	
	public CuentaGmailParameters() {
		super();
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	@XmlElement
	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	@XmlElement
	public String getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	@XmlElement
	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	@XmlElement
	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	@XmlElement
	public String getWebmaster() {
		return webmaster;
	}

	public void setWebmaster(String webmaster) {
		this.webmaster = webmaster;
	}

	@XmlElement
	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	@XmlElement
	public String getGmailAdmin() {
		return gmailAdmin;
	}

	public void setGmailAdmin(String gmailAdmin) {
		this.gmailAdmin = gmailAdmin;
	}

	@XmlElement
	public String getGmailPasswd() {
		return gmailPasswd;
	}

	public void setGmailPasswd(String gmailPasswd) {
		this.gmailPasswd = gmailPasswd;
	}

	@XmlElement
	public String getGmailDominio() {
		return gmailDominio;
	}

	public void setGmailDominio(String gmailDominio) {
		this.gmailDominio = gmailDominio;
	}	
}
