package sme.perf.request.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SqlResultSetMapping(
		name="jiraReportMap",
		entities={
			@EntityResult(
				entityClass=TestIssueReportItem.class,
				fields={
					@FieldResult(name="id", column="id"),
					@FieldResult(name="creator", column="creator"),
					@FieldResult(name="jiraKey", column="jiraKey"),
					@FieldResult(name="priority", column="priority"),
					@FieldResult(name="processor", column="processor"),
					@FieldResult(name="status", column="status"),
					@FieldResult(name="title", column="title"),
					@FieldResult(name="url", column="url"),
					@FieldResult(name="createDate", column="createDate"),
					@FieldResult(name="testRequestId", column="testRequestId"),
					@FieldResult(name="testRequestName", column="testRequestName"),
					@FieldResult(name="projectName", column="projectName")
				}
			)
		}
	)

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(name="getAllTestIssue", query="select T0.id, T0.creator, T0.jiraKey, T0.priority, T0.processor, T0.status, T0.testRequestId, T0.title, T0.url, T0.createDate, T1.NAME as requestName, T2.NAME as projectName "+
			" from dbo.TESTISSUE T0 left outer join dbo.TESTREQUEST T1 on T0.TESTREQUESTID = T1.ID Left outer join dbo.PROJECT T2 on T1.PROJECTID = T2.ID ", resultSetMapping="jiraReportMap")
})
public class TestIssueReportItem {
	@Id
	long id;
	String creator;
	String jiraKey;
	String priority;
	String processor;
	String status;
	long testReqeustId;
	String title;
	String url;
	
	@Column(columnDefinition = "DateTime")
	@Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	DateTime createDate;
	
	String requestName;
	String projectName;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getJiraKey() {
		return jiraKey;
	}
	public void setJiraKey(String jiraKey) {
		this.jiraKey = jiraKey;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getTestReqeustId() {
		return testReqeustId;
	}
	public void setTestReqeustId(long testReqeustId) {
		this.testReqeustId = testReqeustId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public DateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}
	public String getRequestName() {
		return requestName;
	}
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
