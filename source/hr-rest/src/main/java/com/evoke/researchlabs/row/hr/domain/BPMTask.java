package com.evoke.researchlabs.row.hr.domain;

public class BPMTask {
	private String id;
	private String processId;
	private String caseId;
	private String parentCaseId;
	private String type;
	private String priority;
	private String state;
	private String displayDescription;
	private String dueDate;
	private String lastUpdateDate;
	private String displayName;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getParentCaseId() {
		return parentCaseId;
	}

	public void setParentCaseId(String parentCaseId) {
		this.parentCaseId = parentCaseId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public String toString() {
		return "BPMTask [id=" + id + ", processId=" + processId + ", caseId=" + caseId + ", parentCaseId="
				+ parentCaseId + ", type=" + type + ", priority=" + priority + ", state=" + state
				+ ", displayDescription=" + displayDescription + ", dueDate=" + dueDate + ", lastUpdateDate="
				+ lastUpdateDate + ", displayName=" + displayName + "]";
	}
}
