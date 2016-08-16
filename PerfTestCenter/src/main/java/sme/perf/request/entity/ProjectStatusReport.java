package sme.perf.request.entity;

import java.util.List;

public class ProjectStatusReport{
	private String projectName;
	private List<StatusReportItem> blockedTestRequests;
	private List<StatusReportItem> activeTestRequests;
	private List<StatusReportItem> planTestRequests;
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<StatusReportItem> getBlockedTestRequests() {
		return blockedTestRequests;
	}

	public void setBlockedTestRequests(List<StatusReportItem> blockTestRequests) {
		this.blockedTestRequests = blockTestRequests;
	}

	public List<StatusReportItem> getActiveTestRequests() {
		return activeTestRequests;
	}

	public void setActiveTestRequests(List<StatusReportItem> activeTestRequests) {
		this.activeTestRequests = activeTestRequests;
	}

	public List<StatusReportItem> getPlanTestRequests() {
		return planTestRequests;
	}

	public void setPlanTestRequests(List<StatusReportItem> planTestRequests) {
		this.planTestRequests = planTestRequests;
	}

	public List<TestIssue> getIssues() {
		return issues;
	}

	public void setIssues(List<TestIssue> issues) {
		this.issues = issues;
	}

	private List<TestIssue> issues;
	
}