package sme.perf.entity;

import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import sme.perf.execution.entity.ExecutionInfo;

@Entity
public class Scenario {
	@Id
	@GeneratedValue(generator="ScenarioSeq")
    @SequenceGenerator(name = "ScenarioSeq", sequenceName = "SCENARIO_REQ", allocationSize = 1, initialValue = 1)
	private long id;
	
	private long projectId;
	private String name;
	@Column(length=4000)
	private String description;
	@Column(columnDefinition = "TEXT")
	private String taskListJson;
	/// a test scenario could be a packed into a package to include the scripts
	/// private String packagePath;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@PrimaryKeyJoinColumn(name="projectId")
	@JsonBackReference
	//@JsonIgnore
	private Project project;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="scenario")
	@JsonIgnore
	private List<ExecutionInfo> executionInfos;
	
	public List<ExecutionInfo> getExecutionInfos() {
		return executionInfos;
	}
	public void setExecutionInfos(List<ExecutionInfo> executionInfos) {
		this.executionInfos = executionInfos;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
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
	public String getTaskListJson() {
		return taskListJson;
	}
	public void setTaskListJson(String taskListJson) {
		this.taskListJson = taskListJson;
	}
	public String getProjectName(){
		if(null != this.project){
			return this.project.getName();
		}
		return "";
	}
	public void setProjectName(){
		
	}
}
