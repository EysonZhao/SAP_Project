package sme.perf.request.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class TestIssue {
	@Id
	@GeneratedValue(generator="IssueSeq")
    @SequenceGenerator(name = "IssueSeq", sequenceName = "Issue_Seq", allocationSize = 1, initialValue = 1)
	long id;
	
	private long testRequestId;
	private String title;
	private String url;		//the url could link to the issue
	private String status;
	private String priority; 
	private String creator; // the one who create the issue/incident
	private String processor; // the one who is the assignee or 
	private String jiraKey; //the incident id or JIRA jira id
	
	@Column(columnDefinition = "DateTime")
	@Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime createDate;
	
	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getJiraKey() {
		return jiraKey;
	}

	public void setJiraKey(String key) {
		this.jiraKey = key;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH})
    @PrimaryKeyJoinColumn(name="testRequestId")
	@JsonBackReference
	private TestRequest testRequest;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTestRequestId() {
		return testRequestId;
	}

	public void setTestRequestId(long testRequestId) {
		this.testRequestId = testRequestId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TestRequest getTestRequest() {
		return testRequest;
	}

	public void setTestRequest(TestRequest testRequest) {
		this.testRequest = testRequest;
	}
}
