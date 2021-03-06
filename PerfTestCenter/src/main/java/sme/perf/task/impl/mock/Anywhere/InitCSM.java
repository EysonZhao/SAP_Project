package sme.perf.task.impl.mock.Anywhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sme.perf.entity.Host;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.ITask;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;

public class InitCSM extends Task {

	TaskParameterMap parameters;
	TaskParameterMap hosts;
	Status status;
	@Override
	public Result execute() {
		status = Status.Running;
		LogHelper.info("Executing Install B1 Client.");
//		try {
//			Thread.sleep(new Random().nextInt(5) * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		status = Status.Finished;
		return Result.Pass;
	}

	@Override
	public Status getStatus() {
		return Status.Finished;
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		if(null == this.parameters){
			this.parameters = InitCSM.getParameterTemplate();
		}
		return this.parameters;
	}

	@Override
	public String getDescription() {
		return InitCSM.getDescriptionTemplate();
	}
	

	public static TaskParameterMap getParameterTemplate(){
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		parameters.put("BuildPackage", new TaskParameterEntry("", "\\\\10.58.136.102\\Shared\\Build\\xxx\\Upgrade.rar"));
		parameters.put("Platform", new TaskParameterEntry("x64", "x86/x64"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate(){
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> hostList = new ArrayList<Host>();
		hostList.add(new Host("H001", "10.58.136.1", "administrator", "Initial0", "host 001"));
		hostsParameter.put("csm", new TaskParameterEntry( hostList, "host to be installed with CSM"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}
	
	public static String getDescriptionTemplate(){
		return "This task is to install b1 client";
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		this.hosts = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		return this.hosts;
	}
}
