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
import org.eclipse.persistence.annotations.Converter;
import javax.persistence.SequenceGenerator;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sme.perf.utility.JodaDateConverter;

@Entity
@Converter(name = "jodaDateConverter", converterClass = JodaDateConverter.class)
public class StatusUpdate {
	@Id
	@GeneratedValue(generator="StatusUpdateSeq")
    @SequenceGenerator(name = "StatusUpdateSeq", sequenceName = "STATUSUPDATE_REQ", allocationSize = 1, initialValue = 1)
	private long id;
	private String status;
	
	@Column(length=4000)
	private String description;
	private String issue;
	private long testRequestId;
	
	public long getTestRequestId() {
		return testRequestId;
	}
	public void setTestRequestId(long testRequestId) {
		this.testRequestId = testRequestId;
	}
	
	@Column(columnDefinition = "DateTime")
	@Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	private DateTime date;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH})
    @PrimaryKeyJoinColumn(name="testRequestId")
	@JsonIgnore
	private TestRequest testRequest;
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public DateTime getDate() {
		return date;
	}
	
	public void setDate(DateTime date) {
		this.date = date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	public TestRequest getTestRequest() {
		return testRequest;
	}
	public void setTestRequest(TestRequest testRequest) {
		this.testRequest = testRequest;
	}
}
