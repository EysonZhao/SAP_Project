package sme.perf.request.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import sme.perf.entity.Project;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.utility.JodaDateConverter;
import sme.perf.utility.LogHelper;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
//@Converter(name = "jodaDateConverter", converterClass = JodaDateConverter.class)
public class TestRequest implements Cloneable{
	@Id
    @GeneratedValue(generator = "TestRequestSeq")
    @SequenceGenerator(name = "TestRequestSeq", sequenceName = "TESTREQUEST_SEQ", allocationSize = 1, initialValue = 1)
    private long id;
 
    private String name;
    private long projectId;
    private String category;
    private String priority;
    
    @Column(length=4000)
    private String background;
    
    @Column(length=4000)
    private String overallRequirement;
    
    @Column(length=4000)
    private String detailStep;
    private String companyDBName;
    private String companyDBNumber;
    private String userPerCompany;
    private String totalUsers;
    private String testDuration;
    private String rampupMode;
    private String thinktime;
    
    @Column(length=4000)
    private String specialSetting;
    
    @Column(length=4000)
    private String others;
    @Column(length=4000)
    private String monitorObject;
    
    @Column(length=4000)
    private String kpi;
    
    @Column(length=4000)
    private String server;
    
    @Column(length=4000)
    private String client;
    private String build;
    private String requester;
    private String testOwner;
    private String stakeholder;
    private String latestStatus;
    
    @OneToMany(mappedBy = "testRequest", fetch=FetchType.LAZY,  cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TestIssue> issueList;

	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime lastUpdateDate;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="projectId")
    @JsonBackReference
    //@JsonIgnore
    private Project project;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="testRequest")
    @JsonIgnore
    private List<ExecutionInfo> executionList;
    
	@OneToMany(mappedBy = "testRequest", cascade = CascadeType.ALL)//, fetch = FetchType.EAGER)
	//@JsonManagedReference
	@JsonIgnore
    private List<StatusUpdate> statusUpdateList = new ArrayList<StatusUpdate>();
	
	@OneToMany(mappedBy = "testRequest", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Attachment> attachmentList = new ArrayList<Attachment>();
	

	/*** 7.26 Yansong added for TA tools develop interface***/
	private String codeline;
	private String PLNumber;
	private long CLNumber;
	private boolean isHana;
	private boolean isBrowserAccess;
	/*** Added over ***/
	
	public String getProjectName(){
		if(null != this.project){
			return this.project.getName();
		}
		return "";
	}
	
	@Override
	public Object clone() {
		TestRequest retRequest = null;
		try {
			retRequest = (TestRequest) super.clone();
			retRequest.setId(0);
			retRequest.setAttachmentList(null);
			retRequest.setExecutionList(null);
			retRequest.setIssueList(null);
			retRequest.setStatusUpdateList(null);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			LogHelper.error(e);
		}

		return retRequest;
	}
}
