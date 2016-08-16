package sme.perf.request.entity;

import java.util.List;

import javax.persistence.Column;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class StatusReport {
	
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime startDate;
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime endDate;
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime reportDate;
	
	private List<ProjectStatusReport> projectStatusReportList;
	
	public List<ProjectStatusReport> getProjectStatusReportList() {
		return projectStatusReportList;
	}

	public void setProjectStatusReportList(List<ProjectStatusReport> projectStatusReportList) {
		this.projectStatusReportList = projectStatusReportList;
	}
	
	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public DateTime getReportDate() {
		return reportDate;
	}

	public void setReportDate(DateTime reportDate) {
		this.reportDate = reportDate;
	}
}
