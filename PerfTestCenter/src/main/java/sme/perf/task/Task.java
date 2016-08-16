package sme.perf.task;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sme.perf.execution.entity.ExecutionTaskInfo;

public abstract class Task implements ITask{
	protected Logger logger;
	
	public void setLogger(Logger logger){
		this.logger = logger;
	}
	
	public abstract Result execute() throws ParameterMissingException;
		
	protected TaskParameterMap parameters;
	
	public void setParameters(TaskParameterMap parameters){
		this.parameters = parameters;
	}
	
	public TaskParameterMap getParameters(){
		return this.parameters;
	}
	
	protected TaskParameterMap hostParameters;
	
	public void setHosts(TaskParameterMap hostList){
		this.hostParameters = hostList;
	}
	
	public TaskParameterMap getHosts(){
		return this.hostParameters;
	}
	
	protected Status status = Status.New;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	//public abstract void setParameters(TaskParameterMap parameters);
	
	//public abstract TaskParameterMap getParameters();
	
	//public abstract Status getStatus();

	//public abstract void setHosts(TaskParameterMap hostList);
	
	//public abstract TaskParameterMap getHosts();
	
	///return the description of this task. what to do.
	public abstract String getDescription();
	
	public List<ExecutionTaskInfo> getSubExecutionTaskInfoList(){
		return null;
	}
	}
