package sme.perf.task;
import sme.perf.execution.entity.ExecutionTaskInfo;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public interface ITask{
	
	public Result execute() throws ParameterMissingException;
	
	public void setParameters(TaskParameterMap parameters);
	
	public TaskParameterMap getParameters();
	
	public Status getStatus();

	public void setHosts(TaskParameterMap hostList);
	
	public TaskParameterMap getHosts();
	
	///return the description of this task. what to do.
	public String getDescription();
	
	public void setLogger(Logger logger);
	
	public List<ExecutionTaskInfo> getSubExecutionTaskInfoList();
}
