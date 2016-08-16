package sme.perf.execution;

import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.task.Result;

public interface Execution {
	
	public Result Execute(ExecutionInfo info);
	
	public void Stop();
	
	public void Pause();
	
	public void Continue();
	
	public State getState();
	
	public Result getResult();
	
}
