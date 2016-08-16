package sme.perf.task.impl.anywhere.p1601;

import java.util.ArrayList;
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

public class RestartService extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String mainfolder = parameters.getValue("mainfolder").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		
		logger.info("Start to Restart Service.");
		try {				
			String restartServiceCmd = "cd " + mainfolder + " && ./AnywhereRestart.sh && sleep 10";
			for (Host host : allMachine) {
				logger.info("Current machine:" + host.getIP());
				RunRemoteSSH rrs=new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(restartServiceCmd);
				
				String checkJavaFlag="0";
				while (Integer.parseInt(checkJavaFlag.trim())==0){
					String checkJavaCmd="ps -efH | grep java | grep -v grep |wc -l";
					checkJavaFlag=rrs.executeWithReturnString(checkJavaCmd).replace("\r", "").replace("\n", "");
					if(Integer.parseInt(checkJavaFlag.trim())==0){
						rrs.execute(restartServiceCmd);
					}
				}		
			}
			logger.info("Finish to Restart Service.");
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
		if (null == this.parameters) {
			this.parameters = RestartService.getParameterTemplate();
		}
		return this.parameters;
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
		if (null == this.hostParameters) {
			this.hostParameters = RestartService.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Restart Service.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> CSMhostList = new ArrayList<Host>();
		CSMhostList.add(new Host("cnpvgvb1pf009.pvgl.sap.corp", "10.58.8.43", "root",
				"Initial0", "CSM Server"));
		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,
				"Host List for CSM,SP,IDP,etc."));
		List<Host> occhostList = new ArrayList<Host>();
		occhostList.add(new Host("cnpvgvb1pf007.pvgl.sap.corp", "10.58.8.41", "root",
				"Initial0", "OCC Server 1"));
		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,
				"Host List for OCC Machine"));
		List<Host> jobhostList = new ArrayList<Host>();
		jobhostList.add(new Host("cnpvgvb1pf008.pvgl.sap.corp", "10.58.8.42", "root",
				"Initial0", "Job Server 1"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
				"Host List for Job Machine"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new RestartService().getDescription();
	}
	
}
