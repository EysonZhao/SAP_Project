package sme.perf.task.impl.anywhere.p1601;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.RunRemoteSSH;

public class EshopSizingTestPreparation extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status=Status.Running;
		
		logger.info("Start to Run Single Case.");
		try {
			//TODO Eshop Preparation Steps
//			String PrepareCmd ="cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
//					+ windowsJava
//					+ "\" && cd "
//					+ windowsFolder
//					+ "\\JmeterScript\\TestCase && call ..\\..\\Jmeter\\bin\\jmeter -n -t "
//					+ jmeterScriptName
//					+ "Parameters~~~~~~~";

			//RunRemoteSSH.execute(windowsServer.getIP(), windowsServer.getUserName(), windowsServer.getUserPassword(), PrepareCmd);
			
			logger.info("Finish to Run Single Case.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}

	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		if (null == parameters) {
			parameters = EshopSizingTestPreparation.getParameterTemplate();
		}
		return parameters;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		this.hostParameters = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		if (null == hostParameters) {
			hostParameters = EshopSizingTestPreparation.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Run Eshop Preparation step.";
	}
	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
		TaskParameterMap template=new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new EshopSizingTestPreparation().getDescription();
	}
	
}
