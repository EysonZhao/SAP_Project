package sme.perf.request.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SqlResultSetMapping(
		name="ZIssueRecentReportMap",
		entities={
			@EntityResult(
				entityClass=ZIssueFinalReportItem.class,
				fields={
					@FieldResult(name="id", column="id"),
					@FieldResult(name="jiraKey", column="jiraKey"),
					@FieldResult(name="priority", column="priority"),
					@FieldResult(name="status", column="status"),
					@FieldResult(name="title", column="title"),
					@FieldResult(name="projectName", column="projectName")
				}
			)
		}
	)



@Entity
@NamedNativeQueries({

	@NamedNativeQuery(name="ZgetLatestReportIssue", query="SELECT     T0.ID, T0.JIRAKEY, T0.TITLE, T0.PRIORITY, T0.STATUS, T0.CREATEDATE "
			+ "FROM         dbo.TESTISSUE AS T0 LEFT OUTER JOIN dbo.TESTREQUEST AS T1 ON T0.TESTREQUESTID = T1.ID "
			+ "LEFT OUTER JOIN dbo.PROJECT AS T2 ON T1.PROJECTID = T2.ID "
			+ "WHERE     (T2.ID = ?1) AND (T0.STATUS <> 'Closed') "
			+ "AND  T0.CREATEDATE between ?2 and ?3 "
			+ "AND T0.PRIORITY LIKE ?4 "
			+ "ORDER BY (CASE T0.PRIORITY WHEN 'Very High' THEN 1 WHEN 'High' THEN 2 WHEN 'Medium' THEN 3 ELSE 4 END)"
			+ ",CREATEDATE desc", 
			resultSetMapping="ZIssueRecentReportMap"),
	@NamedNativeQuery(name="ZgetOlderReportIssue", query="SELECT     T0.ID, T0.JIRAKEY, T0.TITLE, T0.PRIORITY, T0.STATUS, T0.CREATEDATE "
			+ "FROM   dbo.TESTISSUE AS T0 LEFT OUTER JOIN dbo.TESTREQUEST AS T1 ON T0.TESTREQUESTID = T1.ID LEFT OUTER JOIN dbo.PROJECT AS T2 ON T1.PROJECTID = T2.ID  "
			+ "WHERE    (T2.ID = ?1) AND (T0.STATUS <> 'Closed')  "
			+ "AND (DATEDIFF(DAY,CREATEDATE,GETDATE())<?2) AND (DATEDIFF(DAY,CREATEDATE,GETDATE())> ?3)"
			+ "ORDER BY (CASE T0.PRIORITY WHEN 'Very High' THEN 1 WHEN 'High' THEN 2 WHEN 'Medium' THEN 3 ELSE 4 END), CREATEDATE desc", 
			resultSetMapping="ZIssueRecentReportMap")
	
	
})
public class ZIssueFinalReportItem {
	@Id
	long id;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	String jiraKey;
	String priority;
	String status;
	String title;
	
	@Column(columnDefinition = "DateTime")
	@Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	DateTime createDate;
		
	
}
