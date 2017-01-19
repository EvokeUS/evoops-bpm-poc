package com.evoke.researchlabs.row.hr.domain;

import java.io.Serializable;

/**
 * POJO to hold BPM related configuration attributes.
 * 
 * @author bmohammad
 *
 */
public class BPMConfiguration implements Serializable{
	private static final long serialVersionUID = -4910694972857054819L;
	private String url;
	private String technicalUser;
	private String technicalPwd;
	private String userName;
	private String pwd;
	private String barFile;
	private String organizationFile;
	private String bdmFile;
	private String processName;
	
	public BPMConfiguration(){
		//Default Constructor
	}
	
	public BPMConfiguration(String url, String userName, String pwd) {
		super();
		this.url = url;
		this.userName = userName;
		this.pwd = pwd;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getBarFile() {
		return barFile;
	}

	public void setBarFile(String barFile) {
		this.barFile = barFile;
	}

	public String getOrganizationFile() {
		return organizationFile;
	}

	public void setOrganizationFile(String organizationFile) {
		this.organizationFile = organizationFile;
	}

	public String getBdmFile() {
		return bdmFile;
	}

	public void setBdmFile(String bdmFile) {
		this.bdmFile = bdmFile;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getTechnicalUser() {
		return technicalUser;
	}

	public void setTechnicalUser(String technicalUser) {
		this.technicalUser = technicalUser;
	}

	public String getTechnicalPwd() {
		return technicalPwd;
	}

	public void setTechnicalPwd(String technicalPwd) {
		this.technicalPwd = technicalPwd;
	}

	@Override
	public String toString() {
		return "BPMConfiguration [url=" + url + ", technicalUser=" + technicalUser + ", technicalPwd=" + technicalPwd
				+ ", userName=" + userName + ", pwd=" + pwd + ", barFile=" + barFile + ", organizationFile="
				+ organizationFile + ", bdmFile=" + bdmFile + ", processName=" + processName + "]";
	}
}
