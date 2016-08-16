package sme.perf.request.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;

@SqlResultSetMapping(
	name="statusReportMap",
	entities={
		@EntityResult(
			entityClass=StatusReportItem.class,
			fields={
				@FieldResult(name="id", column="id"),
				@FieldResult(name="projectName", column="ProjectName"),
				@FieldResult(name="testRequestName", column="TestRequestName"),
				@FieldResult(name="testRequestId", column="TestRequestId"),
				@FieldResult(name="latestStatus", column="LatestStatus"),
				@FieldResult(name="lastUpdateDate", column="LastUpdateDate"),
				@FieldResult(name="priority", column="Priority")
			}
		)
	}
)

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(name="blockedTestRequest", query="  SELECT T1.id, T2.NAME as projectName, T1.NAME as testRequestName, T1.id as testRequestId, T1.latestStatus, T1.lastUpdateDate, T1.[Priority] " +
			"  FROM [TESTREQUEST] T1 left outer join [Project] T2 on T1.PROJECTID = T2.ID  " +
			"  WHERE T1.LATESTSTATUS = 'Blocked' and T2.id = ?1 " +
			"  ORDER BY projectName, latestStatus, [Priority], lastUpdateDate", resultSetMapping="statusReportMap"),
	@NamedNativeQuery(name="activeTestRequest", query="  SELECT T1.id, T2.NAME as projectName, T1.NAME as testRequestName, T1.id as testRequestId, T1.latestStatus, T1.lastUpdateDate, T1.[Priority] " +
			"  FROM [TESTREQUEST] T1 left outer join [Project] T2 on T1.PROJECTID = T2.ID  " +
			"  WHERE T1.LATESTSTATUS not in ('New', 'Plan')  and T1.LASTUPDATEDATE>= ?1 AND T1.LASTUPDATEDATE <= ?2  and T2.id = ?3 " +
			"  ORDER BY projectName, latestStatus, [Priority], lastUpdateDate", resultSetMapping="statusReportMap"),
	@NamedNativeQuery(name="planTestRequest", query="  SELECT T1.id, T2.NAME as projectName, T1.NAME as testRequestName, T1.id as testRequestId, T1.latestStatus, T1.lastUpdateDate, T1.[Priority] " +
			"  FROM [TESTREQUEST] T1 left outer join [Project] T2 on T1.PROJECTID = T2.ID  " +
			"  WHERE T1.LATESTSTATUS in ('Plan', 'Prepare', 'Testing', 'Pending')  and T1.LASTUPDATEDATE <= ?2 and T2.id = ?3 " +
			"  ORDER BY projectName, latestStatus, [Priority], lastUpdateDate", resultSetMapping="statusReportMap")
})
public class StatusReportItem {
	@Id
	@GeneratedValue(generator="StatusReportItem")
    @SequenceGenerator(name = "StatusReportItem", sequenceName = "StatusReportItem_SEQ", allocationSize = 1, initialValue = 1)
	long id;
	private long testRequestId;
	
	private String projectName;
	private String testRequestName;
	
	private String latestStatus;
	
	@Column(columnDefinition = "DateTime")
	@Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime lastUpdateDate;
	
	private String priority;
	private String issueJson;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getTestRequestName() {
		return testRequestName;
	}
	public void setTestRequestName(String testRequestName) {
		this.testRequestName = testRequestName;
	}
	public long getTestRequestId() {
		return testRequestId;
	}
	public void setTestRequestId(long testRequestId) {
		this.testRequestId = testRequestId;
	}
	public String getLatestStatus() {
		return latestStatus;
	}
	public void setLatestStatus(String latestStatus) {
		this.latestStatus = latestStatus;
	}
	public DateTime getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(DateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
}
