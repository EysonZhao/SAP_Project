package sme.perf.execution.impl;

import org.joda.time.DateTime;

import sme.perf.dao.GenericDao;
import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionGroupInfo;
import sme.perf.task.Result;

public class RunExecutionScheduler {

	private ExecutionGroupInfo execScheduler;
	private boolean isStop = false;
	
	public RunExecutionScheduler(ExecutionGroupInfo execScheduler){
		this.execScheduler = execScheduler;
	}
	
	public Result Execute() {
		String[] strIdList = execScheduler.getExecutionIdList().split(";");
		execScheduler.setStartDate(DateTime.now());
		execScheduler.setState(State.Running);
		GenericDao<ExecutionGroupInfo> dao = new GenericDao<ExecutionGroupInfo>(ExecutionGroupInfo.class);
		execScheduler = dao.update(execScheduler);
		try{
			for(int i=0 ; i<strIdList.length ; i++){
				long execId = Long.parseLong(strIdList[i].trim());
				ExecutionInfoDao execInfoDao = new ExecutionInfoDao();
				ExecutionInfo  execInfo = execInfoDao.getByID(execId);
				RunExecution runExec = new RunExecution();
				Result execResult = runExec.Execute(execInfo);
			}
			execScheduler.setState(State.Finished);
			execScheduler.setResult(Result.Pass);
		}
		catch(Exception ex){
			execScheduler.setState(State.Failed);
			execScheduler.setResult(Result.Fail);
		}
		finally{
			execScheduler.setEndDate(DateTime.now());
			dao.update(execScheduler);
		}
		return execScheduler.getResult();
	}

	public State getState() {
		return execScheduler.getState();
	}

	public Result getResult() {
		return execScheduler.getResult();
	}

}
