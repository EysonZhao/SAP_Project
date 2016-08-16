package sme.perf.execution.entity;

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
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JodaDateConverter;
import sme.perf.utility.JsonHelper;

@Entity
//@Converter(name = "jodaDateConverter", converterClass = JodaDateConverter.class)
public class ExecutionTaskInfo {
    @Id
    @GeneratedValue(generator = "ExecutionTaskInfo")
    @SequenceGenerator(name = "ExecutionTaskInfo", sequenceName = "EXECUTIONTASKINFO_SEQ", allocationSize = 1, initialValue = 1)
    private long id;

    private long parentId;
    
    @JsonBackReference(value="taskInfo-parent")
    ExecutionTaskInfo parent;
    
    @ManyToOne(optional=true)
    @JoinColumn(name="parentId", referencedColumnName="id")
    public ExecutionTaskInfo getParent(){
    	return parent;
    }
   
    public void setParent(ExecutionTaskInfo parent){
    	this.parent = parent;
    }
    
	@OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true)
	@JoinColumn(name="parentId")
	public List<ExecutionTaskInfo> getSubExecutionTaskInfoList(){
		return subExecutionTaskInfoList;
	}
	
	public void setSubExecutionTaskInfoList(List<ExecutionTaskInfo> subExecutionTaskInfoList){
		this.subExecutionTaskInfoList = subExecutionTaskInfoList;
	}
	
	@JsonManagedReference(value="taskInfo-parent")
    private List<ExecutionTaskInfo> subExecutionTaskInfoList;
    private long sn;
	public long getSn() {
		return sn;
	}

	public void setSn(long sn) {
		this.sn = sn;
	}

	@Column(nullable=true)
	private Long executionInfoId;
    
	@ManyToOne(optional=true,  fetch=FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name="executionInfoId")
    @JsonBackReference(value="taskInfo-executionInfo")
    private ExecutionInfo executionInfo;
    
    private String className;
    private String packageName;

	@Column(columnDefinition = "TEXT")
    private String taskParameterJson;

	@Column(columnDefinition = "TEXT")
    private String taskHostParameterJson;
    
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime startTime;
	
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime endTime;
	
	private Result result;
	
	@Transient
	private String durationStr;
	
    public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getDurationStr() {
		return PeriodFormat.getDefault().print(new Period(startTime, endTime));
	}

	public void setDurationStr(String durationStr) {
		this.durationStr = durationStr;
	}

	public long getId() {
		return id;
		
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
    public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getTaskParameterJson() {
		return taskParameterJson;
	}

	public void setTaskParameterJson(String taskParameterJson) {
		this.taskParameterJson = taskParameterJson;
	}

	public String getTaskHostParameterJson() {
		return taskHostParameterJson;
	}

	public void setTaskHostParameterJson(String taskHostParameterJson) {
		this.taskHostParameterJson = taskHostParameterJson;
	}
	
	public Long getExecutionInfoId() {
		return executionInfoId;
	}

	public void setExecutionInfoId(Long executionInfoId) {
		this.executionInfoId = executionInfoId;
	}

	public ExecutionInfo getExecutionInfo() {
		return executionInfo;
	}

	public void setExecutionInfo(ExecutionInfo executionInfo) {
		this.executionInfo = executionInfo;
	}
    public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	@Override
	public boolean equals(Object o){
		if(null == o)
			return false;
		if(o instanceof ExecutionTaskInfo &&
				((ExecutionTaskInfo) o).getId() == this.getId())
			return true;
		return false;
	}

	public ExecutionTaskInfo duplicateNew() {
		ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
		taskInfo.setClassName(this.className);
		taskInfo.setExecutionInfoId(this.getExecutionInfoId());
		taskInfo.setPackageName(this.getPackageName());
		taskInfo.setSn(this.getSn());
		taskInfo.setTaskHostParameterJson(taskHostParameterJson);
		taskInfo.setTaskParameterJson(taskParameterJson);
		taskInfo.setSn(sn);
		return taskInfo;
	}
	
	public ExecutionTaskInfo refreshExecutionId(){
		long newid = this.getExecutionInfoId();
		String taskParaJson = this.getTaskParameterJson();
		JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
		TaskParameterMap paramMap = jsonHelper.deserialObject(taskParaJson, TaskParameterMap.class);
		//***test to change executionId
		TaskParameterEntry entry = paramMap.getParameters().get("executionId");
		if(entry!=null){
			entry.setValue(String.valueOf(newid));
			taskParaJson = jsonHelper.serializeObject(paramMap);
			this.setTaskParameterJson(taskParaJson);
		}
		return this;
	}
	
}
