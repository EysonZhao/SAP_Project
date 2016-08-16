package sme.perf.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionGroupInfo;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.execution.impl.RunExecution;
import sme.perf.request.entity.TestRequest;
import sme.perf.ta.dao.GetExecutionIds;
import sme.perf.ta.enetity.GeneratedRequestParams;
import sme.perf.task.SameTaskParams;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;


class ExecutionThread extends Thread {
	private Execution exe;
	private ExecutionInfo execInfo;

	public ExecutionThread(Execution exe1, ExecutionInfo execInfo1) {
		this.exe = exe1;
		this.execInfo = execInfo1;
	}

	@Override
	public void run() {
		exe.Execute(execInfo);
	}

	public void shutdown() {
		execInfo.setState(sme.perf.execution.State.Terminated);
		exe.Stop();
		interrupt();
	}
}

@RestController
@RequestMapping("/ExecutionGroup")
public class ExecutionGroupController {

	Logger logger = LogHelper.getLogger();
	GenericDao<ExecutionGroupInfo> dao;

	public ExecutionGroupController() {
		dao = new GenericDao<ExecutionGroupInfo>(ExecutionGroupInfo.class);
	}

	@RequestMapping("/List")
	public @ResponseBody List<ExecutionGroupInfo> list() {
		return dao.getAll();
	}

	@RequestMapping("/Get/{id}")
	public @ResponseBody ExecutionGroupInfo get(@PathVariable long id) {
		return dao.getByID(id);
	}

	@RequestMapping("/Add")
	public @ResponseBody ExecutionGroupInfo add(@RequestBody ExecutionGroupInfo execGroupInfo) {
		execGroupInfo.setState(State.New);
		return dao.add(execGroupInfo);
	}

	@RequestMapping("/Update")
	public @ResponseBody ExecutionGroupInfo update(@RequestBody ExecutionGroupInfo execGroupInfo) {
		return dao.update(execGroupInfo);
	}

	Map<Long, RunExecutionGroupThread> executionGroupThreadMap = new HashMap<Long, RunExecutionGroupThread>();

	@RequestMapping("/Run/{executionGroupId}")
	public @ResponseBody ExecutionGroupInfo run(@PathVariable long executionGroupId) {
		if (null == executionGroupThreadMap.get((Long) executionGroupId)) {
			ExecutionGroupInfo execInfo = dao.getByID(executionGroupId);
			if (execInfo.getState() == sme.perf.execution.State.New) {
				RunExecutionGroupThread newExecThread = new RunExecutionGroupThread(executionGroupId);
				executionGroupThreadMap.put((Long) executionGroupId, newExecThread);
				newExecThread.start();
			}
			return execInfo;
		} else {
			return dao.getByID(executionGroupId);
		}
	}

	@RequestMapping("/Stop/{executionGroupId}")
	public @ResponseBody ExecutionGroupInfo stop(@PathVariable long executionGroupId) {
		RunExecutionGroupThread execThread = executionGroupThreadMap.get((Long) executionGroupId);
		if (null == execThread)
			return null;
		// as the execution thread is really simple. not critical lock need to
		// release. so just stop it.
		if (execThread.isAlive()) {
			execThread.stop();
		}
		ExecutionGroupInfo execGroupInfo = dao.getByID(executionGroupId);
		execGroupInfo.setState(sme.perf.execution.State.Terminated);
		return dao.update(execGroupInfo);
	}

	class RunExecutionGroupThread extends Thread {
		long executonGroupId;
		ExecutionGroupInfo execGroupInfo;

		public RunExecutionGroupThread(long execGroupId) {
			this.executonGroupId = execGroupId;
			this.execGroupInfo = dao.getByID(executonGroupId);
		}

		public void run() {
			if (execGroupInfo.getState() != sme.perf.execution.State.New)
				return;
			execGroupInfo.setState(sme.perf.execution.State.Running);
			dao.update(execGroupInfo);

			String[] idList = this.execGroupInfo.getExecutionIdList().split(";");
			ExecutionInfoDao execInfoDao = new ExecutionInfoDao();

			int timeout = execGroupInfo.getTimeout();

			for (int i = 0; i < idList.length; i++) {
				long execId = Long.parseLong(idList[i]);
				ExecutionInfo execInfo = execInfoDao.getByID(execId);
				ExecutionThread exeThread = null;
				if (execInfo.getState() == sme.perf.execution.State.New) {
					Execution runExec = new RunExecution();
					exeThread = new ExecutionThread(runExec, execInfo);
					exeThread.start();
				}
				int count = 0;
				while (count++ < timeout) {
					try {
						Thread.sleep(1000);
						if ((execInfo.getState() == sme.perf.execution.State.Finished)
								|| (execInfo.getState() == sme.perf.execution.State.Failed)
								|| (execInfo.getState() == sme.perf.execution.State.Terminated)) {
							break;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // end while
				if (count >= timeout) {
					exeThread.shutdown();
				}
			}

			execGroupInfo.setState(sme.perf.execution.State.Finished);
			dao.update(execGroupInfo);
		}
	}

	@RequestMapping("/Getparam/{executionGroupId}")
	public @ResponseBody List<SameTaskParams> getSameParams(@PathVariable long executionGroupId) {
		ExecutionGroupInfo oldExecGroupInfo = dao.getByID(executionGroupId);
		String oldExecutionList = oldExecGroupInfo.getExecutionIdList();
		long ids[] = getOldExecListIds(oldExecutionList);

		ExecutionInfoDao mydao = new ExecutionInfoDao();
		Map<String, Object> same = new HashMap<String, Object>();
		for (int i = 0; i < ids.length; i++) {
			ExecutionInfo exe = mydao.getByID(ids[i]);

			JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
			TaskParameterMap taskParams = jsonHelper.deserialObject(exe.getTasksParameterJson(),
					TaskParameterMap.class);

			Iterator it = taskParams.getParameters().entrySet().iterator();
			if (i == 0) {
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					String key = (String) me.getKey();
					Object value = ((TaskParameterEntry) me.getValue()).getValue();

					same.put(key, value);
				}
			} else {
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					String key = (String) me.getKey();
					Object value = ((TaskParameterEntry) me.getValue()).getValue();
					if (same.containsKey(key) && !(same.get(key).equals(value))) {
						same.remove(key);
					}
				}
			}
		}

		List<SameTaskParams> samelist = new ArrayList<SameTaskParams>();
		Iterator it = same.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			String key = (String) me.getKey();
			Object value = me.getValue();
			SameTaskParams param = new SameTaskParams(key, value);
			samelist.add(param);
		}

		return samelist;
	}

	@RequestMapping("/Modifyparams/{executionGroupId}")
	public @ResponseBody List<SameTaskParams> modifyParams(@PathVariable long executionGroupId,
			@RequestBody List<Object> requestlist) {
		// long executionGroupId=15;
		ExecutionGroupInfo oldExecGroupInfo = dao.getByID(executionGroupId);
		String oldExecutionList = oldExecGroupInfo.getExecutionIdList();
		long ids[] = getOldExecListIds(oldExecutionList);

		ExecutionInfoDao mydao = new ExecutionInfoDao();

		List<SameTaskParams> finallist = new ArrayList<SameTaskParams>();
		for (Object params : requestlist) {
			SameTaskParams s = new SameTaskParams(params.toString());
			finallist.add(s);
		}

		for (int i = 0; i < ids.length; i++) {
			ExecutionInfo exe = mydao.getByID(ids[i]);

			JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
			TaskParameterMap taskParams = jsonHelper.deserialObject(exe.getTasksParameterJson(),
					TaskParameterMap.class);

			for (SameTaskParams params : finallist) {
				taskParams.getParameters().get(params.getName()).setValue(params.getValue());
			}
			String json = jsonHelper.serializeObject(taskParams);
			exe.setTasksParameterJson(json);
			for (int j = 0; j < exe.getTasks().size(); j++) {
				ExecutionTaskInfo task = exe.getTasks().get(j);
				task.setTaskParameterJson(jsonHelper.serializeObject(taskParams));
			}
			mydao.update(exe);
		}

		return finallist;

	}

	@RequestMapping("/Duplicate/{executionGroupId}")
	public @ResponseBody ExecutionGroupInfo duplicate(@PathVariable long executionGroupId) {
		// return (ExecutionGroupInfo)dao.getByID(executionGroupId).clone();
		ExecutionGroupInfo newExecGroupInfo = new ExecutionGroupInfo();
		ExecutionGroupInfo oldExecGroupInfo = dao.getByID(executionGroupId);

		ExecutionController execCtrler = new ExecutionController();
		String oldExecutionList = oldExecGroupInfo.getExecutionIdList();
		String newExecutionList = new String();

		long oldExeListIds[] = getOldExecListIds(oldExecutionList);
		long newExeListIds[] = new long[oldExeListIds.length];

		for (int i = 0; i < oldExeListIds.length; i++) {
			ExecutionInfo temp = execCtrler.duplicate(oldExeListIds[i]);
			newExeListIds[i] = temp.getId();
		}

		newExecutionList = getNewExecutionList(newExeListIds);

		newExecGroupInfo.setExecutionIdList(newExecutionList);
		newExecGroupInfo.setState(State.New);
		newExecGroupInfo.setName(createDefaultNewName(oldExecGroupInfo.getName()));
		dao.add(newExecGroupInfo);

		return newExecGroupInfo;
	}
	/***
	 * Overload for daily TA, Added by Yansong at August 3
	 * @param executionGroupId
	 * @param requestId
	 * @return
	 */
	public ExecutionGroupInfo duplicate(long executionGroupId,long requestId) {
		// return (ExecutionGroupInfo)dao.getByID(executionGroupId).clone();
		ExecutionGroupInfo newExecGroupInfo = new ExecutionGroupInfo();
		ExecutionGroupInfo oldExecGroupInfo = dao.getByID(executionGroupId);

		ExecutionController execCtrler = new ExecutionController();
		String oldExecutionList = oldExecGroupInfo.getExecutionIdList();
		String newExecutionList = new String();

		long oldExeListIds[] = getOldExecListIds(oldExecutionList);
		long newExeListIds[] = new long[oldExeListIds.length];
		ExecutionInfoDao exedao = new ExecutionInfoDao();
		for (int i = 0; i < oldExeListIds.length; i++) {
			ExecutionInfo temp = execCtrler.duplicate(oldExeListIds[i]);
			temp.setTestRequestId(requestId);
			exedao.update(temp);
			newExeListIds[i] = temp.getId();
		}

		newExecutionList = getNewExecutionList(newExeListIds);

		newExecGroupInfo.setExecutionIdList(newExecutionList);
		newExecGroupInfo.setState(State.New);
		newExecGroupInfo.setName(createDefaultNewName(oldExecGroupInfo.getName()));
		dao.add(newExecGroupInfo);

		return newExecGroupInfo;
	}
	
	/************ TA daily benchmark request ************/	
	@RequestMapping("/TADaily")
	public @ResponseBody TestRequest GenerateRequest(@RequestBody GeneratedRequestParams param){
		TestRequest request = new TestRequest();
		GenericDao<TestRequest> dao = new GenericDao<TestRequest>(TestRequest.class);
		
		String category = param.getCategory();
		request.setBuild(String.valueOf(param.getBuild()));
		request.setCodeline(param.getCodeline());
		request.setPLNumber(param.getPlnumber());
		request.setCLNumber(param.getClnumber());
		request.setHana(param.isHana());
		request.setBrowserAccess(param.isBa());
		request.setName(param.getCodeline()+" "+param.getPlnumber()+" CL "+param.getClnumber());
		request.setCategory(category);
		request.setPriority("Medium");
		request.setRequester("Daily TA");
		request.setTestOwner("PerfTestCenter Auto");	
		request.setLatestStatus("New");
		request.setProjectId(param.getProjectid());

		DateTime now = DateTime.now();
		request.setLastUpdateDate(new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0));
		
		dao.add(request);
		/******* Start a new thread to get the executions and get them to run ********/
		
		long exeids = (new GetExecutionIds().getExecutionIds(category)).get(0).getExecutionTemplateIds();
		
		ExecutionGroupInfo newExecInfo = this.duplicate(exeids,request.getId());		
		
		final long runId = newExecInfo.getId();
		ExecutionGroupController that = this;
		Thread runThread = new Thread(){
			public void run(){
				that.run(runId);
			}
		};	 
		runThread.start();

		return request;
	}

	//the code above was added by Yansong in Aug,2nd
	

	private static String createDefaultNewName(String oldName) {
		Pattern p = Pattern.compile("#(\\d*).*$");
		Matcher m = p.matcher(oldName);
		StringBuffer time = new StringBuffer("[");
		DateTime now = DateTime.now();
		int month = now.getMonthOfYear();
		int day = now.getDayOfMonth();
		int hour = now.getHourOfDay();
		int minute = now.getMinuteOfHour();
		fommatTime(month, time);
		fommatTime(day, time);
		time.append("-");
		fommatTime(hour, time);
		fommatTime(minute, time);
		time.append("]");

		if (m.find()) {
			String part = oldName.substring(oldName.lastIndexOf('#') + 1, oldName.length());
			int num = Integer.parseInt(part.split(":")[0]);

			return String.format("%s#%d:%s", oldName.substring(0, oldName.lastIndexOf('#')), (num + 1), time);
		} else {
			return oldName + "#1:" + time;
		}
	}

	private static void fommatTime(int n, StringBuffer sb) {
		if (n < 10) {
			sb.append(0);
		}
		sb.append(n);
	}

	private long[] getOldExecListIds(String src) {
		String[] des = src.split(";");
		int len = des.length;
		long[] result = new long[len];

		for (int i = 0; i < len; i++) {
			result[i] = Long.parseLong(des[i]);
		}
		return result;
	}

	private String getNewExecutionList(long ids[]) {
		StringBuffer sb = new StringBuffer();
		for (long id : ids) {
			sb.append(id);
			sb.append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	
}
