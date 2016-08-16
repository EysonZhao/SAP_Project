package sme.perf.request.entity;

import java.util.List;

public class ZIssueFinalReport {
	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public List<ZIssueFinalReportItem> getNearlySubmittedList() {
		return NearlySubmittedList;
	}

	public void setNearlySubmittedList(List<ZIssueFinalReportItem> nearlySubmittedList) {
		NearlySubmittedList = nearlySubmittedList;
	}

	public List<ZIssueFinalReportItem> getOlderSubmittedList() {
		return OlderSubmittedList;
	}

	public void setOlderSubmittedList(List<ZIssueFinalReportItem> olderSubmittedList) {
		OlderSubmittedList = olderSubmittedList;
	}

	private String ProjectName;
	
	private List<ZIssueFinalReportItem> NearlySubmittedList;
	
	private List<ZIssueFinalReportItem> OlderSubmittedList;
	
}
