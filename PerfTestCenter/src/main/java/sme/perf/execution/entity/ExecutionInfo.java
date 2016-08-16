package sme.perf.execution.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import sme.perf.analysis.entity.UserNumbers;
import sme.perf.entity.Scenario;
import sme.perf.execution.State;
import sme.perf.request.entity.TestRequest;
import sme.perf.task.Result;

@Getter
@Setter
@Entity
//@Converter(name = "jodaDateConverter", converterClass = JodaDateConverter.class)
public class ExecutionInfo {
    @Id
    @GeneratedValue(generator = "ExecutionInfo")
    @SequenceGenerator(name = "ExecutionInfo", sequenceName = "EXECUTION_SEQ", allocationSize = 1, initialValue = 1)
    private long id;
    private String name;
	private long testRequestId;
    private long scenarioId;
    private long isTimeAnalyse;
    private State state;
    private Result result; 
    @Column(columnDefinition = "int default 10800")
    private int timeOut;

	public int getTimeOut() {
		return timeOut==0?10800:timeOut;
	}
	
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime createDate;
	
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")	
    private DateTime startDate;
	
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime endDate;
	
    @Column(columnDefinition = "TEXT")
    private String projectParameterJson;
    
    @Column(columnDefinition = "TEXT")
    private String hostsParameterJson;
    
    @Column(columnDefinition = "TEXT")
    private String tasksParameterJson;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="testRequestId")
    @JsonIgnore
    private TestRequest testRequest;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="scenarioId")
    @JsonIgnore
    private Scenario scenario;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="executionInfo", cascade={CascadeType.ALL})
    @JsonManagedReference(value="taskInfo-executionInfo")
    private List<ExecutionTaskInfo> tasks;
    
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="executionInfo", cascade={CascadeType.ALL})
    @JsonManagedReference(value="result-executionInfo")
    private List<ExecutionResultInfo> resultList;
    
    private int interval;

	
	public ExecutionInfo duplicateNew(){
		ExecutionInfo newExecInfo = new ExecutionInfo();
		newExecInfo.setCreateDate(DateTime.now());
		newExecInfo.setHostsParameterJson(this.getHostsParameterJson());
		newExecInfo.setName(createDefaultNewName(this.getName()));
		newExecInfo.setProjectParameterJson(this.getProjectParameterJson());
		newExecInfo.setScenarioId(this.getScenarioId());
		newExecInfo.setState(State.New);
		newExecInfo.setTasksParameterJson(this.getTasksParameterJson());
		newExecInfo.setTestRequestId(this.getTestRequestId());
		newExecInfo.setTasks(new ArrayList<ExecutionTaskInfo>());
		newExecInfo.setInterval(this.getInterval());
		newExecInfo.setTimeOut(this.getTimeOut());
		newExecInfo.setIsTimeAnalyse(this.getIsTimeAnalyse());
		for(ExecutionTaskInfo taskInfo : this.getTasks()){
			ExecutionTaskInfo newTaskInfo = taskInfo.duplicateNew();
			newTaskInfo.setExecutionInfoId(null);
			newExecInfo.getTasks().add(newTaskInfo);
		}
		return newExecInfo;
	}
	
	private static String createDefaultNewName(String oldName){
		Pattern p = Pattern.compile("#(\\d*).*$");
		Matcher m = p.matcher(oldName); 
		StringBuffer time=new StringBuffer("[");
		DateTime now=DateTime.now();
		int month=now.getMonthOfYear();
		int day = now.getDayOfMonth();
		int hour =now.getHourOfDay();
		int minute=now.getMinuteOfHour();
		fommatTime(month,time);
		fommatTime(day,time);
		time.append("-");
		fommatTime(hour,time);
		fommatTime(minute,time);
		time.append("]");
		
		if(m.find())
		{
			String part = oldName.substring(oldName.lastIndexOf('#')+1,oldName.length());
			int num = Integer.parseInt(part.split(":")[0]);
			
			return String.format("%s#%d:%s", oldName.substring(0, oldName.lastIndexOf('#')), (num+1),time);
		}else{
			return oldName + "#1:" + time;
		}
	}
	private static void fommatTime(int n,StringBuffer sb){
		if(n<10){
			sb.append(0);
		}
		sb.append(n);
	}
}
