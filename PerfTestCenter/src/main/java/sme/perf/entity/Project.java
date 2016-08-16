package sme.perf.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import sme.perf.request.entity.TestRequest;

@Entity
public class Project {
	@Id
	@GeneratedValue(generator="ProjectSeq")
    @SequenceGenerator(name = "ProjectSeq", sequenceName = "PROJECT_REQ", allocationSize = 1, initialValue = 1)
	private long id;
	
	private String name;
	
	@Column(length=4000)
	private String description;
	
	@Column(columnDefinition = "TEXT")
	private String parameterTemplateJson;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="project")
	@JsonManagedReference
	//@JsonIgnore
	private  List<Scenario> scenarios ;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="project")
	@JsonManagedReference
	//@JsonIgnore
	private List<TestRequest> testRequests;
	
	public List<TestRequest> getTestRequests() {
		return testRequests;
	}
	public void setTestRequests(List<TestRequest> testRequests) {
		this.testRequests = testRequests;
	}
	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getParameterTemplateJson() {
		return parameterTemplateJson;
	}
	public void setParameterTemplateJson(String parameterTemplateJson) {
		this.parameterTemplateJson = parameterTemplateJson;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}
//	public void setScenarios(List<Scenario> scenarios) {
//		this.scenarios = scenarios;
//	}
}
