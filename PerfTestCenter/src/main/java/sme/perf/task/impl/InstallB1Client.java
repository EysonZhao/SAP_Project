package sme.perf.task.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sme.perf.entity.Host;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.ITask;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;

public class InstallB1Client extends Task{

	TaskParameterMap parameters;
	TaskParameterMap hosts;
	
	@Override
	public Result execute() {
		///remove default C:\\TA folder
		///create a folder C:\\TA\\Build for copy B1 client installation package
		///uninstall existing B1 client
		///copy installation package to default build folder
		///install B1 client 
		LogHelper.info("Execute Install B1 Client.");
		return Result.Pass;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		// TODO Auto-generated method stub
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		// TODO Auto-generated method stub
		if(null == this.parameters){
			this.parameters = InstallB1Client.getParameterTemplate();
		}
		return this.parameters;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return InstallB1Client.getDescriptionTemplate();
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
		hostsParameter.put("b1Clients", new TaskParameterEntry( hostList, "host to be installed with B1 client"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}
	
	public static String getDescriptionTemplate(){
		return "This task is to install b1 client";
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		// TODO Auto-generated method stub
		this.hosts = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		// TODO Auto-generated method stub
		return this.hosts;
	}
}
