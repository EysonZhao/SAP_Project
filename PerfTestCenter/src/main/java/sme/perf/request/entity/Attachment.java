package sme.perf.request.entity;

import java.sql.Timestamp;
import java.sql.Date;

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
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sme.perf.utility.JodaDateConverter;

@Entity
public class Attachment {
	@Id
	@GeneratedValue(generator="AttachmentSeq")
    @SequenceGenerator(name = "AttachmentSeq", sequenceName = "Attachment_Seq", allocationSize = 1, initialValue = 1)
	private long id;
	private String fileName;
	private long testRequestId;
	
//	@Column(columnDefinition = "DateTime")
//	@Convert("jodaDateConverter")
//	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
//	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
//	private DateTime uploadDate;
//	the code above will be deprecated
	@Column(name="uploadDate")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Timestamp uploadDate;
	
	
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getTestRequestId() {
		return testRequestId;
	}

	public void setTestRequestId(long testRequestId) {
		this.testRequestId = testRequestId;
	}

	public Timestamp getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
	}

	public TestRequest getTestRequest() {
		return testRequest;
	}

	public void setTestRequest(TestRequest testRequest) {
		this.testRequest = testRequest;
	}
}
