package sme.perf.execution.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Scenario;
import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;
import sme.perf.utility.TaskServiceProvider;
import sme.perf.task.TaskParameterMap;

public class RunExecution implements Execution {

	private State state;
	private Result result;	
	private Logger logger;
	private boolean isStop = false;
	private boolean isPause = false;
	
	public RunExecution(){
		this.state = State.New;
		result = Result.Unknown;
	}
	
	@Override
	public Result Execute(ExecutionInfo execInfo) {
		logger = getExecutionLogger(execInfo.getId());
		this.state = State.Running;
		execInfo.setStartDate(DateTime.now());
		execInfo.setState(State.Running);
		ExecutionInfoDao dao = new ExecutionInfoDao();
		dao.update(execInfo);
		
		logger.info("Execution Start");
		int taskNum=0;
		//in order to sort task list, need to create a new List, which is sortable.
		List<ExecutionTaskInfo> tasks =  new ArrayList<ExecutionTaskInfo>();
		for(int i=0 ; i<execInfo.getTasks().size() ; i++){
			tasks.add(execInfo.getTasks().get(i));
		}
		Collections.sort(tasks, new ExecutionTaskInfoSorter());
		
		execInfo.setTasks(tasks);
		GenericDao<Scenario> scenarioDao = new GenericDao<Scenario>(Scenario.class);
		Scenario scenario = scenarioDao.getByID(execInfo.getScenarioId());
		for(ExecutionTaskInfo taskInfo : execInfo.getTasks()){			
			if(taskNum!=0){
			//create an interval time buffer.
				int interval = execInfo.getInterval()*1000;
				try {
					logger.error(String.format("Task interval for %d seconds!", execInfo.getInterval()));
					Thread.sleep(interval);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(isPause){
				logger.info(String.format("Execution#%d is paused after task#%d is done.", execInfo.getId(), taskNum));
				while(isPause){
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
			
			if(isStop){
				break;
			}
			
			try {
				Task task = TaskServiceProvider.createTaskInstance(taskInfo.getClassName(), taskInfo.getPackageName());
				task.setLogger(logger);
				JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
				TaskParameterMap paramMap = jsonHelper.deserialObject(taskInfo.getTaskParameterJson(), TaskParameterMap.class);

				//set task parameters and add the 'scenario' & 'executionId' parameters
				if(paramMap.getParameters().containsKey("scenario") == false){
					paramMap.getParameters().put("scenario", new TaskParameterEntry(scenario.getName(), "scenario name"));
				}
				if(paramMap.getParameters().containsKey("executionId") == false){
					paramMap.getParameters().put("executionId", new TaskParameterEntry(String.format("%d", execInfo.getId()), "execution info id."));
				}
				
				task.setParameters(paramMap);
				TaskParameterMap hostMap = jsonHelper.deserialObject(taskInfo.getTaskHostParameterJson(), TaskParameterMap.class);
				task.setHosts(hostMap);
				
				logger.info(getTaskStateString(execInfo, taskNum, taskInfo, State.Running, null));
				taskInfo.setStartTime(DateTime.now());
				Result taskResult = task.execute();
				taskInfo.setEndTime(DateTime.now());
				taskInfo.setResult(taskResult);
				
				taskInfo.setSubExecutionTaskInfoList(task.getSubExecutionTaskInfoList());
				if(taskResult == Result.Fail){
					this.result = Result.Fail;
					this.state = State.Failed;
					logger.info(getTaskStateString(execInfo, taskNum, taskInfo, null, Result.Fail));
					break;
				}
				else{
					logger.info(getTaskStateString(execInfo, taskNum, taskInfo, null, Result.Pass));
				}
			} catch (InstantiationException | IllegalAccessException| ParameterMissingException  e) {
				logger.error(String.format("execution#%d is failed.", execInfo.getId()));
				logger.error(e);
				this.result = Result.Fail;
				this.state = State.Failed;
				taskInfo.setResult(Result.Fail);
				break;
			}
			
			taskNum++;			
		}
		if(this.isStop){
			this.state = State.Terminated;
			this.result = Result.Unknown;	
			logger.info(String.format("execution#%d is %s after task#%d is done.", execInfo.getId(), this.state.toString(),taskNum-1));
		}
		
		if(this.state == State.Running){
			this.state = State.Finished;
			this.result=Result.Pass;
		}
		
		execInfo.setResult(this.result);
		execInfo.setState(this.state);
		execInfo.setEndDate(DateTime.now());
		dao.update(execInfo);
		
		logger.info(String.format("execution#%d is %s", execInfo.getId(), this.result.toString()));
		
		return this.result;
	}

	private String  getTaskStateString(ExecutionInfo info, int taskNum,
			ExecutionTaskInfo taskInfo, State state, Result result) {
		if(result == null){
			return String.format("execution#%d task:#%d %s is %s", info.getId(), taskNum, taskInfo.getClassName(), state.toString());
		}
		else{
			return String.format("execution#%d task:#%d %s is %s", info.getId(), taskNum, taskInfo.getClassName(), result.toString());
		}
	}
	
	private Logger getExecutionLogger(long execId){
		Logger logger = Logger.getLogger(this.getClass().getName() + execId);
		logger.setLevel(Level.DEBUG);
		File file = new File(PropertyFile.getValue("LoggerPath") + execId + File.separator);
		file.mkdirs();
		file = new File(PropertyFile.getValue("LoggerPath") + execId + File.separator + "execution.txt");
		
		try {
			logger.removeAllAppenders();
			Layout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t] %m%n");
			FileAppender appender = new FileAppender(patternLayout, file.getAbsolutePath(), false);
			
			logger.addAppender(appender);
			logger.addAppender(new ConsoleAppender(patternLayout));
		} catch (IOException e) {
			LogHelper.error(e);
		}
		return logger;
	}

	@Override
	public State getState() {
		return state;
	}
	
	public void setState(State mstate) {
		this.state = mstate;
	}

	@Override
	public Result getResult() {
		return result;
	}

	@Override
	public void Stop(){
		this.state=State.Terminated;
		isStop = true;
	}

	@Override
	public void Pause() {
		this.state=State.Paused;
		isPause = true;
	}


	@Override
	public void Continue() {
		this.state=State.Running;
		isPause = false;
	}
	
}
