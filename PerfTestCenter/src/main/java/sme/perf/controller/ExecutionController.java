package sme.perf.controller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Category;
import sme.perf.entity.Scenario;
import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionFilterParameter;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.execution.impl.RunExecution;
import sme.perf.request.entity.TestRequest;
import sme.perf.ta.dao.GetExecutionIds;
import sme.perf.ta.enetity.GeneratedRequestParams;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;



@RestController
@RequestMapping("/Execution")
public class ExecutionController {

	Logger logger = LogHelper.getLogger();
	
	private ExecutionInfoDao dao;
	public ExecutionController(){
		this.dao = new ExecutionInfoDao();
	}
	
	@RequestMapping("/List")
	public @ResponseBody List<ExecutionInfo> list(){
		//joybean 2016-4-7 remove the sub notes, to reduce the reponse data -> performance
		List<ExecutionInfo> execList = dao.getAll();
		for(ExecutionInfo exec: execList){
			exec.setHostsParameterJson("");
			exec.setProjectParameterJson("");
			exec.setTasksParameterJson("");
			exec.setResultList(null);
			exec.setTasks(null);
		}
		return execList;
	}
	
	@RequestMapping("/ListFinished")
	public @ResponseBody List<ExecutionInfo> listFinished(){
		return  dao.listFinished();
	}
	
	@RequestMapping("/Get/{id}")
	public @ResponseBody ExecutionInfo get(@PathVariable long id){
		return dao.getByID(id);
	}
	
	@RequestMapping("/Duplicate/{id}")
	public @ResponseBody ExecutionInfo duplicate(@PathVariable long id){
		ExecutionInfo baseExecInfo = dao.getByID(id);
		ExecutionInfo newExecInfo = baseExecInfo.duplicateNew();
		List<ExecutionTaskInfo> tasks = newExecInfo.getTasks();
		newExecInfo.setTasks(null);
		newExecInfo = dao.add(newExecInfo);
		
		TaskParameterMap map = new TaskParameterMap();
		JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
		for(ExecutionTaskInfo taskInfo: tasks){
			taskInfo.setExecutionInfoId(newExecInfo.getId());
			//refresh the tasks and refresh the taskParameters 
			taskInfo=taskInfo.refreshExecutionId();
			TaskParameterMap paramMap = jsonHelper.deserialObject(taskInfo.getTaskParameterJson(), TaskParameterMap.class);
			map = map.merge(paramMap);
		}
		newExecInfo.setTasks(tasks);
		
		newExecInfo.setTasksParameterJson(jsonHelper.serializeObject(map));

		dao.update(newExecInfo);
		return dao.getByID(newExecInfo.getId());
	}

	@RequestMapping("/Add")
	public @ResponseBody ExecutionInfo add(@RequestBody ExecutionInfo execInfo){
		execInfo.setCreateDate(DateTime.now());
		execInfo.setState(State.New);
		
		JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
		TaskParameterMap prjParams = jsonHelper.deserialObject(execInfo.getProjectParameterJson(), TaskParameterMap.class);
		TaskParameterMap taskParams = jsonHelper.deserialObject(execInfo.getTasksParameterJson(), TaskParameterMap.class);
		TaskParameterMap hostParams = jsonHelper.deserialObject(execInfo.getHostsParameterJson(), TaskParameterMap.class);
		
		List<ExecutionTaskInfo> taskListBak = execInfo.getTasks();
		execInfo.setTasks(null);
		execInfo = dao.add(execInfo);
		execInfo.setTasks(new ArrayList<ExecutionTaskInfo>());
		
		TaskParameterMap paramMap = taskParams.merge(prjParams);
	
		for(int i=0 ; i<taskListBak.size() ; i++){
			ExecutionTaskInfo task = taskListBak.get(i);
			//set hosts parameters
			task.setTaskHostParameterJson(jsonHelper.serializeObject(hostParams));		
			task.setTaskParameterJson(jsonHelper.serializeObject(paramMap));
			//build connection between execution & tasks
			task.setExecutionInfo(execInfo);
			task.setExecutionInfoId(execInfo.getId());
			execInfo.getTasks().add(task);
		}
		return dao.update(execInfo);
	}
	
	@RequestMapping("/Update")
	public @ResponseBody ExecutionInfo update(@RequestBody ExecutionInfo execInfo){
		//delete the not found 'ExecutionTaskInfo'
		ExecutionInfo existingExecInfo = dao.getByID(execInfo.getId());
		GenericDao<ExecutionTaskInfo> execTaskInfoDao = new GenericDao<ExecutionTaskInfo>(ExecutionTaskInfo.class);
		existingExecInfo.getTasks().removeAll(execInfo.getTasks());
		for(ExecutionTaskInfo taskInfo: existingExecInfo.getTasks()){
			execTaskInfoDao.delete(taskInfo);
		}
		
		JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
		TaskParameterMap prjParams = jsonHelper.deserialObject(execInfo.getProjectParameterJson(), TaskParameterMap.class);
		TaskParameterMap taskParams = jsonHelper.deserialObject(execInfo.getTasksParameterJson(), TaskParameterMap.class);
		TaskParameterMap hostParams = jsonHelper.deserialObject(execInfo.getHostsParameterJson(), TaskParameterMap.class);
		
		TaskParameterMap paramMap = taskParams.merge(prjParams);
		
		for(int i=0 ; i<execInfo.getTasks().size() ; i++){
			ExecutionTaskInfo task = execInfo.getTasks().get(i);
			//set hosts parameters
			task.setTaskHostParameterJson(jsonHelper.serializeObject(hostParams));
			task.setTaskParameterJson(jsonHelper.serializeObject(paramMap));
			//build connection between execution & tasks
			task.setExecutionInfoId(execInfo.getId());
		}
		
		return dao.update(execInfo);
	}
	
	Map<Long, Execution> executionMap = new HashMap<Long, Execution>();
	
	@RequestMapping("/Stop/{executionId}")
	public @ResponseBody String stop(@PathVariable Long executionId){
		Execution execution = executionMap.get(executionId);
		String strStopResult = "";
		if(execution != null ){
			if(execution.getState() == State.Running){
				execution.Stop();
				strStopResult = String.format("Execution:%d is stopped.", executionId);
			}
			else{
				strStopResult = String.format("Execution:%d is not stopped, as it's not running", executionId);
			}
		}
		else{
			strStopResult = String.format("Execution:%d is not found.", executionId);
		}
		logger.info(strStopResult);
		return strStopResult;
	}
	
	
	@RequestMapping("/Pause/{executionId}")
	public @ResponseBody String pause(@PathVariable Long executionId){
		Execution execution = executionMap.get(executionId);
		ExecutionInfo info = dao.getByID(executionId);
		String strStopResult = "";
		if(execution != null){
			if(execution.getState() == State.Running){
				execution.Pause();
				info.setState(State.Paused);
				dao.update(info);
				strStopResult = String.format("Execution:%d is paused.", executionId);
			}
			else {
				strStopResult = String.format("Execution:%d is not paused, as it's not running", executionId);
			}
		}
		else{
			strStopResult = String.format("Execution:%d is not found.", executionId);
		}
		logger.info(strStopResult);
		return strStopResult;
	}
	
	@RequestMapping("/Continue/{executionId}")
	public @ResponseBody String doContinue(@PathVariable Long executionId){
		Execution execution = executionMap.get(executionId);
		ExecutionInfo info = dao.getByID(executionId);
		String strStopResult = "";
		if(execution != null){
			if(execution.getState() == State.Paused){
				execution.Continue();
				info.setState(State.Running);
				dao.update(info);
				strStopResult = String.format("Execution:%d is continued.", executionId);
			}
			else{
				strStopResult = String.format("Execution:%d is not continued, as it's not running.", executionId);
			}
		}
		else{
			strStopResult = String.format("Execution:%d is not found.", executionId);
		}
		logger.info(strStopResult);
		return strStopResult;
	}
	
	@RequestMapping("/Run/{executionId}")
	public @ResponseBody ExecutionInfo run(@PathVariable long executionId){
		ExecutionInfo info = dao.getByID(executionId);
		if(info.getState() == State.New && executionMap.get(executionId) == null){
			info.setState(State.Ready);
			dao.update(info);
			Execution runExec = new RunExecution();
			executionMap.put(executionId, runExec);
			int timeout = info.getTimeOut();
			
			try{
				Thread runThread = new Thread(){
					public void run(){
						runExec.Execute(info);
						dao.update(info);
					}
				};	
				
				runThread.start();
				int count=0;
				while(count<timeout){
					Thread.sleep(1000);
					if(info.getState() == State.Finished||info.getState() == State.Failed||info.getState() == State.Terminated){
						break;
					}
					count++;
				}
				if(count>=timeout){
					runThread.stop();
					logger.error("The thread"+runThread.getId()+" is timeout and be terminated!");
					info.setState(State.Terminated);
					dao.update(info);
				}
			}
			catch(Exception ex){
				LogHelper.error(ex);
			}
		}
		return info;
	}
	
	@RequestMapping("/Export/{executionId}")
	public String exportToFile(@PathVariable long executionId){
		ExecutionInfo info = dao.getByID(executionId);
		ObjectMapper mapper = new ObjectMapper();
		String strJson = "";
		try {
			strJson = mapper.writeValueAsString(info); 
					//mapper.writeValue(new File("C:\\Git_Repo\\com.sap.sbo.occ.ta\\performance\\others\\PerfTestCenter\\src\\test\\java\\sme\\perf\\Execution_17.json"), info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strJson;
	}
	
	@RequestMapping("/Filter")
	public  @ResponseBody List<ExecutionInfo>  filterExecution(@RequestBody ExecutionFilterParameter params){
		return dao.filter(params);
	}
	
	
}
